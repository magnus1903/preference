package se.home.magnus.preference.edittext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;

import se.home.magnus.preference.R;

/**
 * This class is extending the "edit text preference" class making it possible to parse the text
 * "against" a regular expression. NOTE that the enabled/disabled colors of the dialog buttons are
 * specified in the AlertDialogTheme in the styles.xml (in the "using" app).
 */
public class ParsableEditTextPreference extends EditTextPreference {

    /**
     * A regular expression which all "edit text values" must match.
     */
    private final String _regularExpression;

    /**
     * Tells whether or not the fragment manager is set. This is a solution to make the "fragment
     * manager dependency" mandatory. If the fragment manager isn't set (when needed) an exception
     * is thrown. The "motivation" for this solution is that it isn't possible to use "constructor
     * dependency injection".
     */
    private boolean _isFragmentManagerSet;
    /**
     * A "mandatory" fragment manager which is used to retrieve the alert dialog of this
     * preference.
     */
    private FragmentManager _fragmentManager;

    /**
     * @param context      the context this view is running in, through which it can access the
     *                     current theme, resources, etc
     * @param attributeSet the attributes of the XML tag that is inflating the view, which may be
     *                     null
     *
     * @throws RuntimeException
     * @noinspection resource, JavadocDeclaration, RedundantSuppression
     */
    @SuppressWarnings("JavaDoc")
    public ParsableEditTextPreference(@NonNull Context context, @Nullable AttributeSet attributeSet) throws RuntimeException {
        super(context, attributeSet);
        String description, hint, regularExpression;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ParsableEditTextPreference, 0, 0);
        try {
            if ((regularExpression = typedArray.getString(R.styleable.ParsableEditTextPreference_parsableRegularExpression)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableRegularExpression"));
            }
            if ((hint = typedArray.getString(R.styleable.ParsableEditTextPreference_parsableHint)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableHint"));
            }
            if ((description = typedArray.getString(R.styleable.ParsableEditTextPreference_parsableDescription)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableDescription"));
            }
        } finally {
            typedArray.recycle();
        }
        // this "setter method" is necessary to "remove" the "preference title" from the "preference dialog"
        setDialogTitle("");
        setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            /**
             * Called when the dialog view for this preference has been bound, allowing you to customize the
             * EditText displayed in the dialog.
             *
             * @param editText the EditText displayed in the dialog
             */
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                TextView dialogTitle = ((LinearLayout) editText.getParent()).findViewById(R.id.edit_title);
                dialogTitle.setText(HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT));

                if (editText.getText().length() == 0) {
                    setDialogButtonEnabled(false);
                }

                editText.setHint(hint);
                editText.addTextChangedListener(new TextWatcher() {
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
                        setDialogButtonEnabled(source.length() > 0);
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
                        editText.removeTextChangedListener(this);
                        // checks if the supplied string matches the regular expression, if not it must be the last
                        // character that is invalid
                        if (input.matches(regularExpression)) {
                            output = input;
                        } else if (input.length() > 0) {
                            output = input.substring(0, input.length() - 1);
                        }
                        editText.setText(output);
                        editText.setSelection(output.length());
                        editText.addTextChangedListener(this);
                    }

                    /**
                     * This method is called to notify you that, somewhere within source, the text has been changed.
                     *
                     * @param source an editable
                     */
                    @Override
                    public void afterTextChanged(Editable source) {
                        setDialogButtonEnabled(source.length() > 0);
                    }
                });
            }
        });
        _isFragmentManagerSet = false;
        _regularExpression = regularExpression;
    }

    /**
     * Processes a click on this preference.
     */
    @Override
    protected void onClick() {
        super.onClick();
        setDialogButtonEnabled(false);
    }

    /**
     * Sets a fragment manager which is used to retrieve the alert dialog of this preference.
     *
     * @param fragmentManager a fragment manager
     */
    public void setFragmentManager(@NonNull FragmentManager fragmentManager) {
        _isFragmentManagerSet = true;
        _fragmentManager = fragmentManager;
    }

    /**
     * Sets the value which must match the regular expression.
     *
     * @param value a value
     *
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("JavadocDeclaration")
    private void setValue(@NonNull String value) throws IllegalArgumentException {
        if (value.matches(_regularExpression)) {
            setText(value);
        } else {
            throw new IllegalArgumentException(getContext().getString(R.string.parsable_edit_text_matching_error, value, _regularExpression));
        }
    }

    /**
     * Set the enabled state of the positive dialog button in this preference.
     *
     * @param enabled true if the positive dialog button is enabled, false otherwise
     *
     * @throws RuntimeException
     * @noinspection JavadocDeclaration
     */
    private void setDialogButtonEnabled(boolean enabled) throws RuntimeException {
        AlertDialog dialog;
        if (_isFragmentManagerSet) {
            for (Fragment fragment : _fragmentManager.getFragments()) {
                if (fragment instanceof EditTextPreferenceDialogFragmentCompat) {
                    if ((dialog = (AlertDialog) ((EditTextPreferenceDialogFragmentCompat) fragment).getDialog()) != null) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
                        break;
                    }
                }
            }
        } else {
            throw new RuntimeException(getContext().getString(R.string.generic_edit_text_dependency_error));
        }
    }

}
