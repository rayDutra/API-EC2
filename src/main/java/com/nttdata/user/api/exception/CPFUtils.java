package com.nttdata.user.api.exception;

public class CPFUtils {

    public static boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return false;
        }

        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Integer.parseInt(cpf.substring(i, i + 1));
        }

        if (allDigitsAreEqual(digits)) {
            return false;
        }

        int sum = calculateSum(digits, 10, 9);
        int digit1 = calculateDigit(sum);

        sum = calculateSum(digits, 11, 10);
        int digit2 = calculateDigit(sum);

        return digits[9] == digit1 && digits[10] == digit2;
    }

    private static boolean allDigitsAreEqual(int[] digits) {
        for (int i = 1; i < digits.length; i++) {
            if (digits[i] != digits[0]) {
                return false;
            }
        }
        return true;
    }

    private static int calculateSum(int[] digits, int startWeight, int end) {
        int sum = 0;
        int weight = startWeight;
        for (int i = 0; i < end; i++) {
            sum += digits[i] * weight;
            weight--;
        }
        return sum;
    }

    private static int calculateDigit(int sum) {
        int remainder = sum % 11;
        return (remainder < 2) ? 0 : (11 - remainder);
    }
}
