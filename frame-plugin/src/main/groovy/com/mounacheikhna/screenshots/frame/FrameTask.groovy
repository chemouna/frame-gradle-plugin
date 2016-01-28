package com.mounacheikhna.screenshots.frame

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

/**
 * Created by m.cheikhna on 15/01/2015.
 */
public class FrameTask extends DefaultTask implements FrameSpec {

  String screenshotsDir
  String framesDir
  String selectedFrame
  Map<String, Map<String, String>> localTitlesMap = new HashMap<>()
  String backgroundColor
  String backgroundImage
  String textColor
  int textSize = 100
  int topOffset = 40
  int density = 100
  String deviceFrameRequiredSize = "1270x1290"

  @TaskAction
  void performTask() {
    //TODO: provide a clear error to the user when there a trailing / that making folder not recognized
    String screenshotsFolder = "${project.projectDir}/$screenshotsDir/"
    String frameFileName = "${project.projectDir}/$framesDir/$selectedFrame"

    new File(screenshotsFolder).eachFileRecurse(FileType.FILES) {
      if (it.name.contains(".png")) {
        String imageFileName = it.name
        String taskSuffixName = "$imageFileName"
        String imageDir = it.parent
        String locale = localTitlesMap.keySet().findResult { if(imageFileName.contains(it)) return it }
        if(locale == null) return

        //resize screenshot to frame size
        resizeToFrameSize(taskSuffixName, it)

        //put screenshot in a frame
        List<String> frameArgs = ["convert", "$frameFileName", "$imageFileName",
                                  "-gravity", "center", "-compose", "over", "-composite",
                                  "-fill"]
        if(backgroundColor?.trim()) {
          frameArgs.add(backgroundColor)
        }
        else if(backgroundImage?.trim()) {
          frameArgs.add("${project.projectDir}/$backgroundImage")
        }
        frameArgs.addAll(["-channel", "RGBA", "-opaque", "none", "$imageFileName"])
        project.tasks.create("c2$taskSuffixName", Exec) {
          workingDir imageDir
          commandLine frameArgs
        }.execute()

        Map<String, String> screenshotsTitles = localTitlesMap.get(locale)
        def screenshotName = it.name
        String screenshotsTitle = screenshotsTitles.findResult {
          key, value -> if (screenshotName.contains(key)) return value
        }
        screenshotsTitle = screenshotsTitle ?: ""
        project.tasks.create("c3$taskSuffixName", Exec) {
          workingDir imageDir
          commandLine "convert", "$imageFileName", "-gravity",
                  "North", "-pointsize", "$textSize", "-density", density, "-fill", textColor,
                  "-annotate", "+0+$topOffset", "${screenshotsTitle}",
                  "$imageFileName"
        }.execute()
      }
    }
  }

  void resizeToFrameSize(String taskSuffixName, File file) {
    project.tasks.create("c1$taskSuffixName", Exec) {
      workingDir file.parent
      commandLine "convert", "${file.name}", "-resize", "$deviceFrameRequiredSize", "${file.name}"
    }.execute()
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
  void localTitlesMap(Map<String, Map<String, String>> data) {
    this.localTitlesMap = data
  }

  @Override
  void setLocalTitlesMap(Map<String, Map<String, String>> data) {
    this.localTitlesMap = data
  }

  @Override
  void backgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor
  }

  @Override
  void setBackgroundImage(String backgroundImage) {
    this.backgroundImage = backgroundImage
  }

  @Override
  void backgroundImage(String backgroundImage) {
    this.backgroundImage = backgroundImage
  }

  @Override
  void textColor(String textColor) {
    this.textColor = textColor
  }

  @Override
  void textSize(int textSize) {
    this.textSize = textSize
  }

  @Override
  void topOffset(int topOffset) {
    this.topOffset = topOffset
  }
}
