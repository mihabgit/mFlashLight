package com.example.mtanim.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    ImageButton btnSwitch;
    Button button;

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSwitch = (ImageButton)findViewById(R.id.btnSwitch);

//        button = (Button)findViewById(R.id.btnBlink);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash){
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
            return;
        }

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn){
                    turnOffFlash();
                }else {
                    turnOnFlash();
                }
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                blink(50,5);
//            }
//        });
    }

    private void getCamera(){
        if (camera == null){
            try{
                camera = Camera.open();
                params = camera.getParameters();
            }catch (RuntimeException e){
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }

    private void turnOnFlash(){
        if (!isFlashOn){
            if (camera == null || params == null){
                return;
            }
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            toggleButtonImage();
        }
    }

    private void turnOffFlash(){
        if (isFlashOn){
            if (camera == null || params == null){
                return;
            }

            playSound();

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            toggleButtonImage();
        }
    }

    private void toggleButtonImage(){
        if (isFlashOn){
            btnSwitch.setImageResource(R.drawable.on2);
        }else {
            btnSwitch.setImageResource(R.drawable.off2);
        }
    }

    private void playSound(){
        if (isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.switch_on);
        }else {
            mp = MediaPlayer.create(MainActivity.this, R.raw.switch_off);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

//    private void blink(final int delay, final int times) {
//        Thread t = new Thread() {
//            public void run() {
//                try {
//
//                    for (int i=0; i < times*2; i++) {
//                        if (isFlashOn) {
//                            turnOffFlash();
//                        } else {
//                            turnOnFlash();
//                        }
//                        sleep(delay);
//                    }
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        t.start();
//    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();

        turnOffFlash();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart(){
        super.onStart();

        getCamera();
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (camera != null){
            camera.release();
            camera = null;
        }
    }


}
