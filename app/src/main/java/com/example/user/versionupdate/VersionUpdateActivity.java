package com.example.user.versionupdate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

public class VersionUpdateActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        initView();
    }

    private void initView(){
        AlertDialog.Builder dialogTips = new AlertDialog.Builder(this);
        dialogTips.setTitle("提示");
        dialogTips.setMessage("检测到有新版本，是否更新");
        dialogTips.setNegativeButton("取消", null);
        dialogTips.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownload();
            }
        });
        dialogTips.show();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("更新应用");
        progressDialog.setMax(100);
    }

    private void startDownload(){
        //定义保存的文件地址为根目录
        File path = new File(Environment.getExternalStorageDirectory(),
                "大众点评"+ ".apk");
        httpDownLoad(path.getPath(), "http://www.wandoujia.com/apps/com.dianping.v1/download");
    }

    /**
     * 下载的方法
     * @param path 文件保存的路径
     * @param url 下载的地址
     */
    private void httpDownLoad(String path, String url) {
        HttpUtils http = new HttpUtils();
        http.download(url, path, true, true, new RequestCallBack<File>() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                int index = (int) (current * 100 / total);
                progressDialog.setProgress(index);
            }


            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //获取到安装包后，调用系统的android安装apk界面进行安装 这是固定格式
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(new File(responseInfo.result.getPath())),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                progressDialog.dismiss();
                VersionUpdateActivity.this.finish();
            }

            @Override
            public void onFailure(
                    com.lidroid.xutils.exception.HttpException arg0, String arg1) {
                File path = new File(Environment.getExternalStorageDirectory(),
                        "大众点评"  + ".apk");
                Toast.makeText(VersionUpdateActivity.this, "下载失败" + arg1, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                path.delete();
            }
        });
    }


}
