package com.gubadev.soaapp.util;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class Util {

    public static Boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String getValue(TextInputEditText inputText) {
        return inputText.getText().toString().trim();
    }

    public static String getValue(EditText editText) {
        return editText.getText().toString().trim();
    }
}
