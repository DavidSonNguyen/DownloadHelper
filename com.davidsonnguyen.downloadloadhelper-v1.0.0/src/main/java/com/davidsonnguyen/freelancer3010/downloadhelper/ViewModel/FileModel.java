package com.davidsonnguyen.freelancer3010.downloadhelper.ViewModel;

        import android.arch.lifecycle.MutableLiveData;
        import android.arch.lifecycle.ViewModel;

        import com.davidsonnguyen.freelancer3010.downloadhelper.Model.FileDownload;

        import java.io.Serializable;

//Created by DavidSonNguyen on 11/15/2017.

/**
 *FileModel containts {@link FileDownload FileDownload} for observing download proccess,
 * include:
 * <pre>
 *     - size: size(unit: byte) of file you want to download.
 *     - progress: the number of bytes you got from source file.
 *     - done: true if download proccess is complete, otherwise it is false.
 * </pre>
 */
public class FileModel extends ViewModel implements Serializable{
    MutableLiveData<FileDownload> fileDownload;

    public MutableLiveData<FileDownload> getFileDownload() {
        if (fileDownload == null){
            fileDownload = new MutableLiveData<FileDownload>();
        }
        return fileDownload;
    }
}
