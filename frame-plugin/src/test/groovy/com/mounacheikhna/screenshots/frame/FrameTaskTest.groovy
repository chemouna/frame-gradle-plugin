package com.mounacheikhna.screenshots.frame

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by cheikhnamouna on 1/24/16.
 */
@Ignore
class FrameTaskTest {

  public static final String FIXTURE_WORKING_DIR = new File("src/test/fixtures/app")
  private Project project

  @Before
  public void setUp() throws Exception {
    project = ProjectBuilder.builder().withProjectDir(new File(FIXTURE_WORKING_DIR)).build()
    project.apply plugin: 'java'
    project.evaluate()

    /*def outputFile = new File(FIXTURE_WORKING_DIR, "output")
    if(outputFile.exists()) {
      outputFile.listFiles().each {
        File file -> file.delete()
      }
    }*/
  }

  @Test
  public void allScreenshotsShouldBeFramed() {
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

    File outputFolder = new File(FIXTURE_WORKING_DIR, "output")
    Assert.assertTrue(outputFolder.list().length > 0)

    File screenshotsFolder = new File(FIXTURE_WORKING_DIR, "screenshots")
    //make sure that each screenshot has been framed
    screenshotsFolder.list().each {
      Assert.assertTrue(new File("${outputFolder.path}/$it").exists())
    }
  }

  @Test
  public void longTitlesShouldGoToNextLines() {
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFileName("long-titles.json")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)

    frameTask.execute()
    //TODO: somehow assert that there were line breaks for long titles
  }

  @Test
  public void lineBreaksOnTitlesShouldBeApplied() {
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFileName("titles-with-line-breaks.json")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)

    frameTask.execute()

    //TODO: somehow assert that line breaks were applied

  }


  @Test
  public void outputFolderCreatedIfNotExists() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFileName("long-titles.json")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
  }

  @Test
  public void titlesFromFolderShouldBeApplied() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFolder("titles")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
    Assert.assertTrue(new File("${project.projectDir.path}/output").list().length
          == new File("${project.projectDir.path}/screenshots").list().length)

  }

}
