package se.home.magnus.preference.utility;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * This class contains common variables and methods used throughout this library.
 */
public class Common {

    /**
     * A tolerance used when comparing floats to "consider" them equal.
     */
    public static final float FLOAT_EQUALITY_TOLERANCE = 0.0001f;

    /**
     * A regular expression pattern capturing the "numerical part" of a density-independent pixel
     * string (e.g. "45dp").
     */
    public static final Pattern DIP_REGULAR_EXPRESSION_PATTERN = Pattern.compile("^(\\d+)dp$");

    /**
     * Converts the supplied density-independent pixels to its pixels counterpart.
     *
     * @param context a context
     * @param source  a density-independent pixels
     *
     * @return the corresponding pixels
     */
    public static int densityIndependentPixelsToPixels(@NonNull Context context, int source) {
        return Math.round(source * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}