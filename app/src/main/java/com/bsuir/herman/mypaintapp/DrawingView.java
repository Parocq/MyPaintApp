package com.bsuir.herman.mypaintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;


    private int status = 0;

    private Context context;

    private float x0 = 0;
    private float y0 = 0;


    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLayerType(LAYER_TYPE_HARDWARE, null);
        setupDrawing();

    }

    private void setupDrawing() {

        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);


        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    private Canvas imageCanvas;
    private Bitmap imageBitmap;
    private Bitmap selectedImage;
    private int w;
    private int h;
    private boolean loadFile = false;

    public void loadFile(Bitmap selectedImage) {

        this.selectedImage = selectedImage;
        loadFile = true;

        onSizeChanged(w,h,w,h);
        invalidate();

    }

    public void recreate() {
        this.selectedImage = null;
        loadFile = false;

        onSizeChanged(w,h,w,h);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        Log.i("TAG", "onSizeChanged: ");
        this.w = w;
        this.h = h;

        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        if (loadFile) {
            imageBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            imageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }


        imageCanvas = new Canvas(imageBitmap);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.i("TAG", "onDraw: ");
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawBitmap(imageBitmap, 0, 0, canvasPaint);

        canvas.drawPath(drawPath, drawPaint);
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (status) {
            case 0:
                drawLine(touchX, touchY, event);
                break;
            case 1:
                drawRectangle(touchX, touchY, event);
                break;
            case 2:
                drawCircle(touchX, touchY, event);
                break;
            case 3:
                drawTriangle(touchX, touchY, event);
        }

        invalidate();
        return true;
    }

    public void setErase(boolean isErase) {

        if (isErase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    public void setPaintColor(String color) {
        paintColor = Integer.parseInt(color);
        button.setBackgroundColor(paintColor);
        drawPaint.setColor(paintColor);
    }


    Path path = new Path();

    public void drawLine(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                path.moveTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                path.lineTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                imageCanvas.drawPath(path, drawPaint);
                drawPath.reset();
                path.reset();
                break;
        }
    }

    public void drawRectangle(float touchX, float touchY, MotionEvent event) {

        Log.i("TAG", "drawRec: " + x0 + " " + y0);
        Log.i("TAG", "drawRec: " + touchX + " " + touchY);

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.reset();
                path.reset();

                drawPath.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);

                drawPath.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);

                path.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                path.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);

                path.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
                path.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                imageCanvas.drawPath(path, drawPaint);
                drawPath.reset();
                path.reset();
                break;
        }
    }

    public void drawCircle(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:

                drawPath.reset();
                path.reset();

                if (Math.abs(touchY - y0) > Math.abs(touchX - x0)) {
                    if (y0 < touchY) {
                        drawPath.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                    }
                } else {
                    if (x0 < touchX) {
                        drawPath.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                    }
                }

                if (Math.abs(touchY - y0) > Math.abs(touchX - x0)) {
                    if (y0 < touchY) {
                        path.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                    } else {
                        path.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                    }
                } else {
                    if (x0 < touchX) {
                        path.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                    } else {
                        path.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                imageCanvas.drawPath(path, drawPaint);
                drawPath.reset();
                path.reset();
                break;
        }
    }

    public void drawTriangle(float touchX, float touchY, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:

                drawPath.reset();
                path.reset();

                if (x0 < touchX) {
                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo((x0 - Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);
                } else {

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo((x0 + Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);

                }

                if (x0 < touchX) {
                    path.moveTo(x0, y0);
                    path.lineTo(touchX, touchY);

                    path.moveTo(x0, y0);
                    path.lineTo((x0 - Math.abs(x0 - touchX)), touchY);

                    path.lineTo(touchX, touchY);
                } else {

                    path.moveTo(x0, y0);
                    path.lineTo(touchX, touchY);

                    path.moveTo(x0, y0);
                    path.lineTo((x0 + Math.abs(x0 - touchX)), touchY);

                    path.lineTo(touchX, touchY);

                }

                break;

            case MotionEvent.ACTION_UP:

                drawCanvas.drawPath(drawPath, drawPaint);
                imageCanvas.drawPath(path, drawPaint);
                drawPath.reset();
                path.reset();
                break;
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSize(int i) {
        drawPaint.setStrokeWidth(i);
    }

    Button button;

    public void setButton(Button button) {
        this.button = button;
    }
}
