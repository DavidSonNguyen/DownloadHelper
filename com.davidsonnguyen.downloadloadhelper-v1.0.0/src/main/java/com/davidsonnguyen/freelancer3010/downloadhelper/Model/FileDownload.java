package com.davidsonnguyen.freelancer3010.downloadhelper.Model;

/**
 * Created by DavidSonNguyen on 11/15/2017.
 */

public class FileDownload {
    long progress;
    long size;
    boolean done;

    public FileDownload() {
        progress = 0;
        size = 0;
        done = false;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
