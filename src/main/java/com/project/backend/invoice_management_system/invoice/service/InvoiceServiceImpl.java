package com.project.backend.invoice_management_system.invoice.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.common.util.AmountInWordsUtil;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.document.service.NumberSequenceService;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceRequest;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceResponse;
import com.project.backend.invoice_management_system.invoice.model.Invoice;
import com.project.backend.invoice_management_system.invoice.model.InvoiceItem;
import com.project.backend.invoice_management_system.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final NumberSequenceService numberSequenceService;
    private final AmountInWordsUtil amountInWordsUtil;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    @Transactional // Ensures this entire operation is atomic
    public InvoiceResponse createInvoice(InvoiceRequest request, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // 1. Get the next invoice number
        String invoiceNumber = numberSequenceService.getNextInvoiceNumber(company);

        // 2. Build the line items and calculate Total Amount Before Tax
        BigDecimal totalAmountBeforeTax = BigDecimal.ZERO;
        List<InvoiceItem> items = request.getItems().stream()
                .map(itemDto -> {
                    BigDecimal amount = itemDto.getQuantity().multiply(itemDto.getRate())
                            .setScale(2, RoundingMode.HALF_UP);

                    return InvoiceItem.builder()
                            .description(itemDto.getDescription())
                            .hsnCode(itemDto.getHsnCode())
                            .uom(itemDto.getUom())
                            .quantity(itemDto.getQuantity())
                            .rate(itemDto.getRate())
                            .amount(amount)
                            .build();
                })
                .collect(Collectors.toList());

        // Sum the amounts from the items
        for (InvoiceItem item : items) {
            totalAmountBeforeTax = totalAmountBeforeTax.add(item.getAmount());
        }

        // 3. Perform Tax Logic (as per your rules)
        String companyStateCode = company.getCode();
        String clientStateCode = request.getBilledToCode();

        BigDecimal cgstRate = BigDecimal.ZERO;
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstRate = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        BigDecimal igstRate = BigDecimal.ZERO;
        BigDecimal igstAmount = BigDecimal.ZERO;

        if (companyStateCode != null && companyStateCode.equals(clientStateCode)) {
            // INTRA-STATE
            cgstRate = request.getCgstRate() != null ? request.getCgstRate() : BigDecimal.ZERO;
            sgstRate = request.getSgstRate() != null ? request.getSgstRate() : BigDecimal.ZERO;

            cgstAmount = totalAmountBeforeTax.multiply(cgstRate.divide(ONE_HUNDRED))
                    .setScale(2, RoundingMode.HALF_UP);
            sgstAmount = totalAmountBeforeTax.multiply(sgstRate.divide(ONE_HUNDRED))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            // INTER-STATE
            igstRate = request.getIgstRate() != null ? request.getIgstRate() : BigDecimal.ZERO;
            igstAmount = totalAmountBeforeTax.multiply(igstRate.divide(ONE_HUNDRED))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalTaxAmount = cgstAmount.add(sgstAmount).add(igstAmount);
        BigDecimal totalAmountAfterTax = totalAmountBeforeTax.add(totalTaxAmount);

        // 4. Convert total to words
        String totalInWords = amountInWordsUtil.convertToWords(totalAmountAfterTax);

        // 5. Build the main Invoice entity (Snapshotting all data)
        Invoice invoice = Invoice.builder()
                .company(company)
                .invoiceNumber(invoiceNumber)
                .invoiceDate(request.getInvoiceDate())
                .transportMode(request.getTransportMode())
                .vehicleNo(request.getVehicleNo())
                .dateOfSupply(request.getDateOfSupply())
                .placeOfSupply(request.getPlaceOfSupply())
                .orderNumber(request.getOrderNumber())
                .taxOnReverseCharge(request.getTaxOnReverseCharge())
                .grLrNo(request.getGrLrNo())
                .eWayBillNo(request.getEWayBillNo())
                .billedToName(request.getBilledToName())
                .billedToAddress(request.getBilledToAddress())
                .billedToGstin(request.getBilledToGstin())
                .billedToState(request.getBilledToState())
                .billedToCode(request.getBilledToCode())
                .shippedToName(request.getShippedToName())
                .shippedToAddress(request.getShippedToAddress())
                .shippedToGstin(request.getShippedToGstin())
                .shippedToState(request.getShippedToState())
                .shippedToCode(request.getShippedToCode())
                .items(items) // Set the list of items
                .totalAmountBeforeTax(totalAmountBeforeTax)
                .cgstRate(cgstRate)
                .cgstAmount(cgstAmount)
                .sgstRate(sgstRate)
                .sgstAmount(sgstAmount)
                .igstRate(igstRate)
                .igstAmount(igstAmount)
                .totalTaxAmount(totalTaxAmount)
                .totalAmountAfterTax(totalAmountAfterTax)
                .totalAmountInWords(totalInWords)
                .selectedBankName(request.getSelectedBankName())
                .selectedAccountName(request.getSelectedAccountName())
                .selectedAccountNumber(request.getSelectedAccountNumber())
                .selectedIfscCode(request.getSelectedIfscCode())
                .jurisdictionCity(company.getCity()) // Pulled from company
                .termsAndConditions(request.getTermsAndConditions())
                .build();

        // 6. Set the back-reference from items to the invoice
        for (InvoiceItem item : items) {
            item.setInvoice(invoice);
        }

        // 7. Save the invoice (items will be saved by cascade)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceToResponse(savedInvoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices(User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        return invoiceRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::invoiceToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Long invoiceId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        return invoiceToResponse(invoice);
    }

    // --- HELPER METHODS ---

    private Company getCompanyFromUser(User user) {
        if (user.getCompany() == null) {
            throw new IllegalStateException("User is not associated with a company.");
        }
        return user.getCompany();
    }

    private InvoiceResponse invoiceToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .companyId(invoice.getCompany().getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .billedToName(invoice.getBilledToName())
                .totalAmountAfterTax(invoice.getTotalAmountAfterTax())
                .pdfUrl(invoice.getPdfUrl()) // Will be null for now
                .build();
    }
}