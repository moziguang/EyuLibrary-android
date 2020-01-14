package com.eyu.test;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appsflyer.AppsFlyerConversionListener;
import com.eyu.common.SdkHelper;
import com.eyu.common.ad.EyuAdManager;
import com.eyu.common.ad.EyuAdsListener;
import com.eyu.common.ad.model.AdConfig;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements EyuAdsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SdkHelper.init(this);
        String[] permissions = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, SdkHelper.PERMISSIONS_REQ_CODE);
        SdkHelper.initTracking(this, "yourAppKey");
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
        initAdConfig();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SdkHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SdkHelper.onPause(this);
    }

    private void initView()
    {
        View btnVideoAd = findViewById(R.id.buttonRewardAd);
        btnVideoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EyuAdManager.getInstance().showRewardedVideoAd(MainActivity.this, "reward_ad");
            }
        });

        View btnInterstitialAd = findViewById(R.id.btnInterstitialAd);
        btnInterstitialAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EyuAdManager.getInstance().showInterstitialAd(MainActivity.this, "inter_ad");
            }
        });
    }

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
         *     NETWORK_FACEBOOK = "facebook";
         *     NETWORK_ADMOB = "admob";
         *     NETWORK_UNITY = "unity";
         *     NETWORK_VUNGLE = "vungle";
         *     NETWORK_APPLOVIN = "applovin";
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
//        adConfig.setMaxTryLoadRewardAd(2);
//        adConfig.setMaxTryLoadNativeAd(2);
//        adConfig.setMaxTryLoadInterstitialAd(2);

        EyuAdManager.getInstance().config(this, adConfig, this);
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SdkHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}