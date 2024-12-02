package se.home.magnus.preference.edittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import se.home.magnus.preference.R;

/**
 * This class is a wrapper around an alert dialog intended to be used for specifying parsable
 * texts.
 */
public class ParsableEditTextDialog {

    /**
     * The text color used for enabled buttons.
     */
    private final int _buttonEnabledColor;

    /**
     * The text color used for disabled buttons.
     */
    private final int _buttonDisabledColor;

    /**
     * The parsed text.
     */
    private String _parsedText;

    /**
     * A regular expression which all texts must match.
     */
    private String _regularExpression;

    /**
     * A parsed text listener.
     */
    private final OnTextListener _parsedListener;

    /**
     * The title text view in the alert dialog.
     */
    private final TextView _titleTextView;

    /**
     * The message text view in the alert dialog.
     */
    private final TextView _messageTextView;

    /**
     * The parsed text view in the alert dialog.
     */
    private final EditText _parsedTextView;

    /**
     * The wrapped alert dialog.
     */
    private final AlertDialog _alertDialog;

    /**
     * @param context  the context in which the dialog is displayed
     * @param listener a parsed text listener
     */
    @SuppressLint("ClickableViewAccessibility")
    public ParsableEditTextDialog(@NonNull Context context, @NonNull OnTextListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_parsable_edit_text, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        _buttonEnabledColor = ContextCompat.getColor(context, android.R.color.black);
        _buttonDisabledColor = ContextCompat.getColor(context, R.color.color_secondary_light);
        _parsedListener = listener;
        _titleTextView = layout.findViewById(R.id.title);
        _messageTextView = layout.findViewById(R.id.message);
        _parsedTextView = layout.findViewById(android.R.id.edit);
        _parsedTextView.addTextChangedListener(new TextWatcher() {
            /**
             * This method is called to notify you that, within source, the count characters beginning at start
             * are about to be replaced by new text with length after.
             *
             * @param source a character sequence
             * @param start start index of the characters to changed
             * @param count number of characters to be changed
             * @param after length of the replacing characters
             */
            @Override
            public void beforeTextChanged(CharSequence source, int start, int count, int after) {
            }

            /**
             * This method is called to notify you that, within source, the count characters beginning at start have
             * just replaced the old text that had the length before.
             *
             * @param source a character sequence
             * @param start start index of the replacing characters
             * @param before length of the replaced characters
             * @param count number of replacing characters
             * @noinspection SizeReplaceableByIsEmpty
             */
            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                String input = source.toString(), output = "";
                _parsedTextView.removeTextChangedListener(this);
                // checks if the supplied string matches the regular expression, if not it must be the last
                // character that is invalid
                if (input.matches(_regularExpression)) {
                    output = input;
                } else if (input.length() > 0) {
                    output = input.substring(0, input.length() - 1);
                }
                _parsedTextView.setText(output);
                _parsedTextView.setSelection(output.length());
                _parsedTextView.addTextChangedListener(this);
            }

            /**
             * This method is called to notify you that, somewhere within source, the text has been changed.
             *
             * @param source an editable
             */
            @Override
            public void afterTextChanged(Editable source) {
                boolean enabled = source.length() > 0;
                _alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
                _alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(enabled ? _buttonEnabledColor : _buttonDisabledColor);
            }
        });
        builder.setView(layout).setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            /**
             * This method will be invoked when the "ok" button in the dialog is clicked.
             *
             * @param dialog the dialog that received the click
             * @param id the id of the button that was clicked
             */
            @Override
            public void onClick(@NonNull DialogInterface dialog, int id) {
                _parsedListener.onParsed(_parsedTextView.getText().toString());
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            /**
             * This method will be invoked when the "cancel" button in the dialog is clicked.
             *
             * @param dialog the dialog that received the click
             * @param id the id of the button that was clicked
             */
            @Override
            public void onClick(@NonNull DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        _alertDialog = builder.create();
        _alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            /**
             * This method will be invoked when the dialog is shown.
             *
             * @param dialog the dialog that was shown will be passed into the
             *               method
             * @noinspection SizeReplaceableByIsEmpty
             */
            @Override
            public void onShow(@NonNull DialogInterface dialog) {
                boolean enabled = _parsedText != null && _parsedText.length() > 0;
                _alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
                _alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(enabled ? _buttonEnabledColor : _buttonDisabledColor);
                _alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(enabled);
                _alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(enabled ? _buttonEnabledColor : _buttonDisabledColor);
                _parsedTextView.setText(_parsedText);
            }
        });
    }

    /**
     * Shows the alert dialog.
     */
    public void show() {
        _alertDialog.show();
    }

    /**
     * Sets the parsed text in the alert dialog.
     *
     * @param parsedText a parsed text possibly null
     */
    public void setParsedText(@Nullable String parsedText) {
        // due to the fact that the text in the "parsed text view" affects the buttons in the alert dialog,
        // which are only "available" after the alert dialog has been shown, the text in the "parsed text view"
        // can only be set after the alert dialog is shown
        _parsedText = parsedText;
    }

    /**
     * Sets the title text in the alert dialog.
     *
     * @param title a title text
     */
    public void setTitle(@NonNull String title) {
        _titleTextView.setText(title);
    }

    /**
     * Sets the message text in the alert dialog.
     *
     * @param message a message text
     */
    public void setMessage(@NonNull String message) {
        _messageTextView.setText(message);
    }

    /**
     * Sets the hint text in the alert dialog.
     *
     * @param text a hint text
     */
    public void setHint(@NonNull String text) {
        _parsedTextView.setHint(text);
    }

    /**
     * Sets the regular expression which all texts must match in the alert dialog.
     *
     * @param regularExpression a regular expression
     */
    public void setRegularExpression(@NonNull String regularExpression) {
        _regularExpression = regularExpression;
    }

    /**
     * A callback that notifies clients when the text has been successfully parsed.
     */
    public interface OnTextListener {

        /**
         * Notification that the text has been successfully parsed.
         *
         * @param text a new parsed text
         */
        void onParsed(@NonNull String text);

    }

}
