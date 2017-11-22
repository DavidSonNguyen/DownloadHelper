package com.davidsonnguyen.freelancer3010.downloadhelper.User;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.davidsonnguyen.freelancer3010.downloadhelper.Control.CheckInternetConnection;
import com.davidsonnguyen.freelancer3010.downloadhelper.Control.GetFileFormUrl;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.EDownloadStatus;
import com.davidsonnguyen.freelancer3010.downloadhelper.Initial.Connection;
import com.davidsonnguyen.freelancer3010.downloadhelper.MainActivity;
import com.davidsonnguyen.freelancer3010.downloadhelper.R;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.FileModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.ResultModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.StatusModel;

import java.io.File;

//Created by DavidSonNguyen on 11/17/2017.

/**
 * DownloadHelper downloads file from link by using Retrofit, Okio and Lifecycle.
 * <p>
 * So, you must import their library into your project.
 * <p>
 * Refer:
 * <pre>
 *     - Retrofit: <a href="http://square.github.io/retrofit/">http://square.github.io/retrofit/</a>
 *     - Okio: <a href="https://github.com/square/okio">https://github.com/square/okio</a>
 *     - Lifecycle: <a href="https://developer.android.com/topic/libraries/architecture/livedata.html">https://developer.android.com/topic/libraries/architecture/livedata.html</a>
 * </pre>
 * Example:
 * <pre>
 * <code>
 *  DownloadHelper helper = new DownloadHelper.Builder()
 *            .activity(this)
 *            .url("http://download.myfile.com")
 *            .build();
 *        </code>
 * </pre>
 */
public class DownloadHelper implements Connection{
    private AppCompatActivity activity;
    private String url;
    private File desFile;
    private int cache;

    private FileModel fileModel;
    private ResultModel resultModel;
    private StatusModel statusModel;

    GetFileFormUrl getFileFormUrl;

    CheckInternetConnection connection;

    public DownloadHelper(Builder builder) {
        this.activity = builder.activity;
        this.url = builder.url;
        if (builder.desFile != null){
            this.desFile = builder.desFile;
        }else {
            this.desFile = activity.getFilesDir();
        }

        this.cache = builder.cache;

        this.fileModel = builder.fileModel;
        this.resultModel = builder.resultModel;
        this.statusModel = builder.statusModel;
    }

    /**
     * This method is used with default Layout.
     * This will download with available Activity and Layout.
     * <p>
     * You just add {@link #activity activity}, {@link #url url}, {@link #desFile desFile} and {@link #cache cache}.
     * The compulsory parameters are {@link #activity activity} and {@link #url url}.
     * {@link #desFile desFile} and {@link #cache cache} are not, they have default values.
     * </p>
     */
    public void startDownloadActivity(){
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("LINK", url);
        bundle.putSerializable("FILE", desFile);
        bundle.putInt("CACHE", cache);
        intent.putExtra("BUNDLE", bundle);
        activity.startActivity(intent);
    }

    /**
     * This method is used without layout.
     * You can use your own layout to perform your download by using Observed values.
     * <p>
     * We have 3 Observed values: {@linkplain FileModel FileModel}, {@linkplain ResultModel ResultModel}
     * and {@linkplain StatusModel StatusModel}.
     * </p>
     */
    public void startDownload(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            showMessageOKCancel(activity.getResources().getString(R.string.request_permission),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1234);
                            download();
                        }
                    }
            );

            return;
        }
        download();
    }

    // Initialize class to dw=ownload
    private void download(){
        statusModel.getStatus().setValue(EDownloadStatus.STOP);
        getFileFormUrl = new GetFileFormUrl.Builder()
                .url(url)
                .cache(cache)
                .desFile(desFile)
                .fileModel(fileModel)
                .resultModel(resultModel)
                .statusModel(statusModel)
                .activity(activity)
                .build();
        connection = new CheckInternetConnection(activity.getApplicationContext(), this);
        connection.start();
    }

    // Observe Changing internet connection status
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

    // Confirm permission
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(activity.getResources().getString(R.string.allow_permission), okListener)
                .setNegativeButton(activity.getResources().getString(R.string.deny_permission), null)
                .create()
                .show();
    }

    /**
     * Build a new {@linkplain DownloadHelper DownloadHelper}
     * <p>
     * Calling {@link #activity activity} and {@link #url url} is required. All other methods are optional.
     * <p>
     * And if you want to start download without activity, you must add {@link #fileModel fileModel}, {@link #resultModel resultModel}
     * and {@link #statusModel statusDownload} to update download proccess.
     */
    public static final class Builder {

        @NonNull
        AppCompatActivity activity;

        /**
         * url is your link of file that you want to download
         */
        @NonNull
        private String url;

        /**
         * desFile is the FILE that you save whats you get from server
         */
        private File desFile;

        /**
         * cache is size of byte array.
         *   And the byte array is the number of bytes that you get in one time read buffer from source file
         */
        private int cache = 4 * 1024 * 1024;

        /**
         * fileModel is {@link FileModel FileModel}, it containts data about download proccess
         */
        @NonNull
        private FileModel fileModel;

        /**
         * resultModel is {@link ResultModel ResultModel}, it containts result about download proccess
         */
        @NonNull
        private ResultModel resultModel;

        /**
         * statusModel is {@link StatusModel StatusModel}, it containts status about download proccess
         */
        @NonNull
        private StatusModel statusModel;

        /**
         * @param url is your link of file that you want to download
         * @return {@link Builder Builder}
         * <p>
         * Example: https://example.net/filedownload.zip
         */
        @NonNull
        public DownloadHelper.Builder url(String url){
            this.url = url;
            return this;
        }

        /**
         *
         * @param activity
         * @return {@link Builder Builder}
         */
        @NonNull
        public DownloadHelper.Builder activity(AppCompatActivity activity){
            this.activity = activity;
            return this;
        }

        /**
         * @param desFile is the FILE that you save whats you get from server
         * @return {@link Builder Builder}
         * <p>
         * Default desFile is getFilesDir(), this file will be saved in Internal Storage of device.
         */
        public DownloadHelper.Builder desFile(File desFile){
            this.desFile = desFile;
            return this;
        }

        /**
         * @param cache is size of byte array.
         *   And the byte array is the number of bytes that you get in one time read buffer from source file
         * @return {@link Builder Builder}
         * <p>
         * Example: Size of source file is 100MB
         *   and {@link #cache cache} = 10 * 1024 * 1024(byte) = 10(MB).
         *   It is mean you will get 10(MB) from source file and save into your {@link #desFile desFile},
         *   and process will loop this action many times util end of source file.
         * <p>
         * Default {@link #cache cache} = 4 * 1024 * 1024(byte) = 4(MB)
         */
        public DownloadHelper.Builder cache(int cache){
            this.cache = cache;
            return this;
        }

        /**
         * @param fileModel is {@link FileModel FileModel}, it containts data about download proccess
         * @return {@link Builder Builder}
         */
        @NonNull
        public DownloadHelper.Builder fileModel(FileModel fileModel){
            this.fileModel = fileModel;
            return this;
        }

        /**
         *
         * @param resultModel is {@link ResultModel ResultModel}, it containts result about download proccess
         * @return
         */
        @NonNull
        public DownloadHelper.Builder resultModel(ResultModel resultModel){
            this.resultModel = resultModel;
            return this;
        }

        /**
         *
         * @param statusModel is {@link StatusModel StatusModel}, it containts status about download proccess
         * @return
         */
        @NonNull
        public DownloadHelper.Builder statusModel(StatusModel statusModel){
            this.statusModel = statusModel;
            return this;
        }

        /**
         * Create the {@link DownloadHelper DownloadHelper} instance using the configured values.
         */
        public DownloadHelper build(){
            return new DownloadHelper(this);
        }
    }
}
