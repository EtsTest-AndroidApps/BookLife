package com.futabooo.android.booklife.screen.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.futabooo.android.booklife.BookLife;
import com.futabooo.android.booklife.R;
import com.futabooo.android.booklife.databinding.FragmentHomeBinding;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.inject.Inject;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

  @Inject Retrofit retrofit;

  private FragmentHomeBinding binding;

  public HomeFragment() {
  }

  public static HomeFragment newInstance() {
    Bundle args = new Bundle();
    //args.putString(key, value);
    HomeFragment f = new HomeFragment();
    f.setArguments(args);

    return f;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((BookLife) getActivity().getApplication()).getNetComponent().inject(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Observable<ResponseBody> observable = retrofit.create(HomeService.class).home();
    observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ResponseBody>() {
          @Override public void onSubscribe(Disposable d) {

          }

          @Override public void onNext(ResponseBody value) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(value.byteStream()));
            StringBuffer result = null;
            try {
              result = new StringBuffer();
              String line;
              while ((line = reader.readLine()) != null) {
                result.append(line);
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
            String icon =
                Jsoup.parse(result.toString()).select("div.default_box a[href^=/u/] div").get(0).attr("style");
            Glide.with(HomeFragment.this)
                .load(icon.substring(icon.indexOf("http://"), icon.indexOf(")")))
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(binding.icon);

            Element readingVolume = Jsoup.parse(result.toString())
                .select("div.default_box [style=font-size:12px;line-height:22px;font-weight:bold;color:#808080;]")
                .get(0)
                .parent();
            binding.readingVolumeThisMonthTitle.setText(readingVolume.child(0).ownText());
            binding.readingPageThisMonth.setText(getString(R.string.reading_page, readingVolume.child(1).ownText()));
            binding.readingVolumeThisMonth.setText(
                getString(R.string.reading_volume, readingVolume.child(2).ownText()));
            binding.readingPageParDayThisMonth.setText(
                getString(R.string.reading_page_par_day, readingVolume.child(3).ownText()));

            Element lastMonthReadingVolume = Jsoup.parse(result.toString())
                .select("div.default_box [style=font-size:12px;line-height:22px;font-weight:bold;color:#808080;]")
                .get(1)
                .parent();
            binding.readingVolumeLastMonthTitle.setText(lastMonthReadingVolume.child(0).ownText());
            binding.readingPageLastMonth.setText(
                getString(R.string.reading_page, lastMonthReadingVolume.child(1).ownText()));
            binding.readingVolumeLastMonth.setText(
                getString(R.string.reading_volume, lastMonthReadingVolume.child(2).ownText()));
            binding.readingPageParDayLastMonth.setText(
                getString(R.string.reading_page_par_day, lastMonthReadingVolume.child(3).ownText()));
          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onComplete() {

          }
        });
  }
}
