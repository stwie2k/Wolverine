#include <jni.h>
#include <sys/wait.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/file.h>
#include <linux/android/binder.h>
#include <sys/mman.h>
#include "logger.h"
#include <string>

char *tag = "wolv_albb_c";

int lock_file(const char *lock_file_path) {
    LOGE(tag, "%s : %d ---> start try to lock file >> %s <<%s", "lock_file", 13,
         lock_file_path, "\n")
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT, S_IRUSR);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX);
    if (lockRet == -1) {
        LOGE(tag, "lock file failed >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGD(tag, "lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}


int wait_lock_file(const char *lock_file_path) {
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT, S_IRUSR);
    }
    int lockRet;
    while (true) {
        while (true) {
            lockRet = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
            LOGE(tag, "%s : %d ---> lock_file_path : %s , loop_result : %d%s",
                 "wait_file_lock",
                 37,
                 lock_file_path,
                 lockRet,
                 "\n")
            if (lockRet)
                break;
            lockRet = flock(lockFileDescriptor, LOCK_UN);
            LOGE(tag, "%s : %d ---> lock_file_path : %s , unlock_result : %d%s",
                 "wait_file_lock",
                 41,
                 lock_file_path,
                 lockRet,
                 "\n")
            sleep(1u);
        }
        if (lockRet == -1)
            break;
        usleep(0x3E8u);
    }
    LOGE(tag, "%s : %d ---> retry lock file >> %s << %d%s", "wait_file_lock",
         50,
         lock_file_path,
         lockFileDescriptor,
         "\n")

    return flock(lockFileDescriptor, LOCK_EX) != -1;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_wolverine_component_DaemonMain_lockFile(JNIEnv *env, jclass clazz, jstring str) {
    const char *jstr = env->GetStringUTFChars(str, 0);
    lock_file(jstr);
    env->ReleaseStringUTFChars(str, jstr);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_wolverine_component_DaemonMain_nativeSetSid(JNIEnv *env, jclass clazz) {
    setsid();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_wolverine_component_DaemonMain_waitFileLock(JNIEnv *env, jclass clazz,
                                                             jstring str) {
    const char *jstr = env->GetStringUTFChars(str, 0);
    wait_lock_file(jstr);
    env->ReleaseStringUTFChars(str, jstr);
}
