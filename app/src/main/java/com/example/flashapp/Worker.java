package com.example.flashapp;

import android.hardware.Camera;

public class Worker implements Runnable {
    private Camera camera;
    private String code;
    private static long dotsTime = 500;
    private static long linesTime = 1000;

    private void flashOff(){
        if(camera != null){
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

    private void flashOn(){
        if(camera != null){
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

    public Worker(Camera camera , String morsecode){
        this.camera = camera;
        this.code = morsecode;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < this.code.length(); ++i){
                flashOn();
                if(code.charAt(i) == '.'){
                    Thread.sleep(dotsTime);
                }
                else{
                    Thread.sleep(linesTime);
                }
                flashOff();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
