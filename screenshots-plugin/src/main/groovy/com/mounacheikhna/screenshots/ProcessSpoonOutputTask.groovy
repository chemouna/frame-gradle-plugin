package com.mounacheikhna.screenshots

import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

/**
 * Created by m.cheikhna on 29/12/2015.
 */
public class ProcessSpoonOutputTask extends DefaultTask {

  static final String PLAY_FOLDER_RELATIVE_PATH = "src/main/play"

  private String buildTypeInput
  private String productFlavorInput
  private CharSequence[] locales

  private String PHONE = "phone"
  private String SEVEN_INCH_DEVICE = "sevenInch"
  private String TEN_INCH_DEVICE = "tenInch"
  private List<DeviceDetails> devicesDetails

  @TaskAction
  void performTask() {
    initDevicesDetails()
    if (!project.plugins.hasPlugin('android')) {
      throw new StopExecutionException("The 'android' plugin is required.")
    }

    buildTypeInput = project.screenshots.buildType
    productFlavorInput = project.screenshots.productFlavor
    locales = project.screenshots.locales

    putScreenshotsImagesInPlayFolders()
  }

  private File[] putScreenshotsImagesInPlayFolders() {
    getScreenshotsImagesFolder()
        .listFiles({ it.isDirectory() } as FileFilter)
        .each { dir ->
      DeviceDetails device = findDeviceForDirectory(dir)
      if (device != null) {
        println " Found device dir : $dir"
        dir.eachFileRecurse {
          CharSequence cs = it.name
          CharSequence[] locales = project.screenshots.locales
          def foundlocalIndex = StringUtils.indexOfAny(cs, locales)
          if (it.isFile() && it.name.contains(".png") && foundlocalIndex != -1) {
            println " Passed by file ${it.name}"
            def locale = it.name.substring(foundlocalIndex, foundlocalIndex + 5)
            def localeFolder = getPlayLocalFolderName(locale)
            copyImageToPlayFolder(it.name, it.path, playImagesDir(device, localeFolder), locale)
          }
        }
      } else {
        println "Couldn't find a device dir"
      }
    }
  }

  File getScreenshotsImagesFolder() {
    //TODO: use $productFlavorInput only if non empty
    def path = "${project.screenshots.buildDestDir ?: project.projectDir}/spoon-output/image/"
    println " screenshots sources folder path : $path"
    def file = new File(path)
    if (file == null) {
      println "Screenshots folder not found."
      throw new IllegalArgumentException("Screenshots folder not found.")
    }
    file
  }

  static String getPlayLocalFolderName(String locale) {
    locale.replace("_", "-") //this may not be enough for cases like in russia where instead
    // of ro-RU we need to have play folder for RO
  }

  //TODO: fix this, right now it works only emulators names and not phone.
  DeviceDetails findDeviceForDirectory(File dir) {
    //this part works for emulator
    //def serialNo = dir.name.findAll(~/\d+_/).join(".").replace("_", "")
    def serialNo = dir.name.findAll(~/\d+_/).collect{ it.replace("_", "") }.join(".")
    serialNo = serialNo ?: dir.name
    this.devicesDetails.find({ it.serialNo.contains(serialNo) })
  }

  void copyImageToPlayFolder(fileName, path, playImagesDir, locale) {
    println " Copying screenshot $fileName from $path to $playImagesDir for $locale"
    project.tasks.create("copy$fileName", Copy) {
      from path
      into playImagesDir
      rename "(.*)_($locale)_(.*).png", '$3.png'
      //TODO: adapt to all types of screenshots (emulator: android & genymotion device or not)
    }.execute()
  }

  String playImagesDir(DeviceDetails deviceDetails, String localeFolder) {
    def playImagesDir = "${project.getProjectDir()}/$PLAY_FOLDER_RELATIVE_PATH/${localeFolder.replace("_", "-")}/listing/"
    def dirs = [PHONE: "phoneScreenshots", SEVEN_INCH_DEVICE: "sevenInchScreenshots",
               TEN_INCH_DEVICE: "tenInchScreenshots"]
    dirs.findResult { type, dir ->
      deviceDetails.type == type ? playImagesDir + dir : playImagesDir
    }
  }

  @SuppressWarnings("GroovyAssignabilityCheck")
  private void initDevicesDetails() {
    this.devicesDetails = new ArrayList<>(3)
    addDevice(PHONE, project.screenshots.phone)
    addDevice(SEVEN_INCH_DEVICE, project.screenshots.sevenInchDevice)
    addDevice(TEN_INCH_DEVICE, project.screenshots.tenInchDevice)
  }

  private void addDevice(String type, String serialNo) {
    if(serialNo) { //since Groovy truth says that a null or empty string is false
      this.devicesDetails.add(new DeviceDetails(type, serialNo))
    }
  }

}
