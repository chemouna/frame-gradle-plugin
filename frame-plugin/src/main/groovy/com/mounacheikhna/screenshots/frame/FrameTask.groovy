package com.mounacheikhna.screenshots.frame

import groovy.io.FileType
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

/**
 * Created by m.cheikhna on 15/01/2015.
 */
public class FrameTask extends DefaultTask implements FrameSpec {

  String inputDir
  String outputDir
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
  String titlesJsonFile
  JsonSlurper jsonSlurper
  Map<String, Map<String, String>> titles
  private String titlesFolder

  @TaskAction
  void performTask() {
    jsonSlurper = new JsonSlurper()
    titles = getTitles()

    //TODO: provide a clear error to the user when there a trailing / that making folder not recognized
    String screenshotsFolderPath = "${project.projectDir}/$inputDir/"
    if (isDirEmpty(screenshotsFolderPath)) {
      throw new GradleException("Input directory is empty")
    }

    if (!new File("${project.projectDir}/$outputDir").exists()) {
      new File("${project.projectDir}/$outputDir").mkdirs()
    }
    File screenshotsFolder = new File(screenshotsFolderPath)
    screenshotsFolder.eachFileRecurse(FileType.FILES) {
      if (it.name.contains(".png")) {
        //TODO: maybe copy it to output folder before processing
        File output = resizeToFrameSize(it)
        frameScreenshot(output)
        addScreenshotTitle(output)
      }
    }
  }

  File resizeToFrameSize(File file) {
    String outputFilePath = "${project.projectDir}/$outputDir/${file.name}"
    project.tasks.create("c1${file.name}", Exec) {
      workingDir file.parent
      commandLine "convert", "${file.name}", "-resize", "$deviceFrameRequiredSize",
              "$outputFilePath"
    }.execute()
    return new File("$outputFilePath")
  }

  void frameScreenshot(File file) {
    String frameFileName = "${project.projectDir}/$framesDir/$selectedFrame"
    List<String> frameArgs = ["convert", "$frameFileName", "${file.name}",
                              "-gravity", "center", "-compose", "over", "-composite"]
    if (backgroundColor?.trim()) {
      frameArgs.add("-fill")
      frameArgs.add(backgroundColor)
      frameArgs.addAll("-channel", "RGBA",)
    } else if (backgroundImage?.trim()) {
      frameArgs.add("-fill")
      frameArgs.add("${project.projectDir}/$backgroundImage")
      frameArgs.addAll("-channel", "RGBA",)
    }
    frameArgs.addAll(["-opaque", "none", "${file.name}"])
    project.tasks.create("c2${file.name}", Exec) {
      workingDir file.parent
      commandLine frameArgs
    }.execute()
  }

  void addScreenshotTitle(File file) {
    String locale = titles.keySet().findResult { if (file.name.contains(it)) return it }
    Map<String, String> screenshotsTitles = titles.get("$locale")
    String screenshotsTitle = screenshotsTitles.findResult {
      key, value -> if (file.name.contains(key)) return value
    }
    screenshotsTitle = screenshotsTitle ?: ""
    project.tasks.create("c3${file.name}", Exec) {
      workingDir file.parent
      commandLine "convert", "${file.name}", "-gravity",
              "North", "-pointsize", "$textSize", "-density", density, "-fill", textColor,
              "-annotate", "+0+$topOffset", "${screenshotsTitle}",
              "${file.name}"
    }.execute()
  }

  Map<String, Map<String, String>> getTitles() {
    if (titlesJsonFile?.trim()) return getTitlesFromJsonFile()
    if (titlesFolder?.trim()) return getTitlesFromTitlesFolder()
    return localTitlesMap
  }

  Map<String, Map<String, String>> getTitlesFromTitlesFolder() {
    File titlesFolder = new File("${getProject().projectDir.getPath()}/${titlesFolder}")
    if (!titlesFolder.exists()) return localTitlesMap

    Map<String, Map<String, String>> titles = new HashMap<>()
    titlesFolder.eachFileRecurse(FileType.FILES, {
      file ->
        if(file.name.contains(".json")) {
          def locale = file.name.replace(".json", "")
          def values = new HashMap<String, String>()
          def titlesJson = this.jsonSlurper.parse(file)
          titlesJson.screens.each {
            values.put(it.keyword, it.title)
          }
          titles.put(locale, values)
        }
        else if(file.name.contains(".properties")) {
          def matcher = (file.name =~/([a-z]*)_([A-Z]*)_(.*)/)[0]
          def locale = "${matcher[1]}_${matcher[2]}"

          def values = new HashMap<String, String>()

          Properties properties = ParseUtils.parseProperties(file.path)
          properties.each {
              values.put(it.key, it.value)
          }
          titles.put(locale, values)
        }
    })

    return titles;
  }

  private Map<String, Map<String, String>> getTitlesFromJsonFile() {
    File titlesJsonFile = new File("${getProject().projectDir.getPath()}/${this.titlesJsonFile}")
    if (!titlesJsonFile.exists()) return localTitlesMap

    def titlesJson = this.jsonSlurper.parse(titlesJsonFile)
    Map<String, Map<String, String>> titles = new HashMap<>()

    titlesJson.titles.each {
      def locale = it.locale
      def values = new HashMap<String, String>()
      it.screens.each {
        values.put(it.keyword, it.title)
      }
      titles.put(locale, values)
    }
    return titles;
  }

  def isDirEmpty = { dirName ->
    def dir = new File("$dirName")
    dir.exists() && dir.directory && (dir.list() as List).empty
  }

  @Override
  void inputDir(String dir) {
    this.inputDir = dir
  }

  @Override
  void outputDir(String dir) {
    this.outputDir = dir
  }

  @Override
  void framesDir(String dir) {
    this.framesDir = dir
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
  void titlesJsonFile(String titlesJsonFile) {
    this.titlesJsonFile = titlesJsonFile
  }

  @Override
  void titlesFolder(String titlesFolder) {
    this.titlesFolder = titlesFolder
  }

  @Override
  void backgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor
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
