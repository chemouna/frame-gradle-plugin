package com.mounacheikhna.screenshots.frame

import groovy.io.FileType
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction
import org.gradle.util.TextUtil

/**
 * Created by m.cheikhna on 15/01/2015.
 */
public class FrameTask extends DefaultTask implements FrameSpec {

  public static final Tuple2<String, Map<String, String>> EMPTY_TUPLE2 = new Tuple2<>("",
          new HashMap<String, String>())
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
  int density = 200
  String deviceFrameRequiredSize = "1270x1290"
  String titlesFile
  JsonSlurper jsonSlurper
  Map<String, Map<String, String>> titles
  String titlesFolder
  String suffixKeyword
  String fontFilePath
  String screenshotAdjustment

  @TaskAction
  void performTask() {
    jsonSlurper = new JsonSlurper()
    titles = getTitles()

    String screenshotsFolderPath = "${project.projectDir}/$inputDir/"
    if (isDirEmpty(screenshotsFolderPath)) {
      throw new GradleException("Input directory is empty")
    }

    final outputFolder = new File("${project.projectDir}/$outputDir")
    if (!outputFolder.exists()) {
      outputFolder.mkdirs()
    }
    File screenshotsFolder = new File(screenshotsFolderPath)
    String suffix = this.suffixKeyword ?: ""
    screenshotsFolder.eachFileRecurse(FileType.FILES) {
      if (it.name.contains(".png")) {
        File output = resizeToFrameSize(it)
        frameScreenshot(output)
        addScreenshotTitle(output, suffix)
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
    // "-page", "+0+180",
  }

  void frameScreenshot(File file) {
    String frameFileName = "${project.projectDir}/$framesDir/$selectedFrame"
    List<String> frameArgs = ["convert", "$frameFileName", "${file.name}",
                              "-gravity", "center", "-geometry", "$screenshotAdjustment",
                              "-compose", "over", "-composite"]
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

  void addScreenshotTitle(File file, String suffix) {
    String locale = titles.keySet().findResult { if (file.name.contains("_"+ it + "_")) return it }
    Map<String, String> screenshotsTitles = titles.get(locale)
    String screenshotsTitle = screenshotsTitles.findResult {
      key, value ->
        if(key != null && key.contains(suffix)) {
          String keyword = key.replace(suffix, "")
          if(file.name.contains(keyword)) return value
        }
    }
    screenshotsTitle = screenshotsTitle ?: ""

    String[] convertArgs = ["convert", "${file.name}", "-gravity", "North"]

    String fontFullPath = "${project.projectDir}/$fontFilePath"
    println "fontFilePath: $fontFullPath & exists : ${new File(fontFullPath).exists()}"

    if((fontFilePath)?.trim() && new File(fontFullPath).exists()) {
      convertArgs += ["-font", "${fontFullPath}"]
    }
    convertArgs += ["-pointsize", "$textSize", "-density", density, "-fill", textColor,
                       "-annotate", "+0+$topOffset", "${screenshotsTitle}", "${file.name}"]

    println "args: $convertArgs"

    project.tasks.create("c3${file.name}", Exec) {
      workingDir file.parent
      commandLine convertArgs
    }.execute()
  }

  Map<String, Map<String, String>> getTitles() {
    if (titlesFile?.trim()) {
      File file = new File("${getProject().projectDir.getPath()}/${this.titlesFile}")
      if (file.exists()) return titlesFromFile(file)
    }
    else if (titlesFolder?.trim()) {
      return getTitlesFromTitlesFolder()
    }
    return localTitlesMap
  }

  Map<String, Map<String, String>> getTitlesFromTitlesFolder() {
    File titlesFolder = new File("${getProject().projectDir.getPath()}/${titlesFolder}")
    if (!titlesFolder.exists()) return localTitlesMap

    Map<String, Map<String, String>> titles = new HashMap<>()
    titlesFolder.eachFileRecurse(FileType.FILES, {
      file ->
        titles.putAll(titlesFromFile(file))
    })

    return titles;
  }

  Map<String, Map<String, String>> titlesFromFile(File file) {
    Tuple2<String, Map<String, String>> res
    switch (file.name) {
      case ~/.*.json$/:
        res = fromJson(file)
        break
      case ~/.*.properties$/:
        res = fromProperties(file)
        break
      default:
        res = EMPTY_TUPLE2
        break
    }
    Map<String, Map<String, String>> all = new HashMap<>()
    all.put(res.first, res.second)
    return all
  }

  static Tuple2<String, Map<String, String>> fromProperties(File file) {
    String[] matcher = (file.name =~ /([a-z]*)_([A-Z]*)(.*)/)[0]
    if(matcher.size() < 3) return EMPTY_TUPLE2
    def locale = "${matcher[1]}_${matcher[2]}"
    def values = new HashMap<String, String>()
    Properties properties = ParseUtils.parseProperties(file.path)
    properties.each {
      values.put(it.key.toString(), it.value.toString())
    }
    return new Tuple2<>(locale, values)
  }

  Tuple2<String, Map<String, String>> fromJson(File file) {
    def locale = file.name.replace(".json", "")
    def values = new HashMap<String, String>()
    def parsed = this.jsonSlurper.parse(file)
    parsed.each {
      key, value ->
      values.put(key.toString(), value.toString())
    }
    return new Tuple2<>(locale, values)
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


  void titlesFile(String titlesFile) {
    this.titlesFile = titlesFile
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

  @Override
  void suffixKeyword(String suffixKeyword) {
    this.suffixKeyword = suffixKeyword
  }

  @Override
  void fontFilePath(String fontFilePath) {
    this.fontFilePath = fontFilePath
  }

  @Override
  void screenshotAdjustment(String screenshotAdjustment) {
    this.screenshotAdjustment = screenshotAdjustment
  }

}
