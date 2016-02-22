package com.mounacheikhna.screenshots.frame

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

/**
 * Created by cheikhnamouna on 1/24/16.
 */
class FrameTaskTest {

  public static final String FIXTURE_WORKING_DIR = new File("src/test/fixtures/app")
  private Project project

  @Before
  public void setUp() throws Exception {
    project = ProjectBuilder.builder().withProjectDir(new File(FIXTURE_WORKING_DIR)).build()
    project.apply plugin: 'java'
    project.evaluate()
  }

  @Test
  public void testGenerateFramedScreenshots() {
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFileName("titles.json")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)

    frameTask.execute()
  }
}
