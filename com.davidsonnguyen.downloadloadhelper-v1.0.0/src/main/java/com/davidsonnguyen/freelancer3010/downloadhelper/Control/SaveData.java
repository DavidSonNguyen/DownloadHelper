package com.davidsonnguyen.freelancer3010.downloadhelper.Control;

import android.support.v7.app.AppCompatActivity;

import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.ECheckResult;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.EDownloadStatus;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.FileDownload;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.ResultDownload;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.FileModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.ResultModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel.StatusModel;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;

/**
 * Created by DavidSonNguyen on 11/16/2017.
 */

public class SaveData extends Thread {
    private BufferedSource bufferedSource = null;
    private BufferedSink bufferedSink = null;

    AppCompatActivity activity;
    private File file;

    private File desFile;
    private int cache;
    private Response<ResponseBody> response;

    private FileModel fileModel;
    private ResultModel resultModel;
    private StatusModel statusModel;

    public SaveData(){

    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setDesFile(File desFile) {
        this.desFile = desFile;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public void setResponse(Response<ResponseBody> response) {
        this.response = response;
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
    }

    public void setResultModel(ResultModel resultModel) {
        this.resultModel = resultModel;
    }

    public void setStatusModel(StatusModel statusModel) {
        this.statusModel = statusModel;
    }

    @Override
    public void run() {
        super.run();
        saveDataOkio(this.response);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (file != null){
            if (statusModel.getStatus().getValue() == EDownloadStatus.STOP) {
                file.delete();
            }
        }
    }

    private void saveDataOkio(Response<ResponseBody> response){
        String fileName;
        String fileType;
        long fileSize;
        byte[] fileReader = new byte[cache];
        long fileDownloaded = 0;

        if (response.body() == null){
            ResultDownload resultDownload = new ResultDownload();
            resultDownload.setError("File not found");
            resultDownload.setResult(ECheckResult.FAIL);
            resultModel.getResultDownload().postValue(resultDownload);
            return;
        }
        fileName = response.headers().value(5).replace("\"", "");
        fileSize = response.body().contentLength();
        fileType = response.body().contentType().subtype();

        /**
         * this array has size and progress of downloaded file. status[0] = progress, status[1] = size.
         */
        ResultDownload resultDownload = resultModel.getResultDownload().getValue();
        if (resultDownload == null){
            resultDownload = new ResultDownload();
        }
        resultDownload.setError("");
        resultDownload.setResult(ECheckResult.TEMP);

        try{
            file = new File(desFile, fileName + "."+ fileType);
            bufferedSource = response.body().source();

            //Create init source and sink
                if (statusModel.getStatus().getValue() == EDownloadStatus.PAUSE) {
                    bufferedSink = Okio.buffer(Okio.appendingSink(file));

                    fileDownloaded = file.length();
                    bufferedSource.skip(fileDownloaded);

                    FileDownload value = fileModel.getFileDownload().getValue();
                    if (value == null){
                        value = new FileDownload();
                    }
                    value.setProgress(fileDownloaded);
                    value.setSize(fileSize);
                    fileModel.getFileDownload().postValue(value);
                }else {
                    bufferedSink = Okio.buffer(Okio.sink(file));
                }

            statusModel.getStatus().postValue(EDownloadStatus.LOADING);
                //start read
            while (true) {
                int read = bufferedSource.read(fileReader);
                if (read == -1) {
                    break;
                }
                bufferedSink.write(fileReader, 0, read);
                fileDownloaded += read;

                FileDownload value = fileModel.getFileDownload().getValue();
                if (value == null){
                    value = new FileDownload();
                }
                value.setSize(fileSize);
                value.setProgress(fileDownloaded);
                fileModel.getFileDownload().postValue(value);
            }

            bufferedSink.flush();

            //End download
            if (fileDownloaded == fileSize){
                FileDownload value = fileModel.getFileDownload().getValue();
                value.setDone(true);
                fileModel.getFileDownload().postValue(value);
            }else {

            }

        }catch (IOException e){
            e.printStackTrace();
            ResultDownload rd = new ResultDownload();
            rd.setResult(ECheckResult.FAIL);
            rd.setError("File not found");
            resultModel.getResultDownload().postValue(rd);
        }finally {
            try {
                if (bufferedSink != null) {
                    bufferedSink.close();
                }

                if (bufferedSource != null) {
                    bufferedSource.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
