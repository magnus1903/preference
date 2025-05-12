package se.home.magnus.preference.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import se.home.magnus.preference.R;

/**
 * This class is a wrapper around an alert dialog intended to be used for selecting colors.
 */
public class ColorPickerDialog {

    /**
     * The selected color.
     */
    private int _selectedColor;

    /**
     * A selected color change listener.
     */
    private final OnSelectedListener _selectedListener;

    /**
     * The title text view in the alert dialog.
     */
    private final TextView _titleTextView;

    /**
     * The text view connected to the selected image in the alert dialog.
     */
    private final TextView _selectedTextView;

    /**
     * The color picker image view in the alert dialog.
     */
    private final ImageView _colorPickerImageView;

    /**
     * The selected color image view in the alert dialog. This image view MUST be an "xml drawable"
     * (see e.g. "square.xml").
     */
    private final ImageView _selectedColorImageView;

    /**
     * The wrapped alert dialog.
     */
    private final AlertDialog _alertDialog;

    /**
     * @param context  the context in which the dialog is displayed
     * @param listener a selected color change listener
     *
     * @noinspection Convert2Lambda
     */
    @SuppressLint("ClickableViewAccessibility")
    public ColorPickerDialog(@NonNull Context context, @NonNull OnSelectedListener listener) {
        int[] selectedColor = {0};
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_color_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        _selectedListener = listener;
        _titleTextView = layout.findViewById(R.id.dialog_title_text);
        _selectedTextView = layout.findViewById(R.id.dialog_selected_color_text);
        _selectedColorImageView = layout.findViewById(R.id.dialog_selected_color_image);
        _colorPickerImageView = layout.findViewById(R.id.dialog_color_picker_image);
        _colorPickerImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    try {
                        selectedColor[0] = __getBitmapFromView(_colorPickerImageView).getPixel((int) event.getX(), (int) event.getY());
                        if (Color.alpha(selectedColor[0]) > 0) {
                            ((GradientDrawable) _selectedColorImageView.getDrawable()).setColor(Color.argb(Color.alpha(selectedColor[0]), Color.red(selectedColor[0]), Color.green(selectedColor[0]), Color.blue(selectedColor[0])));
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                return true;
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
                if (Color.alpha(selectedColor[0]) > 0) {
                    _selectedColor = selectedColor[0];
                    _selectedListener.onChanged(selectedColor[0]);
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
        if (Color.alpha(_selectedColor) > 0) {
            ((GradientDrawable) _selectedColorImageView.getDrawable()).setColor(_selectedColor);
        }
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
     * Sets the selected color image in the alert dialog.
     *
     * @param id a resource id of an image
     */
    public void setSelectedColorPickerImage(@DrawableRes int id) {
        _selectedColorImageView.setImageResource(id);
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
     * Sets the "selected" text in the alert dialog.
     *
     * @param text a "selected" text
     */
    public void setSelectedText(@NonNull String text) {
        _selectedTextView.setText(text);
    }

    /**
     * Sets the selected color.
     *
     * @param selectedColor an "android color int"
     *
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("JavaDoc")
    public void setSelectedColor(@ColorInt int selectedColor) throws IllegalArgumentException {
        if (Color.alpha(selectedColor) > 0) {
            _selectedColor = selectedColor;
        }
    }

    /**
     * Creates a bitmap from a view.
     *
     * @param view the view from which the bitmap is created
     *
     * @return a bitmap
     */
    private Bitmap __getBitmapFromView(@NonNull View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    /**
     * A callback that notifies clients when the selected color has been changed.
     */
    public interface OnSelectedListener {

        /**
         * Notification that the selected color has changed.
         *
         * @param color a new selected color
         */
        void onChanged(@ColorInt int color);

    }

}
