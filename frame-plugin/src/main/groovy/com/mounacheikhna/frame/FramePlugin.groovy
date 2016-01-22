package com.mounacheikhna.frame

import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by m.cheikhna on 31/12/2015.
 */
class FramePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {

    project.extensions.add("frames", com.mounacheikhna.screenshots.FrameExtension)

    project.afterEvaluate {
      project.tasks.create("FrameScreenshots", FrameTask) {
        framesDir project.frames.framesDir
        selectedFrame project.frames.selectedFrame
      }
    }
  }

}
