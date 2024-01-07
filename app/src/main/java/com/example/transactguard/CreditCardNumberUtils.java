package com.example.transactguard;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.widget.EditText;

public class CreditCardNumberUtils {

    public static void setCreditCardNumberFilter(EditText editText) {
        int maxLength = 16;

        // Set the maximum length of the EditText
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });

        // Add a custom InputFilter to restrict input to numeric digits only
        editText.setKeyListener((KeyListener) new DigitsKeyListener());

        // Optionally, you can add a TextWatcher to format the credit card number as the user types
        // editText.addTextChangedListener(new CreditCardTextWatcher());
    }

    private static class DigitsKeyListener implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Allow only numeric digits
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null; // Accept the input
        }
    }

    // Optionally, you can add a TextWatcher to format the credit card number as the user types
    /*
    private static class CreditCardTextWatcher implements TextWatcher {
        // Implement formatting logic as needed
        // For example, you can add spaces after every 4 digits
    }
    */
}

