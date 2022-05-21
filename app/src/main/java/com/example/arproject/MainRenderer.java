package com.example.arproject;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.google.ar.core.Session;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {

    CameraPreView mCamera;


    ObjRenderer sun;

    Planet mercury;
    Planet venus;
    Planet earth;
    Planet moon;
    Planet mars;
    Planet jupiter;
    Planet saturn;
    Planet neptune;
    Planet uranus;

    Cube mCube;

    boolean mViewportChanged;
    int mViewportWidth, mViewportHeight;
    RenderCallback mRenderCallback;
    boolean isImgFind = false;

    boolean planetDraw = false;
    boolean moonDraw = false;
    ArrayList<Planet> planetList = new ArrayList<>();
    MainRenderer(Context context, RenderCallback callback){
        mRenderCallback = callback;
        mCamera = new CameraPreView();
        mCube = new Cube(0.3f, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 1.0f);


        sun = new ObjRenderer(context, "sun.obj", "sun.png");
        mercury = new Planet(context, "mercury.obj", "mercury.png", "mercury",0,0.4f, -20f,2f,1/4f);
        venus = new Planet(context, "venus.obj", "venus.png", "venus", 0, 0.6f, 120f,1.5f,-1/10f);
        earth = new Planet(context, "earth.obj", "earth.png","earth",0, 0.9f, -90f,1f,1f);
        moon = new Planet(context, "moon.obj", "moon.png","moon",0,0.1f,0,1f,1f);
        mars = new Planet(context, "mars.obj", "mars.png","mars",0,1.2f,-35f,1/1.5f,2.3f);
        jupiter = new Planet(context, "jupiter.obj", "jupiter.png","jupiter",0,1.5f,-63f,1/2f,2.5f);
        saturn = new Planet(context, "saturn.obj", "saturn.png","saturn",0,2f,20f,1/2.5f,2.4f);
        uranus = new Planet(context, "uranus.obj", "uranus.png","uranus",0,2.3f,-140f,1/3f,-1.3f);
        neptune = new Planet(context, "neptune.obj", "neptune.png","neptune",0,2.6f,-43f,1/3.5f,1.2f);


        planetList.add(mercury);
        planetList.add(venus);
        planetList.add(earth);
        planetList.add(mars);
        planetList.add(jupiter);
        planetList.add(saturn);
        planetList.add(uranus);
        planetList.add(neptune);
    }

    interface RenderCallback{
        void preRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f,1.0f, 0.0f, 0.0f);

        mCamera.init();
        mCube.init();
        sun.init();
        moon.init();
        for(ObjRenderer obj : planetList){
            obj.init();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderCallback.preRender();

        GLES20.glDepthMask(false);
        mCamera.draw();

        GLES20.glDepthMask(true);
        if(isImgFind){
            if(planetDraw){
                for(ObjRenderer obj : planetList){
                    obj.draw();
                }

                if(moonDraw) {
                    moon.draw();
                }
            }else{
                mCube.draw();
            }
            sun.draw();
        }
    }

    void updateSession(Session session, int displayRotation){
        if (mViewportChanged) {
            session.setDisplayGeometry(displayRotation, mViewportWidth, mViewportHeight);
            mViewportChanged = false;
        }
    }

    void setProjectionMatrix(float[] matrix){
        mCube.setProjectionMatrix(matrix);
        sun.setProjectionMatrix(matrix);
        moon.setProjectionMatrix(matrix);

        for(ObjRenderer obj : planetList){
            obj.setProjectionMatrix(matrix);
        }
    }

    void updateViewMatrix(float[] matrix){
        mCube.setViewMatrix(matrix);
        sun.setViewMatrix(matrix);
        moon.setViewMatrix(matrix);
        for(ObjRenderer obj : planetList){
            obj.setViewMatrix(matrix);
        }
    }

    int getTextureId(){
        return mCamera == null ? -1 : mCamera.mTextures[0];
    }
}
