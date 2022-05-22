package com.example.arproject;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {
    String vertexShaderString =
            "attribute vec3 aPosition ; " +
                    "uniform vec4 aColor; " +
                    "uniform mat4 uMVPMatrix; " +
                    "varying vec4 vColor; " +
                    "void main () {" +
                    "    vColor = aColor; " +
                    "    gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, aPosition.z, 1.0) ;" +
                    "}";

    String fragmentShaderString =
            "precision mediump float; " +
                    "varying vec4 vColor; " +
                    "void main() { " +
                    "   gl_FragColor = vColor; " +
                    "}";


    float[] mModelMatrix = new float[16];
    float[] mViewMatrix = new float[16];
    float[] mProjMatrix = new float[16];

    float[] mColor;

    int mNumPoints = 0;

    int maxPoints = 1000;

    float[] mPoint = new float[maxPoints * 3];

    FloatBuffer mVertices;
    int mProgram;

    boolean isInited = false;

    int[] mVbo;

    Line() {
        mColor = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
    }

    void update() {

        mVertices = ByteBuffer.allocateDirect(mPoint.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mPoint);
        mVertices.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, mNumPoints * 3 * Float.BYTES, mVertices);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    void updatePoint(float x, float y, float z) {

        if (mNumPoints >= maxPoints - 1) {
            return;
        }

        mPoint[mNumPoints * 3 + 0] = x;
        mPoint[mNumPoints * 3 + 1] = y;
        mPoint[mNumPoints * 3 + 2] = z;
        mNumPoints++;
    }

    void init() {

        mVbo = new int[1];
        GLES20.glGenBuffers(1, mVbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, maxPoints * 3 * Float.BYTES, null, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vertexShaderString);
        GLES20.glCompileShader(vShader);

        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fragmentShaderString);
        GLES20.glCompileShader(fShader);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vShader);
        GLES20.glAttachShader(mProgram, fShader);
        GLES20.glLinkProgram(mProgram);

        isInited = true;

    }


    void draw() {

        GLES20.glUseProgram(mProgram);

        int position = GLES20.glGetAttribLocation(mProgram, "aPosition");
        int color = GLES20.glGetUniformLocation(mProgram, "aColor");
        int mvp = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        float[] mvpMatrix = new float[16];
        float[] mvMatrix = new float[16];

        Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mProjMatrix, 0, mvMatrix, 0);

        GLES20.glEnableVertexAttribArray(position);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 4 * 3, 0);
        GLES20.glUniform4f(color, 1.0f, 0f, 0f, 1.0f);
        GLES20.glUniformMatrix4fv(mvp, 1, false, mvpMatrix, 0);

        GLES20.glLineWidth(30.0f);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mNumPoints);

        GLES20.glDisableVertexAttribArray(position);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    void setmModelMatrix(float[] matrix) {
        System.arraycopy(matrix, 0, mModelMatrix, 0, 16);
    }

    void updateProjMatrix(float[] projMatrix) {
        System.arraycopy(projMatrix, 0, this.mProjMatrix, 0, 16);
    }

    void updateViewMatrix(float[] viewMatrix) {
        System.arraycopy(viewMatrix, 0, this.mViewMatrix, 0, 16);
    }

}