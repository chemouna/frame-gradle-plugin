package com.mounacheikhna.screenshots.frame

/**
 *
 * @author cheikhnamouna.
 */
interface FrameSpec {

  void screenshotsDir(String dir)
  void setScreenshotsDir(String dir)

  void setFramesDir(String dir)
  void framesDir(String dir)

  void setSelectedFrame(String frameName)
  void selectedFrame(String frameName)

  void localTitlesMap(Map<String, Map<String, String>> data)
  void setLocalTitlesMap(Map<String, Map<String, String>> data)

  void setBackgroundColor(String backgroundColor)
  void backgroundColor(String backgroundColor)

  void setBackgroundImage(String backgroundImage)
  void backgroundImage(String backgroundImage)

  void setTextColor(String textColor)
  void textColor(String textColor)

  void setTextSize(int textSize)
  void textSize(int textSize)

  void setTopOffset(int topOffset)
  void topOffset(int topOffset)

}
