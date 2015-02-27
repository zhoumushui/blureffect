package com.zms.blureffect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.lang.reflect.Field;

public class Main extends Activity {
    private int scaleWidth = 100;
    private int scaleHeight = 100;
    private int radius = 5;
    private Drawable blurImage;
    private String TAG = "BLUR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setMax(24);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        Display display = this.getWindowManager().getDefaultDisplay();
        scaleWidth = display.getWidth();
        scaleHeight = (display.getHeight() - getStatusBarHeight()) / 2;
        blurImage = getResources().getDrawable(R.drawable.note); // 要模糊的图片
        BlurImage(blurImage, radius);
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        // boolean fromUser 是用户操作进度条，还是代码中setProgress
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            System.out.println(progress);
            if (seekBar.getProgress() > 0) {
                BlurImage(blurImage, seekBar.getProgress());
            } else {
                // 当模糊半径为0时显示原图
                ImageView imgAfter = (ImageView) findViewById(R.id.imgAfter);
                Bitmap bitmapAfter = ((BitmapDrawable) blurImage).getBitmap();
                Bitmap bitmapAfterAdjust = Bitmap.createScaledBitmap(bitmapAfter, scaleWidth,
                        scaleHeight, true); // 调整大小
                imgAfter.setImageBitmap(bitmapAfterAdjust);
            }
        }

        // 当用户开始滑动进度条时
        public void onStartTrackingTouch(SeekBar seekBar) {
            System.out.println("Start-->" + seekBar.getProgress());
        }

        // 当用户结束滑动进度条时
        public void onStopTrackingTouch(SeekBar seekBar) {
            System.out.println("Stop-->" + seekBar.getProgress());
        }
    }

    public void BlurImage(Drawable blurImage, int radius) {
        // 模糊前：
        ImageView imgBefore = (ImageView) findViewById(R.id.imgBefore);
        // Bitmap bitmapBefore =
        // BitmapFactory.decodeResource(getResources(),R.drawable.note); // 方法一
        Bitmap bitmapBefore = ((BitmapDrawable) blurImage).getBitmap(); // 方法二：drawable转成bitmap
        Bitmap bitmapBeforeAdjust = Bitmap.createScaledBitmap(bitmapBefore,
                scaleWidth, scaleHeight, true);
        imgBefore.setImageBitmap(bitmapBeforeAdjust);
        // 模糊后：
        ImageView imgAfter = (ImageView) findViewById(R.id.imgAfter);
        Bitmap bitmapAfter = Blur.fastblur(this, bitmapBefore, radius); // 第三个参数是模糊半径：0<radius<25
        Bitmap bitmapAfterAdjust = Bitmap.createScaledBitmap(bitmapAfter,
                scaleWidth, scaleHeight, true); // 调整大小
        imgAfter.setImageBitmap(bitmapAfterAdjust);
    }

    /**
     * 获取状态栏的高度
     *
     * @return int
     */
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            Log.d(TAG, " !!! get status bar height failed");
            e1.printStackTrace();
            return 75;
        }
    }
}
