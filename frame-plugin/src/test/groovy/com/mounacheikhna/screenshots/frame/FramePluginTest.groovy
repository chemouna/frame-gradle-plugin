package com.mounacheikhna.screenshots.frame

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
/**
 * Created by cheikhnamouna on 1/24/16.
 */
@Ignore
class FramePluginTest {
  private Project project

  @Before
  public void setUp() throws Exception {
    project = ProjectBuilder.builder().build()
    project.apply plugin: FramePlugin
  }

  @Test
  public void framePluginAddsTasksToProject() {
    //assertEquals(1, project.tasks.size())
  }

}
