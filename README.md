# Android 保活库

取名来源：来自于海贼王不死鸟果实主人“马尔科”，故为 marco

[1688 保活文档](https://aliyuque.antfin.com/yuanmingliang.yml/ug6pab)

# 模块介绍

## marco
保活能力库
```text
拉活频率: 每日最多拉 10 次，可云控
```

## marco_tracer
保活评测工具

## marco_debug
保活调试工具，不进集成，测试时通过摩天轮添加

## 外部关闭保活
```aidl
关闭保活
adb shell touch /data/local/tmp/.bWFy
删除关闭开关
adb shell rm /data/local/tmp/.bWFy
```