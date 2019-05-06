# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#性能优化及混淆参数配置
-optimizationpasses 5
-printmapping out.map
-dontnote
-dontwarn
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep public class org.apache.commons.collections4.map.**{*;}
-keep public class org.apache.commons.collections4.queue.CircularFifoQueue{public *;}
#忽略警告
-ignorewarnings
-dontshrink
#保护泛型
-keepattributes Signature
#
#-keep class net.sqlcipher.**{*;}
#-dontwarn net.sqlcipher.*

-keep class wseemann.media.**{*;}
-dontwarn wseemann.media.*

-keepattributes *Annotation*
-keep @android.support.annotation.Keep class **{
@android.support.annotation.Keep <fields>;
}