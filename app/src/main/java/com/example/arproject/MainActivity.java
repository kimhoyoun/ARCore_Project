package com.example.arproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    final String INIT = "init";                         // 초기상태 (이미지 인식 가능)     이미지 인식만 가능 (버튼 보이지 않음)
    final String IMAGE_SCAN = "imageScan";              // 이미지 스캔 (태양 떠오르는 중)  이미지 인식 불가, 나가기, 공전 자전 버튼 보이기
    final String RISING_SUN = "risingSun";              // 태양이 다 떠오른 후 (돌아요)    행성 obj 출력
    final String PLANET_MOVE_OFF = "off";               // 행성 배치 완료                  이미지 인식 불가, 초기화, 공전/자전, 정보조회, 거리조회 가능
    final String PLANET_MOVE_ON = "on";                 // 행성 공전, 자전 상태            이미지 인식 불가, 초기화, 공전/자전, 정보조회, 거리조회 가능
    final String PLANET_INFO = "info";                  // 행성 정보조회                   이미지 인식 불가, 공전/자전 멈춤, 거리조회, 초기화 불가, 기존 obj draw off
    // ui변경, 선택된 행성 obj 생성, 정보창 VISIBLE, 행성 rotateM() 가능, 나가기 버튼 -> 이전 상태(move on/off) 이동
    final String PLANET_DISTANCE_ONE = "distanceOne";   // 행성 간 거리조회 1회 터치       이미지 인식 불가, 공전/자전 멈춤, 정보조회, 초기화 불가, 새로고침(취소)버튼 생성, 깃발 변경 가능
    final String PLANET_DISTANCE_INFO = "distanceInfo"; // 행성 간 거리 조회 중            이미지 인식 불가, 공전/자전 멈춤, 정보조회, 초기화 불가, 새로고침 가능, 새로고침 후 이전 상태(on/off)
    final String PLANET_REMOVE = "remove";              // 행성 삭제 (블랙홀)              이미지 인식 불가, 공전/자전 멈춤, 정보조회, 초기화 불가, 일정시간 후 init 상태 도달

    String prevState = "";
    String state = INIT;

    ImageButton rotateBtn;

    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true;
    LinearLayout btnLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarAndTitleBar();
        setContentView(R.layout.activity_main);

        mSurfaceView = findViewById(R.id.glsurfaceview);
        rotateBtn = findViewById(R.id.rotateBtn);
        btnLayout = findViewById(R.id.btnLayout);
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);

        if(displayManager != null){
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int displayId) {

                }

                @Override
                public void onDisplayRemoved(int displayId) {

                }

                @Override
                public void onDisplayChanged(int displayId) {
                    synchronized(this){
                        mRenderer.mViewportChanged = true;
                    }
                }
            },null);
        }

        mRenderer = new MainRenderer(this, new MainRenderer.RenderCallback() {
            @Override
            public void preRender() {
                if(mRenderer.mViewportChanged){
                    Display display = getWindowManager().getDefaultDisplay();
                    int displayRotation = display.getRotation();
                    mRenderer.updateSession(mSession, displayRotation);
                }

                mSession.setCameraTextureName(mRenderer.getTextureId());

                Frame frame = null;
                try{
                    frame = mSession.update();
                } catch (CameraNotAvailableException e){
                    e.printStackTrace();
                }

                if(frame.hasDisplayGeometryChanged()){
                    mRenderer.mCamera.transformDisplayGeometry(frame);
                }

//                mRenderer.mObj.setModelMatrix(modelMatrix);

                Camera camera = frame.getCamera();
                float [] projMatrix = new float[16];
                camera.getProjectionMatrix(projMatrix, 0, 0.1f, 100f);
                float[] viewMatrix = new float[16];
                camera.getViewMatrix(viewMatrix, 0);

                // 이미지추적결과에 따른 그리기 설정
                if(!drawTag) {
                    drawImage(frame);


                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(state.equals(IMAGE_SCAN)||state.equals(RISING_SUN)||state.equals(PLANET_MOVE_OFF)||state.equals(PLANET_MOVE_ON)) {
                            btnLayout.setVisibility(View.VISIBLE);
                        }else{
                            btnLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                });






                mRenderer.setProjectionMatrix(projMatrix);
                mRenderer.updateViewMatrix(viewMatrix);

            }
        });

        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mSurfaceView.setRenderer(mRenderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();
        try {
            if(mSession == null){
                switch(ArCoreApk.getInstance().requestInstall(this, true)){
                    case INSTALLED:
                        mSession = new Session(this);
                        Log.d("메인", "ARCore session 생성");
                        break;
                    case INSTALL_REQUESTED:
                        Log.d("메인","ARCore 설치 필요");
                        mUserRequestedInstall = false;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mConfig = new Config(mSession);

        mConfig.setFocusMode(Config.FocusMode.AUTO);
        // 이미지데이터베이스 설정
        setUpImgDB(mConfig);


        mSession.configure(mConfig);

        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
        mSurfaceView.onResume();
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    // 이미지데이터베이스 설정
    void setUpImgDB(Config config){
        // 이미지 데이터베이스 생성
        AugmentedImageDatabase imageDatabase = new AugmentedImageDatabase(mSession);

        try {
            // 파일스트림로드
            InputStream is = getAssets().open("solarsystem.png");
            // 파일스트림에서 Bitmap 생성
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            // 이미지데이터베이스에 bitmap 추가
            imageDatabase.addImage("태양계",bitmap);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setAugmentedImageDatabase(imageDatabase);

    }

    float[] sunMatrix = new float[16];
    float[] imageMatrix = new float[16];
    // 이미지추적결과에 따른 그리기 설정
    void drawImage(Frame frame){

        mRenderer.isImgFind = false;
        // frame(카메라) 에서 찾은 이미지들을 Collection으로 받아온다.
        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        // 찾은 이미지들을 돌린다.
        for (AugmentedImage img : updatedAugmentedImages) {
            if (img.getTrackingState() == TrackingState.TRACKING) {
                mRenderer.isImgFind = true;

                state = IMAGE_SCAN;
                Pose imgPose = img.getCenterPose();

                float[] matrix = new float[16];
                imgPose.toMatrix(matrix, 0);
                float[] modelMatrix = new float[16];

                System.arraycopy(matrix, 0, imageMatrix, 0 ,16);

                mRenderer.mCube.setModelMatrix(imageMatrix);
                moveObj(matrix);
                drawTag = true;
                Log.d("obj 그리지", Arrays.toString(matrix));

                switch (img.getTrackingMethod()) {
                    case LAST_KNOWN_POSE:
                        break;
                    case FULL_TRACKING:
                        break;
                    case NOT_TRACKING:
                        break;
                }
            }
        }
    }

    boolean initStop = false;
    boolean drawTag =false;

    int endTime = 3700;
    void moveObj(float[] matrix) {
        System.arraycopy(matrix, 0, sunMatrix, 0, 16);
        Matrix.translateM(sunMatrix, 0, 0f, -0.7f, 0f);

        Log.d("sunMatrix model", Arrays.toString(matrix));
        Log.d("sunMatrix 처음", Arrays.toString(sunMatrix));
        new Thread() {
            @Override
            public void run() {
                int currentTime = 0;
                while (!initStop) {
                    if(currentTime<100) {
                        drawTag = true;
                        Matrix.translateM(sunMatrix, 0, 0f, 0.01f, 0f);
                        mRenderer.sun.setModelMatrix(sunMatrix);
                        SystemClock.sleep(100);
                        Log.d("sunMatrix 떠오른다", Arrays.toString(sunMatrix));
                    }else if( currentTime == 100){
                        mRenderer.planetDraw = true;
                        state = RISING_SUN;
                    }
                    else if (currentTime<endTime){
                        if(mRenderer.planetDraw){
                            for(Planet planet : mRenderer.planetList){
                                planet.initPlanet(sunMatrix,currentTime,endTime);
                            }

                            SystemClock.sleep(1);
                        }
                    }
                    else if(currentTime > 7301){

                        state = PLANET_MOVE_OFF;
                        Matrix.translateM(mRenderer.moon.myMatrix,  0, mRenderer.earth.myMatrix,0, 0.1f*(float) Math.cos((revolutionAngle) * Math.PI / 180),0.1f *(float) Math.sin((revolutionAngle) * Math.PI / 180),0f);
                        mRenderer.moon.setModelMatrix(mRenderer.moon.myMatrix);
                        mRenderer.moonDraw = true;
                        initStop = true;
                    }
                    currentTime++;
                }
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
        mSession.pause();

    }

    void hideStatusBarAndTitleBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void requestCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    0
            );
        }
    }

    boolean rotateFlag = true;
    boolean blackHallFlag = false;
    double sunAngle = 0;
    double revolutionAngle = 0;
    float rotateAngle = 0;

    public void rotateBtnClick(View view) {

        if (view.getId() == R.id.rotateBtn) {
            if (state.equals(PLANET_MOVE_OFF)) {
                rotateFlag = true;
                state = PLANET_MOVE_ON;

                new Thread() {
                    @Override
                    public void run() {
                        while (rotateFlag) {
                            sunAngle += 0.01;
                            revolutionAngle += 1;
                            rotateAngle += 1;
                            Matrix.rotateM(sunMatrix, 0, 0.01f, 0f, 1f, 0f);

//                        Matrix.translateM(mercuryMatrix, 0, sunMatrix,0, 0.4f*(float) Math.cos(((earthAngle*2-sunAngle + mercuryStartAngle)) * Math.PI / 180),0f,
//                                0.4f*(float) Math.sin(((earthAngle*2-sunAngle + mercuryStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(venusMatrix, 0, sunMatrix,0, 0.6f*(float) Math.cos(((earthAngle*1.5-sunAngle + venusStartAngle)) * Math.PI / 180),0f,
//                                0.6f*(float) Math.sin(((earthAngle*1.5-sunAngle + venusStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(earthMatrix, 0, sunMatrix,0, 0.9f*(float) Math.cos((earthAngle-sunAngle + earthStartAngle) * Math.PI / 180),0f,
//                                0.9f*(float) Math.sin(((earthAngle-sunAngle + earthStartAngle)) * Math.PI / 180));
//                        Matrix.rotateM(earthMatrix, 0, rotateAngle, 0f, 1f, 0f);
//                        Matrix.translateM(marsMatrix, 0, sunMatrix,0, 1.2f*(float) Math.cos(((earthAngle/1.5f-sunAngle + marsStartAngle)) * Math.PI / 180),0f,
//                                1.2f*(float) Math.sin(((earthAngle/1.5f-sunAngle + marsStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(jupiterMatrix, 0, sunMatrix,0, 1.5f*(float) Math.cos(((earthAngle/2f-sunAngle + jupiterStartAngle)) * Math.PI / 180),0f,
//                                1.5f*(float) Math.sin(((earthAngle/2f-sunAngle + jupiterStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(saturnMatrix, 0, sunMatrix,0, 2f*(float) Math.cos(((earthAngle/2.5f-sunAngle + saturnStartAngle)) * Math.PI / 180),0f,
//                                2f*(float) Math.sin(((earthAngle/2.5f-sunAngle + saturnStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(uranusMatrix, 0, sunMatrix,0, 2.3f*(float) Math.cos(((earthAngle/3f-sunAngle + uranusStartAngle)) * Math.PI / 180),0f,
//                                2.3f*(float) Math.sin(((earthAngle/3f-sunAngle + uranusStartAngle)) * Math.PI / 180));
//                        Matrix.translateM(neptuneMatrix, 0, sunMatrix,0, 2.6f*(float) Math.cos(((earthAngle/3.5f-sunAngle + neptuneStartAngle)) * Math.PI / 180),0f,
//                                2.6f*(float) Math.sin(((earthAngle/3.5f-sunAngle + neptuneStartAngle)) * Math.PI / 180));

                            for (Planet planet : mRenderer.planetList) {
                                planet.movePlanet(sunMatrix, sunAngle, revolutionAngle, rotateAngle);
                            }

                            Matrix.translateM(mRenderer.moon.myMatrix, 0, mRenderer.earth.myMatrix, 0, 0.1f * (float) Math.cos((revolutionAngle) * Math.PI / 180), 0.1f * (float) Math.sin((revolutionAngle) * Math.PI / 180), 0f);

                            mRenderer.sun.setModelMatrix(sunMatrix);
                            mRenderer.moon.setModelMatrix(mRenderer.moon.myMatrix);

//                        mRenderer.mercury.setModelMatrix(mercuryMatrix);
//                        mRenderer.venus.setModelMatrix(venusMatrix);
//                        mRenderer.earth.setModelMatrix(earthMatrix);
//                        mRenderer.mars.setModelMatrix(marsMatrix);
//                        mRenderer.jupiter.setModelMatrix(jupiterMatrix);
//                        mRenderer.saturn.setModelMatrix(saturnMatrix);
//                        mRenderer.uranus.setModelMatrix(uranusMatrix);
//                        mRenderer.neptune.setModelMatrix(neptuneMatrix);

                            SystemClock.sleep(50);
                        }
                    }
                }.start();
            }
        }

        else if(view.getId() == R.id.rotateStopBtn) {
            rotateFlag = false;
            state = PLANET_MOVE_OFF;
        }

        else if(view.getId() == R.id.initBtn){
            rotateFlag = false;
            blackHallFlag =true;
            mRenderer.moonDraw = false;
            state = PLANET_REMOVE;

            new Thread() {
                @Override
                public void run() {
                    int currentTime = 100;
                    while (blackHallFlag) {
                        if(currentTime<endTime) {
                            for(Planet planet : mRenderer.planetList){
                                planet.removePlanet(sunMatrix,currentTime,endTime, sunAngle, revolutionAngle);
                            }
                            SystemClock.sleep(1);
                        }else if(currentTime==endTime){
                            mRenderer.planetDraw = false;
                        }else if(currentTime<3780){
                            Matrix.translateM(sunMatrix, 0, 0f, -0.01f, 0f);
                            mRenderer.sun.setModelMatrix(sunMatrix);
                            SystemClock.sleep(100);
                            Log.d("나와야지", currentTime+"");
                        }else if(currentTime == 3780){
                            state = INIT;
                            mRenderer.isImgFind = false;
                            drawTag = false;
                            blackHallFlag = false;
                            try {
                                mSession.resume();
                            } catch (CameraNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }

                        currentTime++;
                    }
                }
            }.start();












        }
    }


}