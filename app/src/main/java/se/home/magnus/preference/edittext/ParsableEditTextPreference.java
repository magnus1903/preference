package se.home.magnus.preference.edittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import se.home.magnus.preference.R;

/**
 * This class is extending the "edit text preference" class making it possible to parse a text
 * "against" a regular expression in a wrapped parsable edit text dialog. NOTE that the
 * enabled/disabled colors of the buttons in the dialog (which is opened when this preference is
 * clicked) are specified in the AlertDialogTheme in the styles.xml (in the "using" app).
 */
public class ParsableEditTextPreference extends EditTextPreference implements ParsableEditTextDialog.OnTextListener {

    /**
     * The title in the dialog.
     */
    private final String _dialogTitle;

    /**
     * The message in the dialog.
     */
    private final String _dialogMessage;

    /**
     * A regular expression which all "edit text values" must match.
     */
    private final String _regularExpression;

    /**
     * A hint to the user what to enter in the dialog.
     */
    private final String _hint;

    /**
     * The wrapped parsable edit text dialog.
     */
    private final ParsableEditTextDialog _dialog;

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
        try (TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ParsableEditText, 0, 0)) {
            _dialog = new ParsableEditTextDialog(context, this);
            if ((_dialogTitle = typedArray.getString(R.styleable.ParsableEditText_parsableTitle)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableTitle"));
            }
            if ((_dialogMessage = typedArray.getString(R.styleable.ParsableEditText_parsableMessage)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableMessage"));
            }
            if ((_regularExpression = typedArray.getString(R.styleable.ParsableEditText_parsableRegularExpression)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableRegularExpression"));
            }
            if ((_hint = typedArray.getString(R.styleable.ParsableEditText_parsableHint)) == null) {
                throw new RuntimeException(getContext().getString(R.string.parsable_edit_text_mandatory_error, "parsableHint"));
            }
        }
    }

    /**
     * Binds the created View to the data for this preference. This is a good place to grab
     * references to custom Views in the layout and set properties on them. Make sure to call
     * through to the superclass implementation.
     *
     * @param preferenceViewHolder the ViewHolder that provides references to the views to fill in,
     *                             these views will be recycled, so you should not hold a reference
     *                             to them after this method returns
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        String text = getPersistedString(null);
        // the next statement is crucial because, if the "preference view holder" isn't set
        // to be NOT recyclable, all "float seek bar preference" instances will share the same
        // "float seek bar" instance
        preferenceViewHolder.setIsRecyclable(false);
        _dialog.setTitle(_dialogTitle);
        _dialog.setMessage(_dialogMessage);
        _dialog.setRegularExpression(_regularExpression);
        _dialog.setHint(_hint);
        _dialog.setParsedText(text);
    }

    /**
     * Notification that the text has been successfully parsed.
     *
     * @param text a new parsed text
     */
    @Override
    public void onParsed(@NonNull String text) {
        setText(text);
        persistString(text);
    }

    /**
     * Processes a click on this preference.
     */
    @Override
    protected void onClick() {
        _dialog.show();
    }

}
