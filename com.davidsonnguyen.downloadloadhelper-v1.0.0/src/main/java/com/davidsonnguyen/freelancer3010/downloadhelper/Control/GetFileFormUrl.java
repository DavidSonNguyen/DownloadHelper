package com.davidsonnguyen.freelancer3010.downloadhelper.Control;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.ECheckResult;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.EDownloadStatus;
import com.davidsonnguyen.freelancer3010.downloadhelper.Initial.RetrofitAPI;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.ResultDownload;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.FileModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.ResultModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.StatusModel;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by DavidSonNguyen on 11/8/2017.
 */

public class GetFileFormUrl {
    private File desFile;
    private int cache = 4 * 1024 * 1024;
    private AppCompatActivity activity;

    private FileModel    fileModel;
    private ResultModel  resultModel;
    private StatusModel  statusModel;

    private String[] urls;

    private SaveData saveData = new SaveData();

    public GetFileFormUrl(Builder builder) {
        this.activity = builder.activity;
        if (builder.desFile != null) {
            this.desFile = builder.desFile;
        }else {
            this.desFile = activity.getFilesDir();
        }
        this.cache = builder.cache;
        urls = splitUrl(builder.url);
        this.fileModel    = builder.fileModel;
        this.resultModel  = builder.resultModel;
        this.statusModel  = builder.statusModel;
    }

    public void download(){
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(urls[0]);
        Retrofit retrofit = builder.build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.downloadUrl(urls[1]);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (statusModel.getStatus().getValue() != EDownloadStatus.LOADING) {
                    saveData = new SaveData();
                    saveData.setActivity(activity);
                    saveData.setCache(cache);
                    saveData.setDesFile(desFile);
                    saveData.setResponse(response);
                    saveData.setFileModel(fileModel);
                    saveData.setResultModel(resultModel);
                    saveData.setStatusModel(statusModel);
                    saveData.start();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ResultDownload resultDownload = resultModel.getResultDownload().getValue();
                if (resultDownload == null){
                    resultDownload = new ResultDownload();
                }
                resultDownload.setResult(ECheckResult.FAIL);
                resultDownload.setError(t.getMessage());
            }
        });
    }

    public void cancel(){
        statusModel.getStatus().setValue(EDownloadStatus.STOP);
        saveData.interrupt();
        ResultDownload resultDownload = resultModel.getResultDownload().getValue();
        if (resultDownload == null){
            resultDownload = new ResultDownload();
        }

        resultDownload.setResult(ECheckResult.FAIL);
        resultDownload.setError("Cancel");

        resultModel.getResultDownload().setValue(resultDownload);
    }

    public void pause(){
        saveData.interrupt();
        if (statusModel.getStatus().getValue() == EDownloadStatus.LOADING) {
            statusModel.getStatus().setValue(EDownloadStatus.PAUSE);
            ResultDownload resultDownload = resultModel.getResultDownload().getValue();
            if (resultDownload == null) {
                resultDownload = new ResultDownload();
            }

            resultDownload.setResult(ECheckResult.TEMP);
            resultDownload.setError("Pause");

            resultModel.getResultDownload().setValue(resultDownload);
        }
    }

    public void resume(){
        download();
    }

    private String[] splitUrl(String url){
        String[] urls = new String[2];
        Uri uri = Uri.parse(url);
        urls[0] = uri.getScheme() + "://" + uri.getHost() + "/";
        urls[1] = uri.getPath().replaceFirst("/", "");
        if (uri.getQuery() != null) {
            urls[1] = urls[1] + "?" + uri.getQuery();
        }
        return urls;
    }

    public static final class Builder {

        @NonNull
        AppCompatActivity activity;

        @NonNull
        private String url;

        private File desFile;

        private int cache = 4 * 1024 * 1024;

        @NonNull
        private FileModel fileModel;

        @NonNull
        private ResultModel resultModel;

        @NonNull
        private StatusModel statusModel;

        /**
         * @param url is your link of file that you want to download
         * @Example: https://static.fptplay.net/static/img/apps/s905x-p212-dv8236-FPT-v6.7.55-1711092056.zip
         */
        @NonNull
        public Builder url(String url){
            this.url = url;
            return this;
        }

        @NonNull
        public Builder activity(AppCompatActivity activity){
            this.activity = activity;
            return this;
        }

        /**
         * @param desFile is the FILE that you save whats you get from server
         * @Default desFile is @getFilesDir(), this file will be saved in Internal Storage of device.
         */
        public Builder desFile(File desFile){
            this.desFile = desFile;
            return this;
        }

        /**
         * @param cache is size of byte array.
         *   And the byte array is the number of bytes that you get in one time read buffer from source file
         * @Example: Size of source file is 100MB
         *   And @cache = 10 * 1024 * 1024(byte) = 10(MB)
         *   It is mean you will get 10(MB) from source file and save into your File(@desFile),
         *   and process will loop this action many times util end of source file.
         * @Default cache is 4 * 1024 * 1024(byte) = 4(MB)
         */
        public Builder cache(int cache){
            this.cache = cache;
            return this;
        }

        @NonNull
        public Builder fileModel(FileModel fileModel){
            this.fileModel = fileModel;
            return this;
        }

        @NonNull
        public Builder resultModel(ResultModel resultModel){
            this.resultModel = resultModel;
            return this;
        }

        @NonNull
        public Builder statusModel(StatusModel statusModel){
            this.statusModel = statusModel;
            return this;
        }

        public GetFileFormUrl build(){
            return new GetFileFormUrl(this);
        }
    }
}
