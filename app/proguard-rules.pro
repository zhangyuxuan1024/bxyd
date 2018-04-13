# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android\adt\adt-bundle-windows-x86_64-20131030\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#指定代码的压缩级别
-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-dontwarn com.android.support.**
-keep class com.android.support.** { *;}

-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** { *;}

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}

-dontwarn com.squareup.okio.**
-keep class com.squareup.okio.** { *;}

-dontwarn io.rong.**
-keep class io.rong.** { *;}

-dontwarn com.jauker.widget.**
-keep class com.jauker.widget.** { *;}

-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *;}

-dontwarn java.nio.file.**
-keep class java.nio.file.** { *;}

-dontwarn com.bigkoo.pickerview.**
-keep class com.bigkoo.pickerview.** { *;}

-dontwarn com.baoyz.swipemenulistview.**
-keep class com.baoyz.swipemenulistview.** { *;}

-dontwarn com.baoyz.swipemenulistview.**
-keep class com.baoyz.swipemenulistview.** { *;}

-dontwarn org.codehaus.mojo.**
-keep class org.codehaus.mojo.** { *;}

-dontwarn com.umeng.analytics.**
-keep class com.umeng.analytics.** { *;}

-dontwarn io.vov.vitamio.**
-keep class io.vov.vitamio.** { *;}

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-keep class com.tencent.mm.sdk.** { *;}

-keep class com.stx.xhb.xbanner.**{*;}

-keepclassmembers class net.iclassmate.bxyd.ui.activitys.index.WebBxActivity{
  public *;
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep class android.webkit.JavascriptInterface {*;}

-dontwarn android.net.SSLCertificateSocketFactory

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}