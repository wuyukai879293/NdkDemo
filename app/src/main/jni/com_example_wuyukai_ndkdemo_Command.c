//
// Created by wuyukai on 16/7/18.
//

#include <jni.h>
#include "com_example_wuyukai_ndkdemo_Command.h"
JNIEXPORT jstring JNICALL Java_com_example_wuyukai_ndkdemo_Command_getStringFromC

        (JNIEnv *env, jobject obj){
   return (*env)->NewStringUTF(env,"这里是来自c的string");
}