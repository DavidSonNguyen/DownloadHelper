package com.davidsonnguyen.freelancer3010.downloadhelper.Initial;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by DavidSonNguyen on 11/8/2017.
 */

public interface RetrofitAPI {
    @Streaming
    @GET()
    Call<ResponseBody> downloadUrl(@Url String url);
}
