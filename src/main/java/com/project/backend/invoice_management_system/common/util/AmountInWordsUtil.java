package com.project.backend.invoice_management_system.common.util;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
public class AmountInWordsUtil {

    // A simple (and limited) implementation.
    // For a real production app, you'd use a more robust library.

    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private String convert(final long n) {
        if (n < 0) {
            return "Minus " + convert(-n);
        }
        if (n < 20) {
            return units[(int) n];
        }
        if (n < 100) {
            return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(int) (n % 10)];
        }
        if (n < 1000) {
            return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }
        if (n < 100000) {
            return convert(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
        }
        if (n < 10000000) {
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        }
        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }

    public String convertToWords(BigDecimal amount) {
        if (amount == null) return "";

        long number = amount.longValue();
        long paisa = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).longValue();

        String rupeeWords = convert(number);
        String paisaWords = (paisa > 0) ? " and " + convert(paisa) + " Paisa" : "";

        return "INR " + rupeeWords + " Rupees" + paisaWords + " Only";
    }
}