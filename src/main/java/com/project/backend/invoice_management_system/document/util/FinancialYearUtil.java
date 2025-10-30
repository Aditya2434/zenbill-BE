package com.project.backend.invoice_management_system.document.util;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.Month;

@Component
public class FinancialYearUtil {

    /**
     * Gets the current financial year (e.g., "2025-2026")
     * based on the current date.
     */
    public String getCurrentFinancialYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        // In India, financial year starts April 1st
        if (now.getMonth().getValue() < Month.APRIL.getValue()) {
            // We are in Jan, Feb, Mar - so we are in the *previous* financial year
            return (year - 1) + "-" + year;
        } else {
            // We are in Apr-Dec
            return year + "-" + (year + 1);
        }
    }
}