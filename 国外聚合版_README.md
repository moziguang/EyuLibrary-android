[https://github.com/moziguang/EyuLibrary-android.git](https://github.com/moziguang/EyuLibrary-android.git)
================
### 迁移到 AndroidX（特别需要注意原生广告）
重构命令使用两个标记。默认情况下，这两个标记在 gradle.properties 文件中都设为 true：

android.useAndroidX=true
Android 插件会使用对应的 AndroidX 库而非支持库。
android.enableJetifier=true
类库映射：
https://developer.android.com/jetpack/androidx/migrate/artifact-mappings
类的对应关系请参考：
https://developer.android.com/jetpack/androidx/migrate/class-mappings

### 项目的buld.gradle 添加以下内容
```gradle
buildscript {

    repositories {
        maven { url 'https://maven.google.com' }
        maven { url 'https://maven.fabric.io/public' }
        maven { url 'https://dl.bintray.com/umsdk/release' }
    }

    dependencies {
        classpath 'com.google.gms:google-services:4.2.0'
        classpath 'io.fabric.tools:gradle:1.26.1'
    }
}

allprojects {

    repositories {

        maven {
            url 'https://repo.rdc.aliyun.com/repository/74503-release-qNEqtU/'
            credentials {
                username 'p5gXfa'
                password 'wKY0RHNSH3'
            }
        }
        maven { url 'https://dl.bintray.com/umsdk/release' }
        maven { url "https://jitpack.io" }//vungle

    }
}
```
### module的build.gradle 添加以下内容

```gradle
apply plugin: 'com.google.gms.google-services'
apply plugin: 'io.fabric'

dependencies {
    implementation 'androidx.multidex:multidex:2.0.1'
    implementation 'androidx.annotation:annotation:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.eyu:eyulibrary:1.4.7'

}
```

### 配置google-services.json
从firebase控制台下载 google-services.json ，并复制到module根目录下

### 配置multiDexEnabled
[https://developer.android.com/studio/build/multidex](https://developer.android.com/studio/build/multidex)

### SDK使用
#### 初始化sdk
```java
SdkHelper.init(this);
SdkHelper.initUmSdk(this, "appKey", "channel");
SdkHelper.initAppFlyerSdk("key", new AppsFlyerConversionListener(){

    @Override
    public void onInstallConversionDataLoaded(Map<String, String> map) {

    }

    @Override
    public void onInstallConversionFailure(String s) {

    }

    @Override
    public void onAppOpenAttribution(Map<String, String> map) {

    }

    @Override
    public void onAttributionFailure(String s) {

    }
}, this.getApplication(), "uninstallKey");


Map<String, Object> defaultsMap = new HashMap<>();
defaultsMap.put("key","defaultValue");
EyuRemoteConfigHelper.initRemoteConfig(this, defaultsMap);
EyuRemoteConfigHelper.fetchRemoteConfig();

```

#### 广告配置
```java
    private void initAdConfig() {
        AdConfig adConfig = new AdConfig();
        /**
         * 广告位
         * {
         *     "cacheGroup": "main_view_inter_ad",
         *     "isEnabled": "true",
         *     "nativeAdLayout": "",
         *     "id": "main_view_inter_ad",
         *     "desc": "首页插屏"
         *   }
         */

        public static String NETWORK_FACEBOOK = "facebook";
        public static String NETWORK_ADMOB = "admob";
        public static String NETWORK_UNITY = "unity";
        public static String NETWORK_VUNGLE = "vungle";
        public static String NETWORK_APPLOVIN = "applovin";
        public static String NETWORK_IRONSOURCE = "ironsource";
        public static String NETWORK_MINTEGRAL = "mintegral";

        adConfig.setAdPlaceConfigStr(EyuRemoteConfigHelper.readRawString(this, R.raw.ad_setting));
        /**
         * 广告key配置
         * [
         * {"id":"adcp_js","key":"ca-app-pub-3940256099942544/1033173712","network":"admob"},
         * {"id":"adjl_jsg","key":"ca-app-pub-3940256099942544/5224354917","network":"admob"},
         * {"id":"adys_sy","key":"ca-app-pub-3940256099942544/2247696110","network":"admob"},
         * {"id":"adys_ba","key":"ca-app-pub-3940256099942544/6300978111","network":"admob"}
         * ]
         */
        adConfig.setAdKeyConfigStr(EyuRemoteConfigHelper.readRawString(this, R.raw.ad_key_setting));

        /**
         * 广告位中广告的缓存池，id与广告位id对应
         * keys：广告key配置中的key
         * isAutoLoad：是否自动加载
         * type：广告类型，interstitialAd为插屏
         * {
         *     "keys": "[\"fb_cp_g\"]",
         *     "isAutoLoad": "true",
         *     "id": "main_view_inter_ad",
         *     "type": "interstitialAd"
         *   }
         */
        adConfig.setAdGroupConfigStr(EyuRemoteConfigHelper.readRawString(this, R.raw.ad_cache_setting));
        adConfig.setAdmobClientId("");
        adConfig.setUnityClientId("");
        adConfig.setVungleClientId("");
//        adConfig.setTtClientId("5010560");
        //设置Admob测试设备
        adConfig.setTestParams(TestParams.builder(true)
                .addAdmobTestDevice(""));
        adConfig.setFbNativeAdClickAreaControl(true);
        adConfig.setReportEvent(true);    
    }
    
```
#### 广告初始化
```java
//这一步比较耗时
EyuAdManager.getInstance().config(MainActivity.this, adConfig, new EyuAdsListener() {
    @Override
    public void onAdReward(String type, String placeId) {

    }

    @Override
    public void onAdLoaded(String type, String placeId) {

    }

    @Override
    public void onAdShowed(String type, String placeId) {
    }

    @Override
    public void onAdClosed(String type, String placeId) {

    }

    @Override
    public void onAdClicked(String type, String placeId) {

    }

    @Override
    public void onDefaultNativeAdClicked() {

    }

    @Override
    public void onAdLoadFailed(String placeId, String key, int code) {

    }

    @Override
    public void onImpression(String type, String placeId) {

    }
});
```

#### 生命周期处理
```java
   Activity中添加
    
        @Override
    protected void onResume() {
        super.onResume();
        SdkHelper.onResume(this);
        EyuAdManager.getInstance().resume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SdkHelper.onPause(this);
        EyuAdManager.getInstance().pause(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EyuAdManager.getInstance().destroyCurrent(this);
    }
```
#### 使用示例
```java
 //激励视频
findViewById(R.id.btnVideoAd).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        EyuAdManager.getInstance().showRewardedVideoAd(MainActivity.this, "reward_ad");
    }
});

//插屏
findViewById(R.id.btnInterstitialAd).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        //展示插屏前可以判断一下是否有加载成功的插屏，其余类型的广告也有类似方法，可根据实际的展示逻辑处理
        if (EyuAdManager.getInstance().isInterstitialAdLoaded("main_view_inter_ad")) {
            EyuAdManager.getInstance().showInterstitialAd(MainActivity.this, "main_view_inter_ad");
        }
    }
});

//banner，需要一个容器
EyuAdManager.getInstance().showBannerAd(MainActivity.this, (ViewGroup) findViewById(R.id.ll_banner),
        "page_view_banner_ad");

//原生广告
/**
 * {
 *     "cacheGroup": "page_view_native_ad",
 *     "isEnabled": "true",
 *     "nativeAdLayout": "native_ad_in_page",
 *     "id": "page_view_native_ad",
 *     "desc": "页面原生广告"
 *   }
 *
 *   nativeAdLayout是原生广告的容器，value是native_ad_in_page，对应layout中 native_ad_in_page.xml
 */
EyuAdManager.getInstance().showNativeAd(this, (ViewGroup) findViewById(R.id.rl_native), "page_view_native_ad");
```

#### 清单文件配置
此配置可能随SDK的升级有所变动
```xml
<meta-data
android:name="com.google.android.gms.ads.APPLICATION_ID"
android:value="ca-app-pub-9624926763614741~6598275826" />

<meta-data
android:name="com.facebook.sdk.ApplicationId"
android:value="@string/facebook_app_id" />

<!--MTG start -->
        <activity
            android:name="com.mintegral.msdk.reward.player.MTGRewardVideoActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
        <!--MTG end-->

        <!--Google Play Services -->
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version"/>
        <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="@string/your_gp_app_id"/>
        <activity android:name="com.google.android.gms.ads.AdActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize" android:theme="@android:style/Theme.Translucent" />

        <!-- UnityAds -->
        <activity
        android:name="com.unity3d.services.ads.adunit.AdUnitActivity"
        android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen"
        android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
        android:hardwareAccelerated="true" />

        <activity
        android:name="com.unity3d.services.ads.adunit.AdUnitTransparentActivity"
        android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen"
        android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen"
        android:hardwareAccelerated="true" />

        <activity
        android:name="com.unity3d.services.ads.adunit.AdUnitTransparentSoftwareActivity"
        android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen"
        android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen"
        android:hardwareAccelerated="false" />

        <activity
        android:name="com.unity3d.services.ads.adunit.AdUnitSoftwareActivity"
        android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen"
        android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
        android:hardwareAccelerated="false" />

         <!--AppLovin -->
        <activity
        android:name="com.applovin.adview.AppLovinInterstitialActivity"/>



        <!-- facebook -->
        <meta-data android:name="com.facebook.sdk.ApplicationId"
            android:value="1907554856229772"/>
        <activity
        android:name="com.facebook.ads.AudienceNetworkActivity"
        android:hardwareAccelerated="true"
        android:configChanges="keyboardHidden|orientation|screenSize" />
        
```

#### 添加权限
//必须要有的权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
<uses-permission android:name="android.permission.GET_TASKS"/>
```


### 代码混淆
如果您需要使用proguard混淆代码，需确保不要混淆SDK的代码。 请在proguard.cfg文件(或其他混淆文件)尾部添加如下配置:
```txt
#ironsource
-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
    public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep class com.ironsource.adapters.** { *;
}
-dontwarn com.ironsource.mediationsdk.**
-dontwarn com.ironsource.adapters.**
-dontwarn com.moat.**
-keep class com.moat.** { public protected private *; }

## UnityAds adapter
# Keep filenames and line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
# Keep JavascriptInterface for WebView bridge
-keepattributes JavascriptInterface
# Sometimes keepattributes is not enough to keep annotations
-keep class android.webkit.JavascriptInterface {
   *;
}
# Keep all classes in Unity Ads package
-keep class com.unity3d.ads.** {
   *;
}
# Keep all classes in Unity Services package
-keep class com.unity3d.services.** {
   *;
}
-dontwarn com.google.ar.core.**
-dontwarn com.unity3d.services.**
-dontwarn com.ironsource.adapters.unityads.**

#applovin
-keepattributes Signature,InnerClasses,Exceptions,Annotation
-keep public class com.applovin.sdk.AppLovinSdk{ *; }
-keep public class com.applovin.sdk.AppLovin* { public protected *; }
-keep public class com.applovin.nativeAds.AppLovin* { public protected *; }
-keep public class com.applovin.adview.* { public protected *; }
-keep public class com.applovin.mediation.* { public protected *; }
-keep public class com.applovin.mediation.ads.* { public protected *; }
-keep public class com.applovin.impl.*.AppLovin { public protected *; }
-keep public class com.applovin.impl.**.*Impl { public protected *; }
-keepclassmembers class com.applovin.sdk.AppLovinSdkSettings { private java.util.Map localSettings; }
-keep class com.applovin.mediation.adapters.** { *; }
-keep class com.applovin.mediation.adapter.**{ *; }

# Vungle
-keep class com.vungle.warren.** { *; }
-dontwarn com.vungle.warren.error.VungleError$ErrorCode
# Moat SDK
-keep class com.moat.** { *; }
-dontwarn com.moat.**
# Okio
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Retrofit
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Google Android Advertising ID
-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.ads.identifier.**

#AppsFlyer
-dontwarn com.appsflyer.**
-keep public class com.google.firebase.iid.FirebaseInstanceId {
    public *;
}

#eyu
-keep class com.eyu.common.**{*;}
```
### 配置appsflyer
[https://support.appsflyer.com/hc/zh-cn/articles/207032126-AppsFlyer-SDK-%E5%AF%B9%E6%8E%A5-Android](https://support.appsflyer.com/hc/zh-cn/articles/207032126-AppsFlyer-SDK-%E5%AF%B9%E6%8E%A5-Android)



