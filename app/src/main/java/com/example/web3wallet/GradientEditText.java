package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

public class GradientEditText extends AppCompatEditText {

    public GradientEditText(Context context) {
        super(context);
    }

    public GradientEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        Paint paint = getPaint();

        @SuppressLint("DrawAllocation") Shader shader = new LinearGradient(0, 0, getWidth(), 0,
                new int[]{
                        0xFFE91E63,
                        0xFFFFC107,
                        0xFF8BC34A,
                        0xFF2196F3,
                        0xFF9C27B0
                },
                null, Shader.TileMode.CLAMP);

        paint.setShader(shader);

        super.onDraw(canvas);
    }
}
