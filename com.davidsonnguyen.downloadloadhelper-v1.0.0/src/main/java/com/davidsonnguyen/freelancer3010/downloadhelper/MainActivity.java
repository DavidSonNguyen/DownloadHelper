package com.davidsonnguyen.freelancer3010.downloadhelper;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidsonnguyen.freelancer3010.downloadhelper.Control.CheckInternetConnection;
import com.davidsonnguyen.freelancer3010.downloadhelper.Control.GetFileFormUrl;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.ECheckResult;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.EDownloadStatus;
import com.davidsonnguyen.freelancer3010.downloadhelper.Initial.Connection;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.FileDownload;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.ResultDownload;
import com.davidsonnguyen.freelancer3010.downloadhelper.User.DownloadHelper;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.FileModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.ResultModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.StatusModel;

import java.io.File;

public class MainActivity extends AppCompatActivity implements Connection {

    ProgressBar progressBar;
    TextView txtPercent;

    GetFileFormUrl getFileFormUrl;
    CheckInternetConnection connection;

    String link;
    File file;
    int cache;

    FileModel fileModel;
    ResultModel resultModel;
    StatusModel statusModel;

    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_module);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
        }

        DownloadHelper helper = new  DownloadHelper.Builder()
                .url("fseg")
                .activity(this)
                .cache(528)
                .build();
        helper.startDownloadActivity();
        helper.startDownload();


        ANHXA();
        bundle = this.getIntent().getBundleExtra("BUNDLE");
        link = bundle.getString("LINK");
        file = (File) bundle.getSerializable("FILE");
        cache = bundle.getInt("CACHE");

        if (savedInstanceState != null){
            if (savedInstanceState.getBoolean("DONE")) {
                txtPercent.setText("100%");
                progressBar.setProgress(100);
            }else {
                FileDownload fileDownload = new FileDownload();
                fileDownload.setSize(savedInstanceState.getLong("SIZE"));
                fileDownload.setProgress(savedInstanceState.getLong("PROGRESS"));
                fileDownload.setDone(false);
                fileModel.getFileDownload().setValue(fileDownload);
                statusModel.getStatus().setValue(EDownloadStatus.PAUSE);
                InitialDownload();
            }
        }else {
            statusModel.getStatus().setValue(EDownloadStatus.STOP);
            InitialDownload();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFileFormUrl.cancel();
    }

    private void ANHXA(){
        connection = new CheckInternetConnection(this, this);
        connection.start();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtPercent  = (TextView) findViewById(R.id.txtPercent);

        fileModel    = ViewModelProviders.of(this).get(FileModel.class);
        resultModel  = ViewModelProviders.of(this).get(ResultModel.class);
        statusModel  = ViewModelProviders.of(this).get(StatusModel.class);
    }

    private void InitialDownload(){
        getFileFormUrl = new GetFileFormUrl.Builder()
                .url(link)
                .cache(cache)
                .desFile(file)
                .fileModel(fileModel)
                .resultModel(resultModel)
                .statusModel(statusModel)
                .activity(this)
                .build();
        ObserveDownload();
    }

    private void ObserveDownload(){
        fileModel.getFileDownload().observe(this, new Observer<FileDownload>() {
            @Override
            public void onChanged(@Nullable FileDownload fileDownload) {
                int per = 0;
                if (fileDownload.getSize() != 0){
                    per = (int) ((fileDownload.getProgress() * 100) / fileDownload.getSize());
                }
                progressBar.setProgress(per);
                txtPercent.setText(per + getResources().getString(R.string.percent));

                if (fileDownload.isDone()){
                    ResultDownload resultDownload = new ResultDownload();
                    resultDownload.setResult(ECheckResult.SUCCESS);
                    resultDownload.setError("");
                    resultModel.getResultDownload().setValue(resultDownload);
                }
            }
        });

        resultModel.getResultDownload().observe(this, new Observer<ResultDownload>() {
            @Override
            public void onChanged(@Nullable ResultDownload resultDownload) {
                if (resultDownload.getResult() == ECheckResult.SUCCESS){
                    Toast.makeText(getApplicationContext(), ECheckResult.SUCCESS.name(), Toast.LENGTH_SHORT).show();
                }else {
                    if (resultDownload.getResult() == ECheckResult.FAIL){
                        Toast.makeText(getApplicationContext(), resultDownload.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234){
            if (grantResults.length == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("LINK", link);
                    bundle.putSerializable("FILE", file);
                    bundle.putInt("CACHE", cache);
                    intent.putExtra("BUNDLE", bundle);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
        }

        if (fileModel.getFileDownload().getValue() != null) {
            if (!fileModel.getFileDownload().getValue().isDone()) {
                outState.putBoolean("DONE", false);
                outState.putString("LINK", link);
                outState.putLong("PROGRESS", fileModel.getFileDownload().getValue().getProgress());
                outState.putLong("SIZE", fileModel.getFileDownload().getValue().getSize());
            } else {
                outState.putBoolean("DONE", true);
            }
        }
    }

    @Override
    public void onChange(boolean connect) {
        if (connect){
            if (statusModel.getStatus().getValue() == EDownloadStatus.PAUSE){
                getFileFormUrl.resume();
            }else {
                getFileFormUrl.download();
            }
        }else {
            getFileFormUrl.pause();
        }
    }
}
