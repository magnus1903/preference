package se.home.magnus.preference.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.ColorUtils;

/**
 * This class is a wrapper around an image view. With this class it is possible to draw on top of
 * the wrapped image view and get notified when an "android color int" has been selected in the
 * wrapped image view.
 */
public class DrawableImageView extends AppCompatImageView {

    /**
     * The selected color.
     */
    private @ColorInt int _selectedColor;

    /**
     * The position of the selected color in the wrapped image view.
     */
    private Point _selectedPosition;

    /**
     * A possible color selected listener
     */
    private ColorSelectedListener _colorSelectedListener;

    /**
     * The paint object to use when drawing the border of the "selected color circle".
     */
    private final Paint _selectedColorBorderPaint;

    /**
     * @param context      the context this view is running in, through which it can access the
     *                     current theme, resources, etc.
     * @param attributeSet the attributes of the XML tag that is inflating this view, this value may
     *                     be null
     */
    public DrawableImageView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(new OnTouchListener() {
            /**
             * Called when a touch event is dispatched to the wrapped image view.
             *
             * @param view  this view
             * @param event the MotionEvent object containing full information about the event
             *
             * @return true if the listener has consumed the event, false otherwise
             */
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int color, x, y;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    x = (int) event.getX();
                    y = (int) event.getY();
                    try {
                        color = __getColor(x, y);
                        if (_colorSelectedListener != null) {
                            _colorSelectedListener.onColorSelected(color);
                        }
                        _selectedPosition = new Point(x, y);
                        _selectedColor = color;
                        invalidate();
                    } catch (IllegalArgumentException e) {
                        // do nothing
                    }
                }
                return true;
            }
        });
        _selectedColorBorderPaint = new Paint();
        _selectedColorBorderPaint.setStrokeWidth(5);
        _selectedColorBorderPaint.setStyle(Paint.Style.STROKE);
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
        }
    }

    /**
     * Sets a color selected listener which is called when an "android color int" has been selected
     * in the *wrapped image view.
     *
     * @param colorSelectedListener a color selected listener
     */
    public void setColorSelectedListener(@NonNull ColorSelectedListener colorSelectedListener) {
        _colorSelectedListener = colorSelectedListener;
    }

    /**
     * Draws the wrapped image with the "selected color circle" on top of it.
     *
     * @param canvas the canvas to draw upon
     */
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) throws IllegalArgumentException {
        super.dispatchDraw(canvas);
        if (_selectedPosition != null) {
            // draw the "selected color circle"
            if (ColorUtils.calculateLuminance(_selectedColor) > 0.25) {
                _selectedColorBorderPaint.setColor(Color.BLACK);
            } else {
                _selectedColorBorderPaint.setColor(Color.WHITE);
            }
            canvas.drawCircle(_selectedPosition.x, _selectedPosition.y, 25, _selectedColorBorderPaint);
        }
    }

    /**
     * Returns the "android color int" at the specified location in the wrapped image view.
     *
     * @param x the x coordinate (0...width-1) of the location in the wrapped image view
     * @param y the y coordinate (0...height-1) of the location in the wrapped image view
     *
     * @return an "android color int"
     *
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("JavadocDeclaration")
    private int __getColor(int x, int y) throws IllegalArgumentException {
        int color;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // to get the bitmap from the wrapped image view, construct a canvas with the specified
        // bitmap to draw into
        draw(new Canvas(bitmap));
        // gets the "android color int" at the specified location
        color = bitmap.getPixel(x, y);
        return color;
    }

    /**
     * Finds the position of a color in the wrapped image view.
     *
     * @param color     an "android color int"
     * @param tolerance a tolerance when comparing the specified color with the image pixel colors
     *
     * @return the position of the color in the wrapped image view or null if the color wasn't found
     */
    @SuppressWarnings("unused")
    private Point __findColorPosition(@ColorInt int color, int tolerance) {
        int imageWidth, imageHeight, offset;
        // an array to hold all image pixel colors
        int[] imagePixels;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // to get the bitmap from the wrapped image view, construct a canvas with the specified
        // bitmap to draw into
        draw(new Canvas(bitmap));
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        imagePixels = new int[imageWidth * imageHeight];
        bitmap.getPixels(imagePixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
        // loop through the array of image pixel colors to find the target color
        for (int y = 0; y < imageHeight; y++) {
            offset = y * imageWidth;
            for (int x = 0; x < imageWidth; x++) {
                if (Math.abs(imagePixels[offset + x] - color) < tolerance) {
                    // found the target color position
                    return new Point(x, y);
                }
            }
        }
        // the target color position wasn't found
        return null;
    }

    /**
     * A callback that notifies the listener when an "android color int" has been selected in the
     * wrapped image view.
     */
    public interface ColorSelectedListener {

        /**
         * Notification on that an "android color int" has been selected in the wrapped image view.
         *
         * @param color the selected color as an "android color int"
         *
         * @throws IllegalArgumentException
         */
        @SuppressWarnings({"JavadocDeclaration", "RedundantSuppression", "unused"})
        void onColorSelected(@ColorInt int color) throws IllegalArgumentException;

    }

}
