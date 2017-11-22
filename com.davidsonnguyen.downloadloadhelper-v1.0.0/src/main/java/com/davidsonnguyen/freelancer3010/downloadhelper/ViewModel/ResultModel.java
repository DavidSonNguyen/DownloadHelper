package com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.ECheckResult;
import com.davidsonnguyen.freelancer3010.downloadhelper.Model.ResultDownload;

import java.io.Serializable;


//Created by DavidSonNguyen on 11/15/2017.

/**
 * ResultModel containts {@link ResultDownload ResultDownload} for observing download process, include:
 * <pre>
 *     - result: this is enum {@link ECheckResult ECheckResult} type, with SUCCESS, FAIL and TEMP.
 *     - error: if result is FAIL, error is show what is problem with download proccess. Unless it is empty.
 * </pre>
 */
public class ResultModel extends ViewModel implements Serializable{
    private MutableLiveData<ResultDownload> resultDownload;

    public MutableLiveData<ResultDownload> getResultDownload() {
        if (resultDownload == null){
            resultDownload = new MutableLiveData<ResultDownload>();
        }
        return resultDownload;
    }
}
