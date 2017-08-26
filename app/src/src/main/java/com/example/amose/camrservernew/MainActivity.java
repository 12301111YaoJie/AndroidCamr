package com.example.amose.camrservernew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.amose.camr.CamService;
import com.example.amose.camr.CamThreadService;
import com.example.amose.camr.utils.CamConstant;
import com.example.amose.camr.utils.CameraUtil;
import com.example.amose.camr.utils.ServiceUtil;

public class MainActivity extends Activity {
    TextView tv;
    SurfaceView surfv;
    SurfaceHolder surfaceHolder;
    int screenWidth=300, screenHeight=300;
    public Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CamConstant.INIT_CAMERA:

				/*if(CamConstant.hadInit){
					return;
				}

				Camera camera = CameraUtil.initCamera(surfaceHolder);
				if( null != camera){
					CamConstant.hadInit = true;
				}*/
                    break;
                case CamConstant.RECYCLE_CAMERA:
                    CameraUtil.recycleCamera();
                    CamConstant.hadInit = false;
                    break;
                default:
                    //tv.setText((CharSequence) msg.obj);
                    tv.append((CharSequence) msg.obj+"\n");
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        MainApplication.mHandler = this.mHandler;
        tv = (TextView) findViewById(R.id.textView);
        surfv = (SurfaceView) findViewById(R.id.surview);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;// »ñÈ¡ÆÁÄ»·Ö±æÂÊ¿í¶È
        screenHeight = dm.heightPixels;
        surfaceHolder = surfv.getHolder();
        surfaceHolder.setFixedSize(screenWidth, screenHeight/4*2);
        surfaceHolder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                CameraUtil.initCamera(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // TODO Auto-generated method stub
            }
        });
        //tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        startService();
    };
    public void startService(){
        Intent intent = new Intent(MainActivity.this, CamThreadService.class);
        startService(intent);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        CameraUtil.recycleCamera();
        CamConstant.hadInit = false;
        if(ServiceUtil.isServiceRunning(this, "CamService")){
            stopService(new Intent(this, CamService.class));
        }
        super.onDestroy();
    }


}
