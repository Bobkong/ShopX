package com.squareup.shopx.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtil {

    public static Bitmap mBitmap;
    public static void getBitmap(final String path) {
        new Thread() {
            @Override
            public void run() {
                try {//子线程处理联网耗时操作
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    byte[] result = baos.toByteArray();
                    System.out.println("length--------------------->" + result.length);
                    mBitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

}
