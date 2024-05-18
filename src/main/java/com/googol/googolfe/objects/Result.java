package com.googol.googolfe.objects;

public class Result {
  private String title;
  private String url;
  private String citation;

  public Result(String title, String citation, String url) {
    this.title = title;
    this.citation = citation;
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getCitation() {
    return citation;
  }
  
  public void setCitation(String citation) {
    this.citation = citation;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
