/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.home.magnus.preference.seekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import se.home.magnus.preference.utility.Common;
import se.home.magnus.preference.R;

/**
 * Preference based on android.preference.SeekBarPreference but uses support preference as a base.
 * It contains a title and a {@link FloatSeekBar} and an optional FloatSeekBar value
 * {@link TextView}. The actual preference layout is customizable by setting {@code android:layout}
 * on the preference widget layout or {@code seekBarPreferenceStyle} attribute.
 *
 * <p>The {@link FloatSeekBar} within the preference can be defined adjustable or not by setting
 * {@code adjustable} attribute. If adjustable, the preference will be responsive to DPAD left/right
 * keys. Otherwise, it skips those keys.
 *
 * <p>The {@link FloatSeekBar} value view can be shown or disabled by setting
 * {@code showSeekBarValue} attribute to true or false, respectively.
 *
 * <p>Other {@link FloatSeekBar} specific attributes (e.g.
 * {@code title, summary, defaultValue, min, max}) can be set directly on the preference widget
 * layout.
 */
public class FloatSeekBarPreference extends Preference {

    private boolean _trackingTouch;

    // Whether to show the FloatSeekBar value TextView next to the bar
    private final boolean _showSeekBarValue;

    private float _value;

    /**
     * The color of the progress of the float seek bar.
     */
    private int _color;

    /**
     * The color of the thumb of the float seek bar.
     */
    private int _thumbColor;

    /**
     * The diameter (in pixels) of the thumb of the float seek bar.
     */
    private final int _diameter;

    /**
     * The thickness (height) of the seek bar (in pixels).
     */
    private final int _size;

    private final float _defaultValue;

    private final float _minimumValue;

    private final float _maximumValue;

    private final float _valueIncrement;

    /**
     * A format string to show the number of decimals of the float seek bar value.
     */
    private final String _valueFormat;

    private FloatSeekBar _floatSeekBar;

    private TextView _textView;

    /**
     * Listener reacting to the {@link FloatSeekBar} changing value by the user
     */
    private final FloatSeekBar.OnSeekBarChangeListener __mSeekBarChangeListener = new FloatSeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(float floatValue, boolean fromUser) throws IllegalArgumentException {
            float seekBarValue = _minimumValue + (_maximumValue - _minimumValue) * floatValue;
            if (_trackingTouch) {
                __setValueInternal(seekBarValue);
            } else {
                // We always want to update the text while the FloatSeekBar is being dragged
                updateLabelValue(seekBarValue);
            }
        }

        @Override
        public void onStartTrackingTouch() {
            _trackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch() {
            _trackingTouch = false;
        }
    };

    public FloatSeekBarPreference(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        float value, minimumValue, maximumValue, defaultValue, valueIncrement;
        // the solution below is chosen, since the "try (TypedArray ..." throws an exception
        TypedArray typedAttributeArray = context.obtainStyledAttributes(attributeSet, R.styleable.FloatSeekBar, 0, 0);
        try {
            minimumValue = typedAttributeArray.getFloat(R.styleable.FloatSeekBar_floatMinimumValue, 0);
            maximumValue = typedAttributeArray.getFloat(R.styleable.FloatSeekBar_floatMaximumValue, 1);
            if (maximumValue - minimumValue > 0) {
                _minimumValue = minimumValue;
                _maximumValue = maximumValue;
            } else {
                throw new IllegalArgumentException(getContext().getString(R.string.float_seek_bar_initiation_error));
            }
            // NOTE that the difference between the maximum and minimum values must be equally divisible by the value increment
            valueIncrement = typedAttributeArray.getFloat(R.styleable.FloatSeekBar_floatValueIncrement, 0.05f);
            if (valueIncrement > 0) {
                value = (maximumValue - minimumValue) / valueIncrement;
                if (Math.abs(value - Math.round(value)) < Common.FLOAT_EQUALITY_TOLERANCE) {
                    _valueIncrement = valueIncrement;
                } else {
                    throw new IllegalArgumentException(getContext().getString(R.string.float_seek_bar_increment_error));
                }
            } else {
                throw new IllegalArgumentException(getContext().getString(R.string.float_seek_bar_increment_error));
            }
            defaultValue = typedAttributeArray.getFloat(R.styleable.FloatSeekBar_floatDefaultValue, 0);
            if (!(defaultValue < minimumValue) && !(defaultValue > maximumValue)) {
                value = defaultValue / valueIncrement;
                // NOTE that the difference between the default and minimum values must be equally divisible by the value increment
                if (Math.abs(value - Math.round(value)) < Common.FLOAT_EQUALITY_TOLERANCE) {
                    _value = _defaultValue = defaultValue;
                } else {
                    throw new IllegalArgumentException(getContext().getString(R.string.float_seek_bar_default_error));
                }
            } else {
                throw new IllegalArgumentException(getContext().getString(R.string.float_seek_bar_default_error));
            }
            _showSeekBarValue = typedAttributeArray.getBoolean(R.styleable.FloatSeekBar_floatShowSeekBarValue, true);
            _valueFormat = "%." + typedAttributeArray.getInt(R.styleable.FloatSeekBar_floatDecimalCount, context.getResources().getInteger(R.integer.float_seek_bar_decimal_count_default_value)) + "f";
            _color = typedAttributeArray.getColor(R.styleable.FloatSeekBar_floatColor, context.getColor(R.color.color_primary));
            _thumbColor = typedAttributeArray.getColor(R.styleable.FloatSeekBar_floatThumbColor, context.getColor(R.color.color_primary));
            _diameter = typedAttributeArray.getInt(R.styleable.FloatSeekBar_floatDiameter, context.getResources().getInteger(R.integer.float_seek_bar_ball_diameter_default_value));
            _size = typedAttributeArray.getInt(R.styleable.FloatSeekBar_floatSize, context.getResources().getInteger(R.integer.float_seek_bar_size_default_value));
        } finally {
            typedAttributeArray.recycle();
        }
    }

    /**
     * Binds the created View to the data for this preference. This is a good place to grab
     * references to custom Views in the layout and set properties on them. Make sure to call
     * through to the superclass implementation.
     *
     * @param preferenceViewHolder the ViewHolder that provides references to the views to fill in,
     *                             these views will be recycled, so you should not hold a reference
     *                             to them after this method returns.
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        // the next statement is crucial because, if the "preference view holder" isn't set
        // to be NOT recyclable, all "float seek bar preference" instances will share the same
        // "float seek bar" instance
        preferenceViewHolder.setIsRecyclable(false);
        _floatSeekBar = (FloatSeekBar) preferenceViewHolder.findViewById(R.id.seekbar);
        _textView = (TextView) preferenceViewHolder.findViewById(R.id.seekbar_value);
        // NOTE that this order of the "setter methods" of the float seek bar MUST be this
        if (_showSeekBarValue) {
            _textView.setVisibility(View.VISIBLE);
        } else {
            _textView.setVisibility(View.GONE);
            _textView = null;
        }
        _floatSeekBar.initialize(__mSeekBarChangeListener, _valueIncrement / (_maximumValue - _minimumValue), _thumbColor, _color, _size, _diameter);
        if (_textView != null) {
            _textView.setText(String.format(_valueFormat, _value));
        }
        _floatSeekBar.setValue((_value - _minimumValue) / (_maximumValue - _minimumValue));
    }

    /**
     * Sets the color of the seek bar of this preference.
     *
     * @param color a color
     */
    public void setColor(@ColorInt int color) {
        _color = color;
    }

    /**
     * Sets the color of the thumb of the seek bar of this preference.
     *
     * @param color a color
     */
    public void setThumbColor(@ColorInt int color) {
        _thumbColor = color;
    }

    /**
     * Sets the default value of this preference.
     */
    public void setDefaultValue() {
        __setValue(_defaultValue);
    }

    /**
     * Returns the value of this preference.
     * <p>
     * return the value of this preference
     */
    public float getValue() {
        return getPersistedFloat(_defaultValue);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = _defaultValue;
        }
        __setValue(getPersistedFloat(Float.parseFloat(defaultValue.toString())));
    }

    private void __setValueInternal(float value) {
        if (value < _minimumValue) {
            value = _minimumValue;
        }
        if (value > _maximumValue) {
            value = _maximumValue;
        }
        updateLabelValue(value);
        persistFloat(value);
        _value = value;
    }

    /**
     * Sets the current value of the {@link FloatSeekBar}.
     *
     * @param seekBarValue The current value of the {@link FloatSeekBar}
     */
    private void __setValue(float seekBarValue) {
        __setValueInternal(seekBarValue);
        if (_floatSeekBar != null) {
            _floatSeekBar.setValue((_value - _minimumValue) / (_maximumValue - _minimumValue));
        }
    }

    /**
     * Attempts to update the TextView label that displays the current value.
     *
     * @param value the value to display next to the {@link FloatSeekBar}
     */
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    @SuppressLint("DefaultLocale")
    void updateLabelValue(float value) {
        if (_textView != null) {
            _textView.setText(String.format(_valueFormat, value));
        }
    }

}
