[https://github.com/moziguang/EyuLibrary-android.git](https://github.com/moziguang/EyuLibrary-android.git)

### 项目的build.gradle 添加以下内容
```
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
 
    }
}
```
### module的build.gradle 添加以下内容

```
apply plugin: 'com.google.gms.google-services'
apply plugin: 'io.fabric'

dependencies {
 
    implementation 'com.android.support:multidex:1.0.3'
    implementation 'com.eyu:eyulibrary:1.3.02'

}
```


### 配置google-services.json
从firebase控制台下载 google-services.json ，并复制到module根目录下

### 配置multiDexEnabled
[https://developer.android.com/studio/build/multidex](https://developer.android.com/studio/build/multidex)

### SDK使用
#### 初始化sdk
```
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
```
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
        adConfig.setMaxTryLoadRewardAd(1);
        adConfig.setMaxTryLoadNativeAd(1);
        adConfig.setMaxTryLoadInterstitialAd(1);
        //设置Admob测试设备
        adConfig.setTestParams(TestParams.builder(true)
                .addAdmobTestDevice(""));
        adConfig.setFbNativeAdClickAreaControl(true);
        adConfig.setReportEvent(true);    
    }
    
```
#### 广告初始化
```
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
});
```

#### 生命周期处理
```
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
```
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
```
meta-data
android:name="com.google.android.gms.ads.APPLICATION_ID"
android:value="ca-app-pub-9624926763614741~6598275826" />

<meta-data
android:name="com.facebook.sdk.ApplicationId"
android:value="@string/facebook_app_id" />
```

#### 添加权限
//必须要有的权限
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
<uses-permission android:name="android.permission.GET_TASKS"/>
```
//最好能提供的权限
```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```


### 代码混淆
如果您需要使用proguard混淆代码，需确保不要混淆SDK的代码。 请在proguard.cfg文件(或其他混淆文件)尾部添加如下配置:
```
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



