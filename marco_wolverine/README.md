# Wolverine(金刚狼)S1
> 寓意是让你的Android应用拥有不死的能力

目前的金刚狼是S1版本，经测试能在国内机器(华为 Android 10)(recent按键情况下 以及设置force-stop情况下)达到90%以上的存活率。
> 存活的概念是杀死又重新启动

## 使用方法

1. 在Application中注册WolverineKeepAlive服务
```
@Override
protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    WolverineKeepAlive.attachBaseContext(base, LockScreenService.class);
}
```

2. ResidentService对应的进程名是":resident"，或者其它任意命名, LockScreenService需要继承DaemonBaseService
```
<service
android:name="com.wolverine.keepalive.ResidentService"
android:enabled="true"
android:exported="false"
android:launchMode="singleInstance"
android:priority="1000"
android:process=":resident">
     <intent-filter>
            <action android:name="com.wolverine.keepalive.ResidentService" />
     </intent-filter>
</service>
```
3. 在合适的地方，启动ResidentService，它将自动唤醒保活进程
```
try {
      Intent residentIntent = new Intent(this, ResidentService.class);
      ContextCompat.startForegroundService(this, residentIntent);
} catch (Exception e) {
	    e.printStackTrace();
}
```


4. app.gradle 增加manifestPlaceholders属性PACKAGE_NAME为applicationId
```
android {
        defaultConfig {
            manifestPlaceholders = [
                    PACKAGE_NAME  : applicationId
            ]
        }
 }
```
5. done 完成你的应用已经不死了，不信就试试吧
## 应对方法

系统有两种思路可以选择：

加入在 force-stop 期间不允许启动新的进程的逻辑
修改 force-stop 的杀进程逻辑为：预先收集好所有进程再进行 kill（如有必要还可以先发送 SIGSTOP）

## 测试

项目根目录下的kill_alive.sh用于重复杀进程测试。

## 小瑕疵
1. 进程数量问题，该保活库会启动3个保活进程，如果算上ResidentService所在进程就是4个进程
2. 如果恰好所有进程被同时杀死，那么保活无效

## 实现原理

> 实现原理参见以下文章，但是Wolverine的实现链接中的描述并不完全相同，仅作为参考
- [Android 黑科技保活实现原理揭秘](http://weishu.me/2020/01/16/a-keep-alive-method-on-android/)
- [深度剖析App保活案例](http://www.52im.net/forum.php?mod=viewthread&tid=2893&highlight=%B1%A3%BB%EE)
- [Android黑科技保活的技术实现](https://juejin.im/post/5e820b61e51d45470652e7b8)

## S2规划
目前S1的版本是通过java 反射与系统Binder交互，性能略差，这也是在某些机器上无法保活的原因之一，所以后续想改成native ioctl与系统Binder交互。


