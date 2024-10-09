package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {

    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = getPaint();
        int width = getWidth();

        @SuppressLint("DrawAllocation") Shader shader = new LinearGradient(0, 0, width, 0,
                new int[]{
                        0xFFFFA500,
                        0xFFFF4500,
                        0xFFFF69B4,
                        0xFFBA55D3,
                        0xFF7B68EE,
                        0xFF00CED1,
                        0xFF32CD32,
                        0xFFFFFF00,
                        0xFFFFD700,
                        0xFFDC143C
                },
                null, Shader.TileMode.REPEAT);

        paint.setShader(shader);

        super.onDraw(canvas);
    }
}
