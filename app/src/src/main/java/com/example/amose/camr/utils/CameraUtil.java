package com.example.amose.camr.utils;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;

public class CameraUtil {
    private static final String TAG = "CameraUtil";
    public static Camera mCamera;
    public static PreviewCallback mPreviewCallback;
    /**
     * 初始化摄像头
     */
    public static Camera initCamera(SurfaceHolder sfh){
        Log.e("camr"," 初始化摄像头111");
        if(CamConstant.InitCamSuccess){
            return mCamera;
        }else {
            Log.e("camr"," 初始化摄像头2222else");
            if(null != mCamera){
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
            mCamera = Camera.open(0);
            if(mCamera != null){
                try{
                    mCamera.setPreviewDisplay(sfh);
                    mCamera.setDisplayOrientation(90);
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPreviewSize(CamConstant.PREVIEW_WIDTH, CamConstant.PREVIEW_HEIGHT);//设置预览图片的大小
                    parameters.setPictureSize(CamConstant.PICTRUE_WIDTH, CamConstant.PICTRUE_HEIGHT);
                    parameters.setPreviewFrameRate(CamConstant.PREVIEW_RATE);//设置每秒30帧
                    List<Integer> frameRates = parameters.getSupportedPreviewFrameRates();
                    for(int i : frameRates){
                        if(i > CamConstant.PREVIEW_RATE){
                            CamConstant.PREVIEW_RATE = i;
                        }
                    }
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    parameters.setJpegQuality(100);
                    parameters.set("orientation", "portrait");
                    parameters.set("rotation", 90);
                    mCamera.setPreviewCallback(new PreviewCallback(){

                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            // TODO Auto-generated method stub

                        }

                    });
                    mCamera.startPreview();
                    mCamera.autoFocus(null);
                    CamConstant.InitCamSuccess = true;
                }catch(Exception e){
                    CamConstant.InitCamSuccess = false;
                }
            }
            return mCamera;
        }
    }
    /**
     * 设置预览数据的回调
     */
    private static void setPreviewListener(){
        mCamera.setPreviewCallback(new PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // TODO Auto-generated method stub

            }
        });
    }
    /**
     * 释放摄像头
     */
    public static void recycleCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * 设置监听回调-观察者
     */
    public static void setPreviewCallback(Camera.PreviewCallback previewCallback){
        mPreviewCallback = previewCallback;
        if(mCamera != null){
            mCamera.setPreviewCallback(mPreviewCallback);
        }
    }
}
