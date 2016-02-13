package lbconsulting.com.backendlesstest1.activities;

import android.app.Application;
import android.content.Context;

import com.backendless.Backendless;

import lbconsulting.com.backendlesstest1.classes.MyLog;
import lbconsulting.com.backendlesstest1.classes.MySettings;


public class App extends Application {

    private static Context mContext;

    private final String APP_ID ="E3C25B04-0237-343F-FF8D-4ACDD2199C00";
    private final String ANDROID_SECRET_KEY ="A3F008D8-0966-48BC-FFEA-3DF898407400";
    private final String APP_VERSION="v1";


    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("App", "onCreate()");

        mContext = this;

        Backendless.initApp(this, APP_ID, ANDROID_SECRET_KEY, APP_VERSION);
        MyLog.i("App", "onCreate(): Backendless initialized");

        MySettings.setContext(mContext);
    }

    public static Context getContext(){
        return mContext;
    }

}
