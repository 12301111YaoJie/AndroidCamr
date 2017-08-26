package com.example.amose.camr;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.amose.camr.utils.CamConstant;
import com.example.amose.camr.utils.CameraUtil;
import com.example.amose.camrservernew.MainApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CamThreadService extends Service {
    Socket mClintSocket;
    ServerSocket serverSocket;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Log.e("camr","begin---11111");
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.e("camr","accept000");
                    sendMSG("accept1111");
                    serverSocket = new ServerSocket(CamConstant.Camera_Port);
                    while(true){
                        Log.e("camr","accept0001111");
                        Log.e("camr","accept11111");
                        sendMSG("accept222");
                        mClintSocket = serverSocket.accept();
                        Log.e("camr","accept22222");
                        Log.e("camr","accept3333");
                        new Thread(){
                            public void run() {
                                if(mClintSocket != null){
                                    sendMSG("accept3333");
                                    //MainApplication.mHandler.sendEmptyMessage(CamConstant.INIT_CAMERA);
                                    Log.e("camr","accept4444");

                                    boolean flag = true;
                                    while(flag){
                                        if(CameraUtil.mCamera != null){
                                            try {
                                                CameraUtil.mCamera.setPreviewCallback(new StreamIt(mClintSocket));
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            flag = false;
                                            mClintSocket = null;
                                        }
                                    }
                                }
                            };
                        }.start();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("camr","Ö®ÐÐ±¨´í Í£Ö¹");
                    e.printStackTrace();
                }

            }
        }).start();
        super.onCreate();

    }
    /**
     * ÊÓÆµÁ÷Êý¾ÝÏà¹Ø»Øµ÷
     */
    private class StreamIt implements PreviewCallback {
        private OutputStream out;
        private InputStream in;
        private int  times = 0;
        public  StreamIt(Socket s) throws IOException {
            // TODO Auto-generated constructor stub
            this.out = s.getOutputStream();
            this.in = s.getInputStream();
            sendMSG("Ñ¹ËõºóµÄÊý¾Ý11");

            int[] textures = new int[1];
            Log.e("camr","Ñ¹ËõºóµÄÊý¾Ý1");
        }
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Size size = camera.getParameters().getPreviewSize();
            try {
                Log.e("camr","Ñ¹ËõºóµÄÊý¾Ý1.5");
                if(times == 0){
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    if(image != null){
                        //ÕâÊÇÔ´Êý¾Ý
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, outputStream);
                        byte[] srcData = outputStream.toByteArray();
                        int len = srcData.length;
                        Bitmap src = BitmapFactory.decodeByteArray(srcData, 0, len);
                        //Ñ¹ËõºóµÄÊý¾Ý
                        byte[] outdata = transImage(src, 640, 480);
                        Log.e("camr","Ñ¹ËõºóµÄÊý¾Ý2");
                        sendMSG("Ñ¹ËõºóµÄÊý¾Ý22");
                        int datalen = outdata.length;
                        out.write((byte) 0xA0);
                        out.write(intTOBytes(datalen));
                        out.write(outdata, 0, datalen);
                        if(!src.isRecycled()){
                            src.recycle();
                        }

                    }
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        /**
         * Êý¾Ý×ª»»£¬½«bitmap×ª»»Îªbyte
         */
        private byte[] transImage(Bitmap bitmap, int width, int height){
            try{
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                //Ëõ·ÅÍ¼Æ¬µÄ³ß´ç
                float scaleWidth = (float) width/bitmapWidth;
                float scaleHeight = (float) height/bitmapHeight;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                //²úÉúËõ·ÅºóµÄBitmap¶ÔÏó
                Bitmap resizeBitemp = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resizeBitemp.compress(CompressFormat.JPEG, 85, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                outputStream.close();
                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }
                if(!resizeBitemp.isRecycled()){
                    resizeBitemp.recycle();
                }
                return byteArray;

            }catch(Exception ex){
                ex.printStackTrace();
                MainApplication.mHandler.sendEmptyMessage(CamConstant.RECYCLE_CAMERA);
            }
            return null;
        }
        /**
         * ½«int ÊýÖµ×ª»»Îª4¸ö×Ö½Ú
         */
        private byte[] intTOBytes(int value){
            Log.e("cam",value+"d");
            byte[] src = new byte[4];
            src[3] = (byte) ((value >> 24) & 0xFF);
            src[2] = (byte)((value >> 16) & 0xFF);
            src[1] = (byte)((value >> 8) & 0xFF);
            src[0] = (byte)(value & 0xFF);
            return src;
        }
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        try {
            if(mClintSocket !=null){
                mClintSocket.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MainApplication.mHandler.sendEmptyMessage(CamConstant.RECYCLE_CAMERA);
        super.onDestroy();
    }
    public void sendMSG(String text){
        Message msg = new Message();
        msg.what = 908;
        msg.obj = text;
        MainApplication.mHandler.sendMessage(msg);
    }

}