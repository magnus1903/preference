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

package se.home.magnus.preference.button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import se.home.magnus.preference.R;

/**
 * This class is a button preference. To configure the button of this preference see
 * "button_preference.xml" and "button_background.xml".
 */
public class ButtonPreference extends Preference {

    /**
     * Tells whether or not the click listener is set. This is a solution to make the "click
     * listener dependency" mandatory. If the click listener isn't set (when needed) an exception is
     * thrown. The "motivation" for this solution is that it isn't possible to use "constructor
     * dependency injection".
     */
    private boolean _isClickListenerSet;

    /**
     * The text color of this button.
     */
    private final int _textColor;

    /**
     * The background color of this button.
     */
    private final int _backgroundColor;

    /**
     * The text of this button.
     */
    private final String _text;

    /**
     * A "mandatory" click listener which is called when this preference is clicked.
     */
    private View.OnClickListener _clickListener;

    /**
     * @param context      the context this preference is running in, through which it can access
     *                     the current theme, resources, etc
     * @param attributeSet the attributes of the XML tag that is inflating this preference, which
     *                     may be null
     *
     * @throws RuntimeException
     * @noinspection resource, JavadocDeclaration, RedundantSuppression
     */
    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attributeSet) throws RuntimeException {
        super(context, attributeSet, 0);
        int textColor, backgroundColor;
        String text;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ButtonPreference, 0, 0);
        try {
            textColor = typedArray.getColor(R.styleable.ButtonPreference_buttonTextColor, context.getColor(android.R.color.white));
            backgroundColor = typedArray.getColor(R.styleable.ButtonPreference_buttonBackgroundColor, context.getColor(R.color.color_primary));
            if ((text = typedArray.getString(R.styleable.ButtonPreference_buttonText)) == null) {
                throw new RuntimeException(getContext().getString(R.string.button_mandatory_error, "buttonText"));
            }
            _isClickListenerSet = false;
            _textColor = textColor;
            _backgroundColor = backgroundColor;
            _text = text;
        } finally {
            typedArray.recycle();
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
        Button button;
        // the next statement is crucial because, if the "preference view holder" isn't set
        // to be NOT recyclable, all "button preference" instances will share the same
        // "button preference" instance
        preferenceViewHolder.setIsRecyclable(false);
        button = (Button) preferenceViewHolder.findViewById(R.id.button);
        button.setTextColor(_textColor);
        ((GradientDrawable) button.getBackground()).setColor(_backgroundColor);
        button.setText(_text);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param view the view that was clicked
             */
            @Override
            public void onClick(@NonNull View view) {
                if (_isClickListenerSet) {
                    _clickListener.onClick(view);
                } else {
                    throw new RuntimeException(getContext().getString(R.string.button_dependency_error));
                }
            }
        });
    }

    /**
     * Sets a click listener which is called when this preference is clicked.
     *
     * @param clickListener a click listener
     */
    public void setClickListener(@NonNull View.OnClickListener clickListener) {
        _isClickListenerSet = true;
        _clickListener = clickListener;
    }

}
