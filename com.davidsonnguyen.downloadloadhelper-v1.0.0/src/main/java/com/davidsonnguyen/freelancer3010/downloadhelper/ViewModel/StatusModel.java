package com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.EDownloadStatus;

import java.io.Serializable;

 // Created by DavidSonNguyen on 11/15/2017.

/**
 * StatusModel containts and observes {@link EDownloadStatus EDownloadStatus} with 3 values:
 * LOADING, STOP and PAUSE.
 */
public class StatusModel extends ViewModel implements Serializable{
    MutableLiveData<EDownloadStatus> status;

    public MutableLiveData<EDownloadStatus> getStatus() {
        if (status == null){
            status = new MutableLiveData<EDownloadStatus>();
        }
        return status;
    }
}
