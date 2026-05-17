package com.project.backend.invoice_management_system.invoice.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.model.Client;
import com.project.backend.invoice_management_system.client.repository.ClientRepository;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.common.util.AmountInWordsUtil;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.document.service.NumberSequenceService;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceRequest;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceResponse;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceDetailResponse;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceItemDto;
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
    private final ClientRepository clientRepository;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        String invoiceNumber = numberSequenceService.getNextInvoiceNumber(company);

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

        for (InvoiceItem item : items) {
            totalAmountBeforeTax = totalAmountBeforeTax.add(item.getAmount());
        }

        String companyStateCode = company.getCode();
        String clientStateCode = request.getBilledToCode();
        BigDecimal cgstRate = BigDecimal.ZERO;
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstRate = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        BigDecimal igstRate = BigDecimal.ZERO;
        BigDecimal igstAmount = BigDecimal.ZERO;

        if (companyStateCode != null && companyStateCode.equals(clientStateCode)) {
            cgstRate = request.getCgstRate() != null ? request.getCgstRate() : BigDecimal.ZERO;
            sgstRate = request.getSgstRate() != null ? request.getSgstRate() : BigDecimal.ZERO;
            cgstAmount = totalAmountBeforeTax.multiply(cgstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
            sgstAmount = totalAmountBeforeTax.multiply(sgstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
        } else {
            igstRate = request.getIgstRate() != null ? request.getIgstRate() : BigDecimal.ZERO;
            igstAmount = totalAmountBeforeTax.multiply(igstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalTaxAmount = cgstAmount.add(sgstAmount).add(igstAmount);
        BigDecimal totalAmountAfterTax = totalAmountBeforeTax.add(totalTaxAmount);
        String totalInWords = amountInWordsUtil.convertToWords(totalAmountAfterTax);

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
                .items(items)
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
                .jurisdictionCity(company.getCity())
                .termsAndConditions(request.getTermsAndConditions())
                .pdfUrl(request.getPdfUrl())
                .status("Unpaid")
                .build();

        for (InvoiceItem item : items) {
            item.setInvoice(invoice);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceToResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices(User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        return invoiceRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::invoiceToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long invoiceId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        return invoiceToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceDetailsById(Long invoiceId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        return InvoiceDetailResponse.builder()
                .id(invoice.getId())
                .companyId(invoice.getCompany().getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .transportMode(invoice.getTransportMode())
                .vehicleNo(invoice.getVehicleNo())
                .dateOfSupply(invoice.getDateOfSupply())
                .placeOfSupply(invoice.getPlaceOfSupply())
                .orderNumber(invoice.getOrderNumber())
                .taxOnReverseCharge(invoice.getTaxOnReverseCharge())
                .grLrNo(invoice.getGrLrNo())
                .eWayBillNo(invoice.getEWayBillNo())
                .billedToName(invoice.getBilledToName())
                .billedToAddress(invoice.getBilledToAddress())
                .billedToGstin(invoice.getBilledToGstin())
                .billedToState(invoice.getBilledToState())
                .billedToCode(invoice.getBilledToCode())
                .shippedToName(invoice.getShippedToName())
                .shippedToAddress(invoice.getShippedToAddress())
                .shippedToGstin(invoice.getShippedToGstin())
                .shippedToState(invoice.getShippedToState())
                .shippedToCode(invoice.getShippedToCode())
                .items(invoice.getItems().stream().map(item ->
                        InvoiceItemDto.builder()
                                .description(item.getDescription())
                                .hsnCode(item.getHsnCode())
                                .uom(item.getUom())
                                .quantity(item.getQuantity())
                                .rate(item.getRate())
                                .build()
                ).collect(java.util.stream.Collectors.toList()))
                .cgstRate(invoice.getCgstRate())
                .cgstAmount(invoice.getCgstAmount())
                .sgstRate(invoice.getSgstRate())
                .sgstAmount(invoice.getSgstAmount())
                .igstRate(invoice.getIgstRate())
                .igstAmount(invoice.getIgstAmount())
                .totalAmountBeforeTax(invoice.getTotalAmountBeforeTax())
                .totalTaxAmount(invoice.getTotalTaxAmount())
                .totalAmountAfterTax(invoice.getTotalAmountAfterTax())
                .totalAmountInWords(invoice.getTotalAmountInWords())
                .selectedBankName(invoice.getSelectedBankName())
                .selectedAccountName(invoice.getSelectedAccountName())
                .selectedAccountNumber(invoice.getSelectedAccountNumber())
                .selectedIfscCode(invoice.getSelectedIfscCode())
                .termsAndConditions(invoice.getTermsAndConditions())
                .jurisdictionCity(invoice.getJurisdictionCity())
                .pdfUrl(invoice.getPdfUrl())
                .build();
    }

    @Override
    @Transactional
    public InvoiceDetailResponse updateInvoice(Long invoiceId, InvoiceRequest request, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        BigDecimal totalAmountBeforeTax = BigDecimal.ZERO;
        List<InvoiceItem> newItems = request.getItems().stream()
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

        for (InvoiceItem item : newItems) {
            totalAmountBeforeTax = totalAmountBeforeTax.add(item.getAmount());
        }

        String companyStateCode = company.getCode();
        String clientStateCode = request.getBilledToCode();

        BigDecimal cgstRate = BigDecimal.ZERO;
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstRate = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        BigDecimal igstRate = BigDecimal.ZERO;
        BigDecimal igstAmount = BigDecimal.ZERO;

        if (companyStateCode != null && companyStateCode.equals(clientStateCode)) {
            cgstRate = request.getCgstRate() != null ? request.getCgstRate() : BigDecimal.ZERO;
            sgstRate = request.getSgstRate() != null ? request.getSgstRate() : BigDecimal.ZERO;
            cgstAmount = totalAmountBeforeTax.multiply(cgstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
            sgstAmount = totalAmountBeforeTax.multiply(sgstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
        } else {
            igstRate = request.getIgstRate() != null ? request.getIgstRate() : BigDecimal.ZERO;
            igstAmount = totalAmountBeforeTax.multiply(igstRate.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalTaxAmount = cgstAmount.add(sgstAmount).add(igstAmount);
        BigDecimal totalAmountAfterTax = totalAmountBeforeTax.add(totalTaxAmount);
        String totalInWords = amountInWordsUtil.convertToWords(totalAmountAfterTax);

        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setTransportMode(request.getTransportMode());
        invoice.setVehicleNo(request.getVehicleNo());
        invoice.setDateOfSupply(request.getDateOfSupply());
        invoice.setPlaceOfSupply(request.getPlaceOfSupply());
        invoice.setOrderNumber(request.getOrderNumber());
        invoice.setTaxOnReverseCharge(request.getTaxOnReverseCharge());
        invoice.setGrLrNo(request.getGrLrNo());
        invoice.setEWayBillNo(request.getEWayBillNo());

        invoice.setBilledToName(request.getBilledToName());
        invoice.setBilledToAddress(request.getBilledToAddress());
        invoice.setBilledToGstin(request.getBilledToGstin());
        invoice.setBilledToState(request.getBilledToState());
        invoice.setBilledToCode(request.getBilledToCode());

        invoice.setShippedToName(request.getShippedToName());
        invoice.setShippedToAddress(request.getShippedToAddress());
        invoice.setShippedToGstin(request.getShippedToGstin());
        invoice.setShippedToState(request.getShippedToState());
        invoice.setShippedToCode(request.getShippedToCode());

        invoice.getItems().clear();
        for (InvoiceItem item : newItems) {
            item.setInvoice(invoice);
        }
        invoice.getItems().addAll(newItems);

        invoice.setTotalAmountBeforeTax(totalAmountBeforeTax);
        invoice.setCgstRate(cgstRate);
        invoice.setCgstAmount(cgstAmount);
        invoice.setSgstRate(sgstRate);
        invoice.setSgstAmount(sgstAmount);
        invoice.setIgstRate(igstRate);
        invoice.setIgstAmount(igstAmount);
        invoice.setTotalTaxAmount(totalTaxAmount);
        invoice.setTotalAmountAfterTax(totalAmountAfterTax);
        invoice.setTotalAmountInWords(totalInWords);

        invoice.setSelectedBankName(request.getSelectedBankName());
        invoice.setSelectedAccountName(request.getSelectedAccountName());
        invoice.setSelectedAccountNumber(request.getSelectedAccountNumber());
        invoice.setSelectedIfscCode(request.getSelectedIfscCode());
        invoice.setJurisdictionCity(company.getCity());
        invoice.setTermsAndConditions(request.getTermsAndConditions());

        if (request.getPdfUrl() != null) {
            invoice.setPdfUrl(request.getPdfUrl());
        }

        Invoice saved = invoiceRepository.save(invoice);
        return getInvoiceDetailsById(saved.getId(), currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByClientId(Long clientId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        Client client = clientRepository.findByIdAndCompanyId(clientId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return invoiceRepository.findByBilledToNameAndCompanyId(client.getClientName(), company.getId())
                .stream()
                .map(this::invoiceToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceResponse markAsPaid(Long invoiceId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        invoice.setStatus("Paid");
        Invoice saved = invoiceRepository.save(invoice);
        return invoiceToResponse(saved);
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
                .pdfUrl(invoice.getPdfUrl())
                .status(invoice.getStatus() != null ? invoice.getStatus() : "Unpaid")
                .items(invoice.getItems() != null ? invoice.getItems().stream().map(item ->
                        InvoiceItemDto.builder()
                                .description(item.getDescription())
                                .hsnCode(item.getHsnCode())
                                .uom(item.getUom())
                                .quantity(item.getQuantity())
                                .rate(item.getRate())
                                .build()
                ).collect(Collectors.toList()) : null)
                .build();
    }
}