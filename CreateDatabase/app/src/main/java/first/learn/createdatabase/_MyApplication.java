package first.learn.createdatabase;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.multidex.MultiDex;


public class _MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
       context = getApplicationContext(); // Grab the Context you want.

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }


    private static final Handler mHandler = new Handler();

    public static void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }


}

