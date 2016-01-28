package com.mounacheikhna.screenshots.frame

import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by m.cheikhna on 31/12/2015.
 */
public class FramePlugin implements Plugin<Project> {

  static final String GROUP_SCREENSHOTS = "screenshots"

  @Override
  void apply(Project project) {
    project.extensions.add("frames", FrameExtension)
    project.afterEvaluate {
      project.task("FrameScreenshots",
              type: FrameTask,
              group: GROUP_SCREENSHOTS) {
        //TODO: is there an auto way for this in gradle ?
        inputDir project.frames.inputDir
        outputDir project.frames.outputDir
        framesDir project.frames.framesDir
        selectedFrame project.frames.selectedFrame
        localTitlesMap project.frames.localTitlesMap
        backgroundColor project.frames.backgroundColor
        backgroundImage project.frames.backgroundImage
        textColor project.frames.textColor
        textSize project.frames.textSize
        topOffset project.frames.topOffset
      }
    }
  }

}