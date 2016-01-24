package com.mounacheikhna.screenshots.frame

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

/**
 * Created by m.cheikhna on 15/01/2015.
 */
class FrameTask extends DefaultTask implements FrameSpec {

  private String screenshotsDir
  private String framesDir
  private String selectedFrame
  private Map<String, String> titles

  @TaskAction
  void performTask() {
    //TODO: provide a clear error to the user when there a trailing / that making folder not recognized
    String screenshotsFolder = "${project.projectDir}/$screenshotsDir/"
    //this is the one that can be provided by users
    String frameFileName = "${project.projectDir}/$framesDir/$selectedFrame"

    String deviceFrameRequiredSize = "1270x1290"
    String labelTextSize = "40"
    String topOffset = "40"

    Map<String, String> screenshotsTitles = titles

    new File(screenshotsFolder).eachFileRecurse {
      if (it.isFile() && it.name.contains(".png")) {
        String imageFileName = it.name
        String taskSuffixName = "$imageFileName" //"${dir.name}$imageFileName"
        String imageDir = it.parent

        //resize screenshot to frame size
        project.tasks.create("c1$taskSuffixName", Exec) {
          workingDir imageDir
          commandLine "convert", "$imageFileName", "-resize", deviceFrameRequiredSize,
                  "$imageFileName"
        }.execute()

        //put screenshot in a frame
        project.tasks.create("c2$taskSuffixName", Exec) {
          workingDir imageDir
          commandLine "convert", "$frameFileName", "$imageFileName",
                  "-gravity", "center", "-compose", "over", "-composite",
                  "-fill", "gold", "-channel", "RGBA", "-opaque", "none", "$imageFileName"
        }.execute()

        def screenshotName = it.name
        String screenshotsTitle = screenshotsTitles.findResult {
          key, value -> if (screenshotName.contains(key)) return value
        }
        screenshotsTitle = screenshotsTitle ?: ""

        //check if any key of titles is contained in it.name
        //-> check if any of a map is contained in a string
        //put background and title
        project.tasks.create("c3$taskSuffixName", Exec) {
          workingDir imageDir
          commandLine "convert", "$imageFileName", "-background", "Gold", "-gravity",
                  "North",
                  "-pointsize", "$labelTextSize", "-density", "100", "-fill", "white",
                  "-annotate", "+0+$topOffset", "${screenshotsTitle}",
                  "$imageFileName"
        }.execute()
      }
    }
  }

  @Override
  void screenshotsDir(String dir) {
    this.screenshotsDir = dir
  }

  @Override
  void setScreenshotsDir(String dir) {
    this.screenshotsDir = dir
  }

  @Override
  void setFramesDir(String dir) {
    this.framesDir = dir
  }

  @Override
  void framesDir(String dir) {
    this.framesDir = dir
  }

  @Override
  void setSelectedFrame(String frameName) {
    this.selectedFrame = frameName
  }

  @Override
  void selectedFrame(String frameName) {
    this.selectedFrame = frameName
  }

  @Override
  void titles(Map<String, String> titles) {
    this.titles = titles
  }

  @Override
  void setTitles(Map<String, String> title) {
    this.titles = titles
  }
}
