package com.mounacheikhna.screenshots

/**
 * Created by m.cheikhna on 31/12/2015.
 */
class ScreenshotsExtension {

  String buildDestDir
  String phone
  String sevenInchDevice
  String tenInchDevice

  //it would be cool to have some options to pass localized
  //custom input like in blablacar with departure & arrival that need
  //to be per country

  List<String> locales = []

  String buildType
  String productFlavor
  String screenshotsClass

}
