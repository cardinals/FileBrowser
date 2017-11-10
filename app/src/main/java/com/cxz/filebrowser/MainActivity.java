package com.cxz.filebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_select;
    private Button btn_open;
    private Button btn_open2;
    private TextView tv_result;
    private static final int FILE_SELECT_CODE = 0x01;
    private static final String TAG = "MainActivity";
    private String filePath = "";
    private ArrayList<String> filePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_select = (Button) findViewById(R.id.btn_select);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_open2 = (Button) findViewById(R.id.btn_open2);
        tv_result = (TextView) findViewById(R.id.tv_result);

        btn_select.setOnClickListener(this);
        btn_open.setOnClickListener(this);
        btn_open2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
                selectFile();
                break;
            case R.id.btn_open:
                if (!TextUtils.isEmpty(filePath))
                    open();
                break;
            case R.id.btn_open2:
                if (!TextUtils.isEmpty(filePath))
                    open2();
                break;
        }
    }

    /**
     * 自定义的方式打开
     */
    private void open2() {
        FileBrowserActivity.show(this, filePath);
    }

    /**
     * TBS默认的方式打开
     */
    private void open() {
        HashMap<String, String> params = new HashMap<>();
        params.put("local", "true");
        params.put("style", "1");
        filePath = filePath.replace("file:///", "");
        QbSdk.openFileReader(MainActivity.this, filePath, params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d(TAG, "onReceiveValue-->" + s);
            }
        });
    }

    private void selectFile() {

        FilePickerBuilder.getInstance().setMaxCount(10)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .pickFile(this);

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    try {
                        filePath = getPath(MainActivity.this, uri);
                        tv_result.setText(filePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "File Path: " + filePath);
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    filePaths = new ArrayList<>();
                    filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    filePath = filePaths.get(0);
                    tv_result.setText(filePath);
                    Log.d(TAG, "File Path: " + filePath);
                }
                break;
        }
    }

    private String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

}
