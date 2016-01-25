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

    //def localTitlesContainer = project.container(LocalTitle)
    /*project.configure(project) {
      project.extensions.create("frames", FrameExtension*//*, localTitlesContainer*//*)
    }*/

    /*localTitlesContainer.whenObjectAdded { LocalTitle localTitle ->
      addLocalTitle(localTitle)
    }*/

    project.afterEvaluate {
      project.task("FrameScreenshots",
              type: FrameTask,
              group: GROUP_SCREENSHOTS) {
        screenshotsDir project.frames.screenshotsDir
        framesDir project.frames.framesDir
        selectedFrame project.frames.selectedFrame
        localTitlesMap project.frames.localTitlesMap
        backgroundColor project.frames.backgroundColor
        textColor project.frames.textColor
        textSize project.frames.textSize
        topOffset project.frames.topOffset
      }
    }
  }

}