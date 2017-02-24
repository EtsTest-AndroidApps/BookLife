package com.futabooo.android.archive.screen.home;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

interface HomeService {
  @GET("/") Observable<ResponseBody> home();
}
