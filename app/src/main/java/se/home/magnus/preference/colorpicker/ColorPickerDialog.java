package se.home.magnus.preference.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import se.home.magnus.preference.R;
import se.home.magnus.preference.ui.DrawableImageView;

/**
 * This class is a wrapper around an alert dialog intended to be used for selecting colors.
 */
public class ColorPickerDialog {

    /**
     * The selected color.
     */
    private @ColorInt int _selectedColor;

    /**
     * A selected color change listener.
     */
    private final ColorChangedListener _colorChangedListener;

    /**
     * The title text view in the alert dialog.
     */
    private final TextView _titleTextView;

    /**
     * The color picker image view in the alert dialog.
     */
    private final DrawableImageView _colorPickerImageView;

    /**
     * The wrapped alert dialog.
     */
    private final AlertDialog _alertDialog;

    /**
     * @param context  the context in which the dialog is displayed
     * @param listener a selected color change listener
     */
    @SuppressWarnings("unused")
    @SuppressLint("ClickableViewAccessibility")
    public ColorPickerDialog(@NonNull Context context, @NonNull ColorChangedListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_color_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        _colorChangedListener = listener;
        _titleTextView = layout.findViewById(R.id.dialog_title_text);
        _colorPickerImageView = layout.findViewById(R.id.dialog_color_picker_image);
        _colorPickerImageView.setColorSelectedListener(new DrawableImageView.ColorSelectedListener() {
            /**
             * Notification on that an "android color int" has been selected in the wrapped image.
             *
             * @param color the selected color as an "android color int"
             *
             * @throws IllegalArgumentException
             */
            @SuppressWarnings({"JavadocDeclaration", "RedundantSuppression"})
            @Override
            public void onColorSelected(@ColorInt int color) throws IllegalArgumentException {
                if (Color.alpha(color) > 0) {
                    _selectedColor = color;
                }
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
                if (Color.alpha(_selectedColor) > 0) {
                    _colorChangedListener.onColorChanged(_selectedColor);
                    dialog.cancel();
                }
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
    }

    /**
     * Shows the alert dialog.
     */
    public void show() {
        _alertDialog.show();
    }

    /**
     * Sets the color picker image in the alert dialog.
     *
     * @param id a resource id of an image
     */
    public void setColorPickerImage(@DrawableRes int id) {
        _colorPickerImageView.setImageResource(id);
    }

    /**
     * Sets the title  in the alert dialog.
     *
     * @param title a title
     */
    public void setTitle(@NonNull String title) {
        _titleTextView.setText(title);
    }

    /**
     * Sets the selected color.
     *
     * @param selectedColor an "android color int"
     *
     * @throws IllegalArgumentException
     */
    @SuppressWarnings({"JavaDoc", "RedundantSuppression"})
    public void setSelectedColor(@ColorInt int selectedColor) throws IllegalArgumentException {
        if (Color.alpha(selectedColor) > 0) {
            _selectedColor = selectedColor;
            _colorPickerImageView.setSelectedColor(selectedColor);
        }
    }

    /**
     * A callback that notifies clients when the selected color has been changed.
     */
    public interface ColorChangedListener {

        /**
         * Notification that the selected color has changed.
         *
         * @param color a new selected color
         */
        void onColorChanged(@ColorInt int color);

    }

}
