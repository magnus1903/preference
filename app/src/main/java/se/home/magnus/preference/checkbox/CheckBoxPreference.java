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

package se.home.magnus.preference.checkbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.StateListDrawableCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceViewHolder;

import se.home.magnus.preference.R;
import se.home.magnus.preference.utility.Common;

/**
 * This class is a check box preference. To configure the check box of this preference see
 * "check_box_preference.xml".
 */
public class CheckBoxPreference extends androidx.preference.CheckBoxPreference {

    /**
     * The default checked state.
     */
    private final boolean _isCheckedDefault;

    /**
     * The resource id of the "checked" image.
     */
    private final int _checkedId;

    /**
     * The resource id of the "unchecked" image.
     */
    private final int _uncheckedId;

    /**
     * The check box of this check box preference.
     */
    private CheckBox _checkBox;

    /**
     * @param context      the context this preference is running in, through which it can access
     *                     the current theme, resources, etc
     * @param attributeSet the attributes of the XML tag that is inflating this preference, which
     *                     may be null
     */
    public CheckBoxPreference(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        // the solution below is chosen, since the "try (TypedArray ..." throws an exception
        TypedArray typedAttributeArray = context.obtainStyledAttributes(attributeSet, R.styleable.CheckBoxPreference, 0, 0);
        try {
            _isCheckedDefault = typedAttributeArray.getBoolean(R.styleable.CheckBoxPreference_displayPlayerDefaultValue, false);
            if ((_checkedId = typedAttributeArray.getResourceId(R.styleable.CheckBoxPreference_checkedId, -1)) < 0) {
                throw new RuntimeException(getContext().getString(R.string.check_box_preference_mandatory_error, "checkedId"));
            }
            if ((_uncheckedId = typedAttributeArray.getResourceId(R.styleable.CheckBoxPreference_uncheckedId, -1)) < 0) {
                throw new RuntimeException(getContext().getString(R.string.check_box_preference_mandatory_error, "uncheckedId"));
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
     *                             to them after this method returns.
     *
     * @throws RuntimeException
     * @noinspection RedundantSuppression
     */
    @SuppressWarnings("JavaDoc")
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder preferenceViewHolder) throws RuntimeException {
        super.onBindViewHolder(preferenceViewHolder);
        StateListDrawableCompat buttonStateList = new StateListDrawableCompat();
        // the next statement is crucial because, if the "preference view holder" isn't set
        // to be NOT recyclable, all "check box preference" instances will share the same
        // "check box preference" instance
        preferenceViewHolder.setIsRecyclable(false);
        buttonStateList.addState(new int[]{android.R.attr.state_checked}, ResourcesCompat.getDrawable(getContext().getResources(), _checkedId, null));
        buttonStateList.addState(new int[]{-android.R.attr.state_checked}, ResourcesCompat.getDrawable(getContext().getResources(), _uncheckedId, null));
        _checkBox = (CheckBox) preferenceViewHolder.findViewById(R.id.check_box);
        _checkBox.setButtonDrawable(buttonStateList);
        if (getPersistedBoolean(_isCheckedDefault)) {
            _checkBox.performClick();
        }
    }

    /**
     * Processes a click on this preference.
     */
    @Override
    protected void onClick() {
        _checkBox.performClick();
        persistBoolean(_checkBox.isChecked());
    }

    /**
     * Sets the default checked state of this preference.
     */
    public void setDefaultState() {
        _checkBox.setChecked(_isCheckedDefault);
        persistBoolean(_isCheckedDefault);
    }

    /**
     * Returns the checked state, i.e. whether this check box preference is checked or not.
     *
     * @return the checked state
     */
    public boolean isChecked() {
        return getPersistedBoolean(_isCheckedDefault);
    }

}
