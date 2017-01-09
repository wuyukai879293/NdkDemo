package com.example.wuyukai.ndkdemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyukai on 16/12/13.
 */
public class AccelerometerSensor {
    private static final String TAG = "TestSensor";
    private static final int START_SHAKE = 0x1;
    private SensorManager sensorManager;
    private Handler mHandler;
    private MEMS mems;
    private EDTW edtw = new EDTW();
    private int[] arrayX;
    private int[] arrayZ;
    private boolean runInMainAc = true;
    public boolean isRunInMainAc(){

        if(runInMainAc){
            Log.i("runMsg","main");
        }else {
            Log.i("runMsg","gescoll");
        }
        return runInMainAc;
    }
    public void genTempTool(Context context,int USER_ID){
        edtw.genTemplate(context,USER_ID);
    }
    public void setRunInMainAc(){
        sensorOn();
        runInMainAc = true;

    }
    public void setRunInGesAc(){
        sensorOn();
        runInMainAc = false;
    }
    public AccelerometerSensor(SensorManager sensorManager,Handler handler,MEMS mems){
        this.sensorManager = sensorManager;
        this.mHandler = handler;
        this.mems = mems;
    }

    public void sensorOn() {
        sensorManager.registerListener(mSensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void sensorOff() {
        sensorManager.unregisterListener(mSensorEventListener);
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();

            if (type == Sensor.TYPE_ACCELEROMETER) {
//                float[] values = event.values;
                float x = -event.values[0]/10;
                float y = -event.values[1]/10;
                float z = -event.values[2]/10;
                //Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
                mems.MEMS_Data(x, z);

//                textViewY.setText(String.valueOf(y));
                if (mems.isDataFinished()) {
                    mems.resetDataFinish();
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            super.run();
                            saveAsGesLib();
                            if(isRunInMainAc()){
                                int i = edtw.edtwProcessPro(getArrayX(),getArrayZ(),mems);
                                String s = edtw.getUserName(i);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ges_id",i);
                                bundle.putString("ges_name",s);
                                Message msg = mHandler.obtainMessage();
//                                msg.obj = i;
                                msg.obj = bundle;
                                msg.what = MainActivity.MESSAGE_GES_TYPE;
                                mHandler.sendMessage(msg);
                                Log.i(TAG,i+"");
                            }else {
                                List<int []> sendList = new ArrayList<>();
                                sendList.add(getArrayX());
                                sendList.add(getArrayZ());
                                Message msg = mHandler.obtainMessage();
                                msg.obj = sendList;
                                mHandler.sendMessage(msg);
                                Log.i(TAG,"本条数据采集完成");
                            }
                            mems.Reg_Init();
                            mems.Reg_Groud_Init();
                        }
                    };
                    thread.start();
//                    textViewZ.setText(String.valueOf(z));
//                    gesType = Recognition(mems);
//                    switch (type){
//                        case 1:
//                            output.setText("向右甩动");
//                            switch (mode){
//                                case COMMON:
//                                    str = "04";
//                                    break;
//                                case PICTURE:
//                                    str = "22";
//                                    break;
//                                case PPT:
//                                    str = "12";
//                                    break;
//                            }
//                            break;
//                        case 2:
//                            output.setText("向左甩动");
//                            switch (mode){
//                                case COMMON:
//                                    str = "03";
//                                    break;
//                                case PICTURE:
//                                    str = "21";
//                                    break;
//                                case PPT:
//                                    str = "11";
//                                    break;
//                            }
//                            break;
//                        case 3:
//                            output.setText("向上甩动");
//                            switch (mode){
//                                case COMMON:
//                                    str = "01";
//                                    break;
//                                case PICTURE:
//                                    str = "00";
//                                    break;
//                                case PPT:
//                                    str = "00";
//                                    break;
//                            }
//                            break;
//                        case 4:
//                            output.setText("向下甩动");
//                            switch (mode){
//                                case COMMON:
//                                    str = "02";
//                                    break;
//                                case PICTURE:
//                                    str = "00";
//                                    break;
//                                case PPT:
//                                    str = "00";
//                                    break;
//                            }
//                            break;
//                        case 5:
//                            output.setText("顺时针画圈");
//                            switch (mode){
//                                case COMMON:
//                                    str = "00";
//                                    break;
//                                case PICTURE:
//                                    str = "25";
//                                    break;
//                                case PPT:
//                                    str = "14";
//                                    break;
//                            }
//                            break;
//                        case 6:
//                            output.setText("逆时针画圈");
//                            switch (mode){
//                                case COMMON:
//                                    str = "00";
//                                    break;
//                                case PICTURE:
//                                    str = "00";
//                                    break;
//                                case PPT:
//                                    str = "13";
//                                    break;
//                            }
//                            break;
//                        case 7:
//                            output.setText("逆时针画矩形");
//                            str = "00";
//                            break;
//                        case 8:
//                            output.setText("顺时针画矩形");
//                            str = "00";
//                            break;
//                        default:
//                            str = "00";
//                            break;
//
//                    }
//                    masterStop = true;
////                        displayButton.setText(type + "");
//                    //setupChat();
//                    if(mType == TypeBluetooth.Client){
//                        //str = getType() + "";
//                        sendMessage(str);
//                    } else{
//
//                        if(dataArrive){
//                            sendMessage(getType() + "+" + msgFromSlave);
//                            msgFromSlave = 0;
//                            dataArrive = false;
//                            masterStop = false;
//
//                        }
////                            str = type + "";
//                        //type = type  + msgFromSlave;
//                        //str = type + "";
//
//                        // sendMessage(getType() + "+" + msgFromSlave);
//                        // msgFromSlave = 0;
//                    }

//                    mems.Reg_Init();
//                    mems.Reg_Groud_Init();

                    //str = "x:" + x + "   " + "y:" + y + "    " + "z:" + z + "    ";
                    //str =  x + " "  + y + " " +  z + "\n";
                    //str = type + "";
                    //msgBuffer = str.getBytes();
                    //sendMessage(str);
                    //mChatService.write(msgBuffer);
                    //isStart = false;
                    //isStop = false;
//                if ((Math.abs(x - lastX) > speed || Math.abs(y - lastY) > speed) && !getShakeState()){
//                    isShake = true;
//                    Thread thread = new Thread() {
//                        @Override
//                        public void run() {
//
//
//                            super.run();
//                            try {
//                                Log.d(TAG, "onSensorChanged: 摇动");
//
//                                //开始震动 发出提示音 展示动画效果
//                                mHandler.obtainMessage(START_SHAKE).sendToTarget();
//
//                                //再来一次震动提示
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//                    thread.start();
//                }
//                lastX = x;
//                lastY = y;
//                lastY = z;

                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) { /* empty */
        }
    };
    public void saveAsGesLib(){
        int n= 0;
        int i;
        n = mems.Length();
        arrayX = new int[mems.Size];
        arrayZ = new int[mems.Size];
        if(mems.Length()<=45){
            mems.Resample(n, mems.Size/2);
            mems.Normalize(30, 1, mems.Size/2);
            for(i =0;i<mems.Size/2;i++){
                arrayX[i] = (int) mems.Tranfrom(i, 0);
//                Log.i("resample",arrayX[i]+"");
                arrayZ[i] = (int) mems.Tranfrom(i, 2);
//                Log.i("sample",arrayZ[i]+"");

            }
        }else{
            mems.Resample(n, mems.Size);
            mems.Normalize(30, 1, mems.Size);
            for(i =0;i<mems.Size;i++){
                arrayX[i] = (int) mems.Tranfrom(i, 0);
                arrayZ[i] = (int) mems.Tranfrom(i, 2);
//                Log.i("resample",arrayX[i]+"");
//                Log.i("resample",arrayZ[i]+"");

            }
        }

    }
    public int[] getArrayX(){
        return arrayX;
    }
    public int[] getArrayZ(){
        return arrayZ;
    }
}
