package com.buzzware.apnigari.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class AlertUtils {

    /**
     * Convert dp to px value.
     * -
     * Source: https://stackoverflow.com/a/6327095/2263329
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * Show an AlertDialog with a single input box.
     *
     * @param context         Application context
     * @param title           Dialog title
     * @param message         Dialog input message/hint
     * @param inputType       InputType of EditText
     * @param positiveBtnText Dialog positive button text
     * @param negativeBtnText Dialog negative button text
     * @param listener        Dialog buttons click listener
     */
    public static void showSingleInputDialog(
            @NonNull Context context,
            @NonNull String title,
            @NonNull String message,
            int inputType,
            @NonNull String positiveBtnText,
            @NonNull String negativeBtnText,
            @NonNull final SingleInputDialogListener listener
    ) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

//        TextInputLayout textInputLayout = new TextInputLayout(context);

        final EditText input = new EditText(context);
        input.setSingleLine(true);
//        input.setInputType(inputType);
        input.setHint(message);


        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int left_margin = AlertUtils.dpToPx(20, context.getResources());
        int top_margin = AlertUtils.dpToPx(10, context.getResources());
        int right_margin = AlertUtils.dpToPx(20, context.getResources());
        int bottom_margin = AlertUtils.dpToPx(5, context.getResources());

        params.setMargins(left_margin, top_margin, right_margin, bottom_margin);

//        textInputLayout.setLayoutParams(params);

//        textInputLayout.addView(input);
        input.setLayoutParams(params);
        container.addView(input);

        alert.setView(container);

        alert.setPositiveButton(positiveBtnText, (dialog, whichButton) -> {

            listener.positiveCallback(input.getText().toString());

            dialog.dismiss();
        });

        alert.setNegativeButton(negativeBtnText,
                (dialog, which) -> {

                    listener.negativeCallback();

                    dialog.dismiss();
                });

        alert.show();
    }

    public interface SingleInputDialogListener {

        void positiveCallback(String inputText);

        void negativeCallback();

    }


}