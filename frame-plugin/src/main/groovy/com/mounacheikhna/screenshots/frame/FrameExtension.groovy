package com.mounacheikhna.screenshots.frame
/**
 * Created by m.cheikhna on 15/01/2015.
 */
public class FrameExtension {

  String screenshotsDir
  String framesDir
  String selectedFrame
  Map<String, Map<String, String>> localTitlesMap = new HashMap<>()
  String backgroundColor
  String textColor
  int textSize
  int topOffset

  public FrameExtension() {
  }

}
