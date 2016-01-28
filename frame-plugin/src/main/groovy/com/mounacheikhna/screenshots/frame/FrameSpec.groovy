package com.mounacheikhna.screenshots.frame

/**
 *
 * @author cheikhnamouna.
 */
interface FrameSpec {

  void screenshotsDir(String dir)

  void framesDir(String dir)

  void selectedFrame(String frameName)

  void localTitlesMap(Map<String, Map<String, String>> data)

  void backgroundColor(String backgroundColor)

  void backgroundImage(String backgroundImage)

  void textColor(String textColor)

  void textSize(int textSize)

  void topOffset(int topOffset)

}
