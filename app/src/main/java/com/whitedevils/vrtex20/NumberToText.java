package com.whitedevils.vrtex20;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberToText {

    private static final String[] ONES = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    private static final String[] TEENS = {"", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] TENS = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    private static final String[] THOUSANDS = {"", "Thousand", "Lakh", "Crore"};

    public static String convertToIndianCurrency(String num) {
        BigDecimal bd = new BigDecimal(num);
        long number = bd.longValue();
        int decimal = bd.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).intValue();

        StringBuilder result = new StringBuilder();

        if (number == 0 && decimal == 0) {
            return "Zero Rupees Only";
        }

        List<String> parts = new ArrayList<>();
        int partIndex = 0;

        while (number > 0) {
            int hundreds = (int) (number % 1000);
            if (hundreds != 0) {
                parts.add(convertLessThanThousand(hundreds) + " " + THOUSANDS[partIndex]);
            }
            number /= 1000;
            partIndex++;
        }

        // Reverse the parts
        for (int i = parts.size() - 1; i >= 0; i--) {
            result.append(parts.get(i)).append(" ");
        }

        if (decimal > 0) {
            result.append(convertLessThanThousand(decimal)).append(" Paise ");
        }

        return result.append("Only").toString();
    }

    private static String convertLessThanThousand(int number) {
        StringBuilder result = new StringBuilder();

        if (number >= 100) {
            result.append(ONES[number / 100]).append(" Hundred ");
            number %= 100;
        }

        if (number >= 11 && number <= 19) {
            result.append(TEENS[number - 10]);
            return result.toString();
        } else if (number == 10 || number >= 20) {
            result.append(TENS[number / 10]).append(" ");
            number %= 10;
        }

        if (number > 0) {
            result.append(ONES[number]);
        }

        return result.toString();
    }
}







