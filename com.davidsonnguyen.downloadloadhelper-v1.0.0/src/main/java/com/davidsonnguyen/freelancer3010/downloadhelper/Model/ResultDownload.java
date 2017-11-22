package com.davidsonnguyen.freelancer3010.downloadhelper.Model;

import com.davidsonnguyen.freelancer3010.downloadhelper.Enum.ECheckResult;

/**
 * Created by DavidSonNguyen on 11/15/2017.
 */

public class ResultDownload {
    private ECheckResult result;
    private String error;

    public ResultDownload() {
        result = ECheckResult.TEMP;
        error = "";
    }

    public ECheckResult getResult() {
        return result;
    }

    public void setResult(ECheckResult result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
