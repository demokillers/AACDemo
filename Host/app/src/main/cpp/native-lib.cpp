#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring
JNICALL Java_com_demokiller_host_native_JNIUtils_testJNI(JNIEnv *env, jobject) {
    std::string key = "01234567890123456789012345678901";
    return env->NewStringUTF(key.c_str());
}

