package com.mounacheikhna.screenshots.frame

/**
 *
 * @author cheikhnamouna.
 */
interface FrameSpec {

  /** Input directory containing screenshots **/
  void inputDir(String dir)

  /** Directory in which to put the framed screenshots, if none is provided inputDir is used **/
  void outputDir(String dir)

  void framesDir(String dir)

  void selectedFrame(String frameName)

  void localTitlesMap(Map<String, Map<String, String>> data)

  void backgroundColor(String backgroundColor)

  void backgroundImage(String backgroundImage)

  void textColor(String textColor)

  void textSize(int textSize)

  void topOffset(int topOffset)

}
