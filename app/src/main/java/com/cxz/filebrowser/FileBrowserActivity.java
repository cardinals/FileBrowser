package com.cxz.filebrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;

public class FileBrowserActivity extends AppCompatActivity {

    private static String TAG = "FileBrowserActivity";

    private FileBrowserView mFileBrowserView;
    private Toolbar mToolbar;
    private String filePath;

    public static void show(Context context, String path) {
        Intent intent = new Intent(context, FileBrowserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", path);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        init();

    }

    private void init() {
        Intent intent = this.getIntent();
        String path = (String) intent.getSerializableExtra("path");
        if (!TextUtils.isEmpty(path)) {
            Log.d(TAG, "文件path:::" + path);
            setFilePath(path);
        }

        initToolbar();

        initFileBrowser();

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getFileName(filePath));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFileBrowser() {
        mFileBrowserView = (FileBrowserView) findViewById(R.id.file_browser_view);
        mFileBrowserView.setOnGetFilePathListener(new FileBrowserView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(FileBrowserView mFileBrowserView) {
                mFileBrowserView.displayFile(new File(getFilePath()));
            }
        });

        mFileBrowserView.show();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * 获取文件名
     *
     * @param path
     * @return
     */
    private String getFileName(String path) {
        String name = "";
        if (TextUtils.isEmpty(path)) {
            return name;
        }
        int i = path.lastIndexOf('/');
        if (i == -1) {
            return name;
        }
        name = path.substring(i + 1);
        return name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileBrowserView != null) {
            mFileBrowserView.onStopDisplay();
        }
    }
}
