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

package se.home.magnus.preference.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

import se.home.magnus.preference.R;
import se.home.magnus.preference.utility.Common;

/**
 * A preference for selecting colors via a wrapped color picker dialog.
 */
public class ColorPickerPreference extends DialogPreference implements ColorPickerDialog.OnSelectedListener {

    /**
     * The default color.
     */
    private final int _defaultColor;

    /**
     * The resource id of the color picker image in the dialog.
     */
    private final int _imagePickerId;

    /**
     * The resource id of the color selected image in the dialog.
     */
    private final int _imageSelectedId;

    /**
     * The title in the dialog.
     */
    private final String _dialogTitle;

    /**
     * The text connected to the selected image in the dialog.
     */
    private final String _selectedText;

    /**
     * The wrapped color picker dialog.
     */
    private final ColorPickerDialog _dialog;

    /**
     * The selected color image view in this preference. This image view MUST be an "xml drawable"
     * (see e.g. "square.xml").
     */
    private ImageView _selectedColorImageView;

    /**
     * @param context      the context this view is running in, through which it can access the
     *                     current theme, resources, etc
     * @param attributeSet the attributes of the XML tag that is inflating this view, this value may
     *                     be null
     */
    public ColorPickerPreference(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        // the solution below is chosen, since the "try (TypedArray ..." throws an exception
        TypedArray typedAttributeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColorPicker, 0, 0);
        try {
            _dialog = new ColorPickerDialog(context, this);
            _imagePickerId = typedAttributeArray.getResourceId(R.styleable.ColorPicker_colorPickerId, -1);
            _imageSelectedId = typedAttributeArray.getResourceId(R.styleable.ColorPicker_colorSelectedId, -1);
            _defaultColor = typedAttributeArray.getInt(R.styleable.ColorPicker_colorDefaultColor, R.color.color_primary);
            if ((_dialogTitle = typedAttributeArray.getString(R.styleable.ColorPicker_colorTitle)) == null) {
                throw new RuntimeException(getContext().getString(R.string.color_picker_mandatory_error, "colorTitle"));
            }
            if ((_selectedText = typedAttributeArray.getString(R.styleable.ColorPicker_colorSelectedText)) == null) {
                throw new RuntimeException(getContext().getString(R.string.color_picker_mandatory_error, "colorSelectedText"));
            }
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
     *                             to them after this method returns
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        int color = getPersistedInt(_defaultColor);
        // the next statement is crucial because, if the "preference view holder" isn't set
        // to be NOT recyclable, all "float seek bar preference" instances will share the same
        // "float seek bar" instance
        preferenceViewHolder.setIsRecyclable(false);
        _dialog.setColorPickerImage(_imagePickerId);
        _dialog.setSelectedColorPickerImage(_imageSelectedId);
        _dialog.setTitle(_dialogTitle);
        _dialog.setSelectedText(_selectedText);
        _dialog.setSelectedColor(color);
        _selectedColorImageView = (ImageView) preferenceViewHolder.findViewById(R.id.selected_color);
        _selectedColorImageView.setImageResource(_imageSelectedId);
        ((GradientDrawable) _selectedColorImageView.getDrawable()).setColor(color);
    }

    /**
     * Notification that the selected color has changed.
     *
     * @param color a new selected color
     */
    @Override
    public void onChanged(@ColorInt int color) {
        __setColor(color);
    }

    /**
     * Processes a click on this preference.
     */
    @Override
    protected void onClick() {
        _dialog.show();
    }

    /**
     * Sets the default color of this preference.
     */
    public void setDefaultColor() {
        __setColor(_defaultColor);
    }

    /**
     * Returns the color of this preference.
     *
     * return the color of this preference
     */
    public @ColorInt int getColor() {
        return getPersistedInt(_defaultColor);
    }

    /**
     * Sets the current color and selected color.
     *
     * @param color the current
     */
    private void __setColor(@ColorInt int color) {
        if (_selectedColorImageView != null) {
            if (Color.alpha(color) > 0) {
                _dialog.setSelectedColor(color);
                ((GradientDrawable) _selectedColorImageView.getDrawable()).setColor(color);
                persistInt(color);
            }
        }
    }

}

