# lap

lap 是local audio parser缩写，意为离线媒体解析库，可提供离线解析多媒体信息的能力。


## 依赖

工程实现依赖 [FFmpegMediaMetadataRetriever](https://github.com/wseemann/FFmpegMediaMetadataRetriever)

故需在工程的build.gradle中添加依赖

```
implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14'
```

## 权限申明

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" tools:ignore="ProtectedPermissions" />
```

## 引入工程

- 添加依赖

1 在工程的根目录下的build.gradle 添加 maven { url "https://jitpack.io" }

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

2 添加 Lap 依赖

```
implementation 'com.github.hehr:lap:1.0.3'
```

- aar或者jar

1 手动执行lap-lib目录下的pack.sh 脚本

2 在工程引入out/文件夹下编译的aar文件即可(如需jar包可直接解压aar包剥离jar文件和数据库文件即可)


## 配置

1 设置音频文件扫描过滤大小限制,默认512个字节

```
Lap.getInstance().setAudioSize(512 * 1024);
```

2 设置本地解析音频文件数量限制,默认设置 500

```
Lap.getInstance().setEntryNumber(500);
```

3 添加音频扫描类型,默认已支持音频类型:mp3、ogg、wav、m4a、ape、flac、aac、wma

```
Lap.getInstance().addAudioType("mp3");
```


## 初始化

```
Lap.getInstance().initialize(this, new InitializeListener() {
            @Override
            public void onError(Error error) {
                isInit =false;
            }

            @Override
            public void onInit() {
                isInit = true;
            }
        });

```


## 开始解析

```
Lap.getInstance().scan(SD_CARD_PATH , new ScanListener(){

    @Override
    public void onResult(List<AudioBean> list) {

        Log.d(TAG , list.toString());
    }

    @Override
    public void onError(Error error) {
        Log.e(TAG , error.toString());
    }
});

```

## 解析结果

参见 AudioBean

## 异常跟踪

```
/**
 * 初始化 数据库文件不存在
 */
INIT_DB_NOT_EXISTS_ERROR(1001,"unencrypted.db not exists"),

/**
 * 初始化方法未执行
 */
INIT_NOT_EXECUTE_ERROR(1002,"Initialize method not excute"),

/**
 * 传入音频路径非文件夹
 */
SCAN_PATH_NOT_DIR_ERROR(1003,"the path not a directory"),

/**
 * 音频解析线程非法参数异常
 */
FFMAR_PARSE_ILLEGAL_ARGUMENT_ERROR(1004 , "FFMARTask task IllegalArgumentException"),

/**
 * 初始化 数据库文件拷贝IO异常
 */
INIT_DB_COPY_IO_EXCEPTION_ERROR(1015,"copy encrypted.db IOException"),

/**
 * trans 节点解析字符串异常
 */
TRANS_NODE_EXCEPTION_ERROR(1016, "trans Exception"),

/**
 * taskFactory  shutdown 之后在此提交任务
 */
EXECUTOR_SERVICE_SHUTDOWN_EXCEPTION_ERROR(1017,"ExecutorServiceShutdownException"),

/**
 *
 */
TASK_INTRRRUPTED_EXCEPTION_ERROR(1018, " InterruptedException"),

/**
 *
 */
TASK_EXECUTION_EXCEPTION_ERROR(1019 , "ExecutionException"),

```

## todo list
