package com.example.aiderchat_proj.utils;

import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BindingAdapters {

    public interface Validation {
        public boolean validate(boolean setMsg);
    }


    @BindingAdapter("android:textSize")
    public static void bindTextSize(TextView textView, int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @BindingAdapter("android:paddingMessage")
    public static void bindPaddingMessage(TextView textView, boolean id) {
        if (id)
            textView.setPadding(TypedValue.COMPLEX_UNIT_DIP * 50,10,10,10);
    }

    @BindingAdapter("error")
    public static void setError(EditText editText, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            editText.setError(editText.getContext().getString((Integer) strOrResId));
        } else {
            editText.setError((String) strOrResId);
        }
    }

    @BindingAdapter("errorText")
    public static void setError(TextView textView, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            textView.setError(textView.getContext().getString((Integer) strOrResId));
        } else {
            textView.setError((String) strOrResId);
        }
    }


    @BindingAdapter("ValidationOnFocus")
    public static void setValidation(EditText editText, Validation validation) {
        View.OnFocusChangeListener onFocusChangeListener = (view, hasFocus) -> {
            EditText et = (EditText) view;
            if (et.getText().length() > 0 && !hasFocus) {
                validation.validate(true);
            }
        };
        editText.setOnFocusChangeListener(onFocusChangeListener);
    }

    @BindingAdapter("android:text")
    public static void setDateText(TextView view, Date value) {
        // this function written only because we need one-way to source binding

    }

    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    @InverseBindingAdapter(attribute = "android:text")
    public static Date getDateText(TextView view) {
        try {
            return format.parse(view.getText().toString());
        } catch (ParseException e) {
            return null;
        }
    }

}
