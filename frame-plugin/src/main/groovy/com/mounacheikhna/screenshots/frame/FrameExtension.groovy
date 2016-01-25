package com.mounacheikhna.screenshots.frame
/**
 * Created by m.cheikhna on 15/01/2015.
 */
public class FrameExtension {

  String screenshotsDir
  String framesDir
  String selectedFrame
  //NamedDomainObjectContainer<LocalTitle> localTitles
  Map<String, Map<String, String>> localTitlesMap = new HashMap<>()

  public FrameExtension() {
  }

  public FrameExtension(String screenshotsDir, String framesDir, String selectedFrame,
          Map<String, Map<String, String>> localTitlesMap) {
    this.screenshotsDir = screenshotsDir
    this.framesDir = framesDir
    this.selectedFrame = selectedFrame
    this.localTitlesMap = localTitlesMap
  }

  public FrameExtension(Map<String, Map<String, String>> localTitlesMap) {
    this.localTitlesMap = localTitlesMap
  }

  /*FrameExtension(NamedDomainObjectContainer<LocalTitle> localTitles) {
      this.localTitles = localTitles
    }

    def localTitles(Closure closure) {
      localTitles.configure {closure}
    }*/
}
