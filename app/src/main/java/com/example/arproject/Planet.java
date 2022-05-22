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


    // 레이아웃에 들어갈 행성 정보
    String name_kor; // 한국명
    String info_distance; // 지름
    String info_surface; // 표면적
    String info_weight; // 질량
    String info_lean; // 자전축 기울기
    String info_revolve; // 공전 주기
    String info_rotate; // 자전 주기
    String info_maxCel; // 최고섭씨온도
    String info_minCel; // 최저섭씨온도
    String info_press; // 대기압
    String info_satellite; // 위성


    public Planet() {
        super();
    }

    public Planet(Context context, String objName, String textureName, String mName, int mDistance, float sunDistance, float startAngle, float revolutionAngleRatio, float  rotateAngleRatio,  String[] info_planet) {
        super(context, objName, textureName);
        this.name = mName;
        this.distance = mDistance;
        this.myMatrix = new float[16];
        this.sunDistance = sunDistance;
        this.startAngle = startAngle;
        this.revolutionAngleRatio = revolutionAngleRatio;
        this.rotateAngleRatio = rotateAngleRatio;

        this.name_kor = info_planet[0];
        this.info_distance = info_planet[1];
        this.info_surface = info_planet[2];
        this.info_weight = info_planet[3];
        this.info_lean = info_planet[4];
        this.info_revolve = info_planet[5];
        this.info_rotate = info_planet[6];
        this.info_maxCel = info_planet[7];
        this.info_minCel = info_planet[8];
        this.info_press = info_planet[9];
        this.info_satellite = info_planet[10];
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
