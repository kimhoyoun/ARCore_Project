package com.example.arproject;

import android.content.Context;
import android.opengl.Matrix;

public class Planet extends ObjRenderer{

    String name;
    int distance;
    float[] myMatrix;

    float sunDistance;
    float startAngle;
    float revolutionAngleRatio;
    float rotateAngleRatio;

    public Planet() {
        super();
    }

    public Planet(Context context, String objName, String textureName, String mName, int mDistance, float sunDistance, float startAngle, float revolutionAngleRatio, float  rotateAngleRatio) {
        super(context, objName, textureName);
        this.name = mName;
        this.distance = mDistance;
        this.myMatrix = new float[16];
        this.sunDistance = sunDistance;
        this.startAngle = startAngle;
        this.revolutionAngleRatio = revolutionAngleRatio;
        this.rotateAngleRatio = rotateAngleRatio;
    }



    public void initPlanet(float[] parentMatrix, int currentTime, int endTime){
        Matrix.translateM(myMatrix, 0, parentMatrix,0, (currentTime-100)*(sunDistance/endTime)*(float) Math.cos((startAngle) * Math.PI / 180),0f,
                (currentTime-100)*(sunDistance/endTime)*(float) Math.sin((startAngle) * Math.PI / 180));


        this.setModelMatrix(myMatrix);

    }

    public void movePlanet(float[] parentMatrix, double sunAngle, double revolutionAngle, float rotateAngle){
        Matrix.translateM(myMatrix, 0, parentMatrix,0, sunDistance*(float) Math.cos((revolutionAngle*revolutionAngleRatio-sunAngle + startAngle) * Math.PI / 180),0f,
                sunDistance*(float) Math.sin(((revolutionAngle*revolutionAngleRatio-sunAngle + startAngle)) * Math.PI / 180));

        Matrix.rotateM(myMatrix, 0, rotateAngle*rotateAngleRatio, 0f, 1f, 0f);

        this.setModelMatrix(myMatrix);

    }

    public void removePlanet(float[] parentMatrix, int currentTime, int endTime, double sunAngle, double revolutionAngle){
        Matrix.translateM(myMatrix, 0, parentMatrix,0, (sunDistance-(currentTime)*(sunDistance/endTime))*(float) Math.cos((revolutionAngle*revolutionAngleRatio-sunAngle + startAngle) * Math.PI / 180),0f,
                (sunDistance-(currentTime)*(sunDistance/endTime))*(float) Math.sin(((revolutionAngle*revolutionAngleRatio-sunAngle + startAngle)) * Math.PI / 180));


        this.setModelMatrix(myMatrix);
    }

}
