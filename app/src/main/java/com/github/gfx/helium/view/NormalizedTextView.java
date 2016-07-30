package com.github.gfx.helium.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.text.Normalizer;

public class NormalizedTextView extends AppCompatTextView {

    public NormalizedTextView(Context context) {
        super(context);
    }

    public NormalizedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalizedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Normalizer.normalize(text, Normalizer.Form.NFC), type);
    }
}
