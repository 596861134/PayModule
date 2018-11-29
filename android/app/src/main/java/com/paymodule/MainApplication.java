package com.paymodule;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private Activity app_activity = null;
  private static volatile RNSDK mInstance;
  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new ReactNativePackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    mInstance = this;
    initGlobeActivity();
  }

  private void initGlobeActivity() {
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        app_activity = activity;
        Log.e("onActivityCreated===", app_activity + "");
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        app_activity = activity;
        Log.e("onActivityDestroyed===", app_activity + "");
      }

      /** Unused implementation **/
      @Override
      public void onActivityStarted(Activity activity) {
        app_activity = activity;
        Log.e("onActivityStarted===", app_activity + "");
      }

      @Override
      public void onActivityResumed(Activity activity) {
        app_activity = activity;
        Log.e("onActivityResumed===", app_activity + "");
      }

      @Override
      public void onActivityPaused(Activity activity) {
        app_activity = activity;
        Log.e("onActivityPaused===", app_activity + "");
      }

      @Override
      public void onActivityStopped(Activity activity) {
        app_activity = activity;
        Log.e("onActivityStopped===", app_activity + "");
      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
      }
    });
  }

  /**
   * 获取实例
   * @return
   */
  public static MainApplication getInstance() {
    if (null == mInstance) {
      synchronized (RNSDK.class) {
        if (null == mInstance) {
          mInstance = new MainApplication();
        }
      }
    }
    return mInstance;
  }

  /**
   * 公开方法，外部可通过 MyApplication.getInstance().getCurrentActivity() 获取到当前最上层的activity
   */
  public Activity getCurrentActivity() {
    return app_activity;
  }
}
