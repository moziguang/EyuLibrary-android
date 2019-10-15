package com.example.app_overseas;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appsflyer.AppsFlyerConversionListener;
import com.eyu.common.SdkHelper;
import com.eyu.common.ad.EyuAdManager;
import com.eyu.common.ad.EyuAdsListener;
import com.eyu.common.ad.model.AdConfig;
import com.eyu.common.ad.model.TestParams;
import com.eyu.common.firebase.EyuRemoteConfigHelper;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSDK();
        initView();
    }

    private void initSDK() {
        SdkHelper.init(this);
        SdkHelper.initAppFlyerSdk("key", new AppsFlyerConversionListener() {

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
        initAdConfig();
    }

    private void initView() {
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
    }

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
        adConfig.setTestParams(TestParams.builder(true)
                .addAdmobTestDevice(""));
        adConfig.setFbNativeAdClickAreaControl(true);
        adConfig.setReportEvent(true);
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
    }

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

}
