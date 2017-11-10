package com.cxz.filebrowser;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initTBS();
    }

    private void initTBS() {
        // 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e("apptbs", " onViewInitFinished is-----> " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
