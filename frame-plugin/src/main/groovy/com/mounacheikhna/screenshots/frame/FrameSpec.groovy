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

  /*void titles(Map<String, String> title)
  void setTitles(Map<String, String> title)*/

  void localTitlesMap(Map<String, Map<String, String>> data)
  void setLocalTitlesMap(Map<String, Map<String, String>> data)

}
