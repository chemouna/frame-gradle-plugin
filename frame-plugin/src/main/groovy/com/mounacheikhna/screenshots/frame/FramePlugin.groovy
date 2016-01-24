package com.mounacheikhna.screenshots.frame

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by m.cheikhna on 31/12/2015.
 */
class FramePlugin implements Plugin<Project> {

  private static final String GROUP_SCREENSHOTS = "screenshots"

  @Override
  void apply(Project project) {

    project.extensions.add("frames", FrameExtension)

    project.afterEvaluate {
      Task frameTask = project.tasks.create("FrameScreenshots", FrameTask) {
        screenshotsDir project.frames.screenshotsDir
        framesDir project.frames.framesDir
        selectedFrame project.frames.selectedFrame
      }
      frameTask.group = GROUP_SCREENSHOTS
    }
  }

}
