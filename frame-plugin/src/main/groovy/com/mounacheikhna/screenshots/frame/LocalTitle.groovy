package com.mounacheikhna.screenshots.frame

/**
 * Created by cheikhnamouna on 1/24/16.
 */
class LocalTitle {

  String name;

  //TODO: find a better representation than this
  Map<String, String> data = [:]

  LocalTitle(String name) {
    this.name = name
  }

  LocalTitle(Map<String, String> data, String name) {
    this.data = data
    this.name = name
  }
}
