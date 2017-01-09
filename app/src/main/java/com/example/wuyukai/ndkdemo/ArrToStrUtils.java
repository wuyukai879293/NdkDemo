package com.example.wuyukai.ndkdemo;

/**
 * Created by wuyukai on 16/12/18.
 */
public class ArrToStrUtils {
    public static String implodeArray(int[] inputArray, String glueString) {
        /** Output variable */
        String output = "";
        if (inputArray.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);
            for (int i=1; i<inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }
            output = sb.toString();
        }
        return output;
    }

    public static int[] explodedArray(String str){
        int[] output = new int[30];
        String[] out = str.split(",");
        for(int i = 0;i<out.length;i++){
            output[i] = Integer.valueOf(out[i]);
        }
        return output;
    }
}
