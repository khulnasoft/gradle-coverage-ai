package com.davidparry.cover.test;

public class Utility {

  public String extractJson(String text) {
    if (text != null) {
      return text.substring(text.indexOf('{'), text.lastIndexOf('}') + 1);
    } else {
      return "{}";
    }
  }

}
