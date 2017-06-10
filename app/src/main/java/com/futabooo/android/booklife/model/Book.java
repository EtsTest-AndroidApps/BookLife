package com.futabooo.android.booklife.model;

import com.google.gson.annotations.SerializedName;

public class Book {
  int id;
  String title;
  @SerializedName("image_url") String imageUrl;
  String path;
  int page;
  boolean original;
  @SerializedName("registration_count") int registrationCount;
  Author author;
  @SerializedName("amazon_urls") AmazonUrl amazonUrl;

  public Book(int id, String title, String imageUrl, String path, int page, boolean original, int registrationCount,
      Author author, AmazonUrl amazonUrl) {
    this.id = id;
    this.title = title;
    this.imageUrl = imageUrl;
    this.path = path;
    this.page = page;
    this.original = original;
    this.registrationCount = registrationCount;
    this.author = author;
    this.amazonUrl = amazonUrl;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public boolean isOriginal() {
    return original;
  }

  public void setOriginal(boolean original) {
    this.original = original;
  }

  public int getRegistrationCount() {
    return registrationCount;
  }

  public void setRegistrationCount(int registrationCount) {
    this.registrationCount = registrationCount;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public AmazonUrl getAmazonUrl() {
    return amazonUrl;
  }

  public void setAmazonUrl(AmazonUrl amazonUrl) {
    this.amazonUrl = amazonUrl;
  }
}
