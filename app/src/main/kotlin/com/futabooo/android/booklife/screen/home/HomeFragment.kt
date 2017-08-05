package com.futabooo.android.booklife.screen.home

import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futabooo.android.booklife.BookLife
import com.futabooo.android.booklife.R
import com.futabooo.android.booklife.databinding.FragmentHomeBinding
import com.futabooo.android.booklife.model.HomeResource
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import org.jsoup.Jsoup
import retrofit2.Retrofit
import timber.log.Timber

class HomeFragment : Fragment() {

  @Inject lateinit var retrofit: Retrofit
  @Inject lateinit var sharedPreferences: SharedPreferences

  lateinit var binding: FragmentHomeBinding

  lateinit var page: String
  lateinit var volume: String
  lateinit var pageParDay: String

  companion object {

    fun newInstance(): HomeFragment = HomeFragment()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity.application as BookLife).netComponent.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater!!, R.layout.fragment_home, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val date = Date()
    val dateFormat = SimpleDateFormat("yyyy/MM")
    binding.fragmentHomeDate.text = dateFormat.format(date)

    val observable = retrofit.create(HomeService::class.java).home()
    observable.subscribeOn(Schedulers.io()).flatMap {
      val reader = BufferedReader(InputStreamReader(it.byteStream()))
      val result = reader.readLines().filter(String::isNotBlank).toList()

      val readingVolumes = Jsoup.parse(result.toString())
          .select("div.home_index__userdata__main section.home_index__userdata__reading-volume")

      val thisVolume = readingVolumes[0].select("ul span")
      page = thisVolume[0].text()
      volume = thisVolume[2].text()
      pageParDay = thisVolume[4].text()

      val href = Jsoup.parse(result.toString()).select("div.home_index__userdata__side a").attr("href")
      if (href != "" && !sharedPreferences.contains("user_id")) {
        val userId = Integer.parseInt(href.substring(7))
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
      }

      val csrfToken = Jsoup.parse(result.toString()).select("meta[name=csrf-token]")[0].attr("content")

      retrofit.create(HomeService::class.java).getJson(csrfToken, 0, 10)
    }.observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<JsonObject> {
      override fun onSubscribe(d: Disposable) {

      }

      override fun onNext(value: JsonObject) {

        // user_idが保存されていない場合は取得して保存する
        if (!sharedPreferences.contains("user_id")) {
          val jsonArray = value.getAsJsonArray("resources")
          val gson = Gson()
          val resources = gson.fromJson(jsonArray, Array<HomeResource>::class.java)
          val userId = resources[0].user.id
          val editor = sharedPreferences.edit()
          editor.putInt("user_id", userId)
          editor.apply()
        }

        binding.readingPageCurrentMonth.text = page
        binding.readingVolumeCurrentMonth.text = volume
        binding.readingPageParDayCurrentMonth.text = pageParDay
      }

      override fun onError(e: Throwable) {
        Timber.e(e)
      }

      override fun onComplete() {

      }
    })
  }
}
