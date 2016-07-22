package com.mounacheikhna.screenshots.frame

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.Ignore

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
  public void allScreenshotsShouldBeFramed() {
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFile("titles.json")
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
    frameTask.titlesFolder("config-very-long-titles")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)

    frameTask.execute()
  }

  @Test
  public void lineBreaksOnTitlesShouldBeApplied() {
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFile("titles-with-line-breaks.json")
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
    frameTask.titlesFile("long-titles.json")
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

  @Test
  public void jsonTitlesFromFolderShouldBeApplied() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFolder("config")
    frameTask.suffixKeyword("_screen")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
    //TODO: assert that there are as many generated files in the output as nb screenshots x nb of properties files
  }

  @Test
  public void propertiesTitlesWithSuffixesShouldBeApplied() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFolder("config-with-suffixes")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.suffixKeyword("_screen")
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
  }

  @Test
  public void jsonWithDifferentConfigNamesShouldBeApplied() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFolder("config-different-locale-name")
    frameTask.suffixKeyword("_screen")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
  }

  @Test
  public void localesWithSamePrefixesShouldNotInterleave() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()

    Task frameTask = project.tasks.create("frameTask", FrameTask.class)
    frameTask.inputDir("screenshots-locales-same-prefix")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_port_back.png")
    frameTask.titlesFolder("config-locales-same-prefix")
    frameTask.suffixKeyword("_screen")
    frameTask.backgroundColor("#4CAF50")
    frameTask.textColor("#FFFFFF")
    frameTask.textSize(40)
    frameTask.topOffset(40)
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
  }

  @Test
  public void customFileFontShouldBeUsed() {
    new File(FIXTURE_WORKING_DIR, "output").deleteDir()
    Task frameTask = project.tasks.create("frameTask", FrameTask.class)

    frameTask.inputDir("screenshots")
    frameTask.outputDir("output")
    frameTask.framesDir("frames")
    frameTask.selectedFrame("galaxy_nexus_frame10.png")
    frameTask.titlesFolder("config-very-long-titles")
    frameTask.suffixKeyword("_screen")
    frameTask.backgroundColor("#FFFFFF")
    frameTask.textColor("#4a4c4a")
    frameTask.textSize(34)
    frameTask.topOffset(80)
    frameTask.screenshotAdjustment("+8+140")
    frameTask.fontFilePath("fonts/OpenSans-Semibold.ttf")
    frameTask.execute()

    Assert.assertTrue(new File("${project.projectDir.path}/output").exists())
  }

}
