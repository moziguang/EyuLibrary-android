### 迁移到 AndroidX（特别需要注意原生广告）
重构命令使用两个标记。默认情况下，这两个标记在 gradle.properties 文件中都设为 true：

android.useAndroidX=true
Android 插件会使用对应的 AndroidX 库而非支持库。
android.enableJetifier=true
类库映射：
https://developer.android.com/jetpack/androidx/migrate/artifact-mappings
类的对应关系请参考：
https://developer.android.com/jetpack/androidx/migrate/class-mappings

### 1.项目的build.gradle 添加以下内容
```gradle
buildscript {
    ...
    repositories {
        ...
        maven { url 'https://dl.bintray.com/umsdk/release' }
        ...
    }
    ...
}

allprojects {
    repositories {
        ...
        maven {
            url 'https://repo.rdc.aliyun.com/repository/74503-release-qNEqtU/'
            credentials {
                username 'p5gXfa'
                password 'wKY0RHNSH3'
            }
        }
        maven { url 'https://dl.bintray.com/umsdk/release' }
        ...
    }
}
```
### 2.module的build.gradle 添加以下内容
```gradle
...
dependencies {
    ...
    implementation 'androidx.multidex:multidex:2.0.1'
    implementation 'androidx.annotation:annotation:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.eyu:eyulibrary-tt-mtg-gdt:1.1.7'
    ...
}
```

### 3.配置multiDexEnabled
https://developer.android.com/studio/build/multidex

### 4.初始化sdk
```java
        SdkHelper.init(this);
        //申请权限
        String[] permissions = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, SdkHelper.PERMISSIONS_REQ_CODE);
        
        SdkHelper.initTracking(this, "yourAppKey" );
        SdkHelper.initUmSdk(this, "appKey", "channel");
        SdkHelper.initAppFlyerSdk("afKey", new AppsFlyerConversionListener() {
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
        }, getApplication());
        
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SdkHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```
### 6.实现广告回调EyuAdsListener
```java
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
```
### 7.初始化广告
```java
    private void initAdConfig() {
        AdConfig adConfig = new AdConfig();

        //游戏内广告位配置，每个广告位对应一个广告缓存池（cacheGroup）
        adConfig.setAdPlaceConfigStr("[\n" +
                "  {\n" +
                "    \"isEnabled\":\"true\",\n" +
                "    \"desc\":\"激励视频\",\n" +
                "    \"id\":\"reward_ad\",\n" +
                "    \"nativeAdLayout\":\"\",\n" +
                "    \"cacheGroup\":\"reward_ad_group\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"isEnabled\":\"true\",\n" +
                "    \"desc\":\"插屏\",\n" +
                "    \"id\":\"inter_ad\",\n" +
                "    \"nativeAdLayout\":\"\",\n" +
                "    \"cacheGroup\":\"inter_ad_group\"\n" +
                "  }\n" +
                "]");
        /**广告key配置，从广告平台获取，
         *     NETWORK_WM = "wm";
         *     NETWORK_GDT = "gdt";
         *     NETWORK_MINTEGRAL = "mintegral";
        **/
        adConfig.setAdKeyConfigStr("[{\n" +
                "    \"id\":\"wm_inter_ad\",\n" +
                "    \"key\":\"910560544\",\n" +
                "    \"network\":\"wm\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\":\"wm_reward_ad\",\n" +
                "    \"key\":\"910560534\",\n" +
                "    \"network\":\"wm\"\n" +
                "  }]");
        /**广告缓存池配置，一个缓存池对应一到多个key
         * TYPE_REWARD_AD = "rewardAd";
         * TYPE_INTERSTITIAL_AD = "interstitialAd";
         * TYPE_NATIVE_AD = "nativeAd";
         */
        adConfig.setAdGroupConfigStr("[\n" +
                "  {\n" +
                "    \"keys\":\"[\\\"wm_reward_ad\\\"]\",\n" +
                "    \"type\":\"rewardAd\",\n" +
                "    \"id\":\"reward_ad_group\",\n" +
                "    \"isAutoLoad\":\"true\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"keys\":\"[]\",\n" +
                "    \"type\":\"nativeAd\",\n" +
                "    \"id\":\"native_ad_group\",\n" +
                "    \"isAutoLoad\":\"true\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"keys\":\"[\\\"wm_inter_ad\\\"]\",\n" +
                "    \"type\":\"interstitialAd\",\n" +
                "    \"id\":\"inter_ad_group\",\n" +
                "    \"isAutoLoad\":\"true\"\n" +
                "  }\n" +
                "]");

        adConfig.setTtClientId("TtClientId");
        adConfig.setAppName("test");

        EyuAdManager.getInstance().config(this, adConfig, this);
    }
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
### 8.添加权限
```xml
//必须要有的权限
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
<uses-permission android:name="android.permission.GET_TASKS"/>
//最好能提供的权限
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

### 9. 适配Anroid7.0
```txt
http://ad.toutiao.com/union/media/support/custom17#1.2%20AndroidManifest%E9%85%8D%E7%BD%AE
如果您的应用需要适配Anroid7.0以及8.0，请在AndroidManifest中添加如下代码：
<provider
	 android:name="androidx.core.content.FileProvider"
	 android:authorities="${applicationId}.fileprovider"
	 android:exported="false"
	 android:grantUriPermissions="true">
	<meta-data
	     android:name="android.support.FILE_PROVIDER_PATHS"
	     android:resource="@xml/file_paths" />
</provider>  
<provider
    android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
    android:authorities="${applicationId}.TTMultiProvider"
    android:exported="false" />
  
在res/xml目录下，新建一个xml文件file_paths，在该文件中添加如下代码：
<?xml version="1.0" encoding="utf-8"?>  
<paths xmlns:android="http://schemas.android.com/apk/res/android">  
     <external-files-path name="external_files_path" path="Download" />  
    //为了适配所有路径可以设置 path = "."
</paths>
```
### 10.代码混淆
```txt
如果您需要使用proguard混淆代码，需确保不要混淆SDK的代码。 请在proguard.cfg文件(或其他混淆文件)尾部添加如下配置:
#umeng
-keep class com.umeng.** {*;}

#bytedance
-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep class com.androidquery.callback.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}


# Mintegral
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }

#AppsFlyer
-dontwarn com.appsflyer.**
-keep public class com.google.firebase.iid.FirebaseInstanceId {
    public *;
}

#eyu
-keep class com.eyu.common.**{*;}

##热云
-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.** {*;}
-dontwarn com.bun.miitmdid.**
-keep class com.bun.miitmdid.** {*;}
-dontwarn com.reyun.tracking.**
-keep class com.reyun.tracking.** {*;}

```

### 11.展示激励视频
```java
EyuAdManager.getInstance().showRewardedVideoAd(MainActivity.this, "reward_ad");
```
### 12.展示插屏广告
```java
EyuAdManager.getInstance().showInterstitialAd(MainActivity.this, "inter_ad");
```
### 13.适配android 9.0
```xml
<application
        ...
       android:networkSecurityConfig="@xml/network_security_config"
        ...>
        <uses-library android:name="org.apache.http.legacy" android:required="false" />
        ...
</application>
```
### 14.MTG activity 声明
```xml
<activity
            android:name="com.mintegral.msdk.reward.player.MTGRewardVideoActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
```

### 15.AppsFlyer
https://support.appsflyer.com/hc/zh-cn/articles/207032126-%E9%80%82%E7%94%A8%E4%BA%8E%E5%BC%80%E5%8F%91%E4%BA%BA%E5%91%98%E7%9A%84Android-SDK%E9%9B%86%E6%88%90#%E7%AE%80%E4%BB%8B