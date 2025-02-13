package com.example.therapistbluelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF rectF;
    private int progress = 0;
    private int progressColor = 0xFF3D5AFE; // Default progress color (violet)
    private int textColor = 0xFFFFFFFF;    // Default text color (white)

    public GaugeView(Context context) {
        super(context);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Background arc paint
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(0xFFE0E0E0); // Light gray color
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // Progress arc paint
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor); // Use the dynamic progress color
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // Shadow for progress arc
        progressPaint.setShadowLayer(10, 0, 5, 0x803D5AFE); // Subtle shadow
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // Text paint
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor); // Dynamic text color
        textPaint.setTextAlign(Paint.Align.CENTER);

        // RectF for the arcs
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        // Calculate dynamic stroke widths
        float strokeWidth = size * 0.1f; // 10% of size
        backgroundPaint.setStrokeWidth(strokeWidth * 0.5f); // Thinner background arc
        progressPaint.setStrokeWidth(strokeWidth); // Thicker progress arc

        // Calculate radius dynamically
        float radius = (size / 2f) - (strokeWidth * 1.5f); // Account for stroke width and padding
        float centerX = width / 2f;
        float centerY = height / 2f;

        // Define the bounding rectangle for the arcs
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw the background arc
        canvas.drawArc(rectF, 135, 270, false, backgroundPaint);

        // Draw the progress arc
        progressPaint.setColor(progressColor); // Ensure the updated color is applied
        canvas.drawArc(rectF, 135, (270 * progress) / 100, false, progressPaint);

        // Draw the percentage text
        textPaint.setTextSize(radius * 0.5f); // Set text size to 50% of radius
        textPaint.setColor(textColor); // Apply dynamic text color
        canvas.drawText(progress + "%", centerX, centerY + (textPaint.getTextSize() / 3), textPaint);
    }

    public void setProgress(int progress) {
        this.progress = Math.min(Math.max(progress, 0), 100); // Clamp between 0 and 100
        invalidate(); // Redraw the view
    }

    public void setProgressColor(int color) {
        this.progressColor = color; // Set the progress arc color dynamically
        invalidate(); // Redraw the view to apply the color
    }

    public void setTextColor(int color) {
        this.textColor = color; // Set the text color dynamically
        invalidate(); // Redraw the view to apply the color
    }
}
