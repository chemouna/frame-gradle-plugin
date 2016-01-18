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
    if (!project.plugins.hasPlugin('android')) {
      throw new StopExecutionException("The 'android' plugin is required.")
    }

    buildTypeInput = project.screenshots.buildType
    productFlavorInput = project.screenshots.productFlavor
    locales = project.screenshots.locales

    initDevicesDetails()
    putScreenshotsImagesInPlayFolders()
  }

    @SuppressWarnings("GroovyAssignabilityCheck")
    private void putScreenshotsImagesInPlayFolders() {
      getScreenshotsImagesFolder()
        .listFiles({ it.isDirectory() } as FileFilter)
        .collect { ["dir": it, "device" : findDeviceForDirectory(it)] }
        .collect { println " dir map : $it"}
        .grep { it.get('device') }
        .collect { println " dir/device after filtering by existence of device : $it"}
        .each { it ->
            File dir = it['dir'] as File
            DeviceDetails device = it['device'] as DeviceDetails
            dir.eachFileMatch(~/.*\.png/) {
              println "found match file ${it.name}"
              def foundlocalIndex = StringUtils.indexOfAny(it.name, project.screenshots.locales)
              if (foundlocalIndex != -1) {
                println " Passed by file ${it.name}"
                def locale = it.name.substring(foundlocalIndex, foundlocalIndex + 5)
                def localeFolder = getPlayLocalFolderName(locale)
                println " local : $locale and localeFolder : $localeFolder"
                copyImageToPlayFolder(it.name, it.path, playImagesDir(device, localeFolder), locale)
              }
            }
        }
    }

/*
  private File[] putScreenshotsImagesInPlayFolders() {
    getScreenshotsImagesFolder()
        .listFiles({ it.isDirectory() } as FileFilter)
        .each { dir ->
      DeviceDetails device = findDeviceForDirectory(dir)
      println "device result we got from findDeviceForDirectory : $device"
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
*/

  File getScreenshotsImagesFolder() {
    //TODO: use $productFlavorInput only if non empty
    def path = "${project.screenshots.buildDestDir ?: project.projectDir}/spoon-output/image/"
    println "screenshots folder"
    new File(path)
  }

  static String getPlayLocalFolderName(String locale) {
    locale.replace("_", "-") //this may not be enough for cases like in russia where instead
    // of ro-RU we need to have play folder for RO
  }

/*
  //TODO: fix this, right now it works only emulators names and not phone.
  DeviceDetails findDeviceForDirectory(File dir) {
    def serialNo = dir.name.findAll(~/\d+_/).collect { it.replace("_", "") }.join(".")
    serialNo = serialNo ?: dir.name
    println " findDeviceForDirectory for dir ${dir.name} sn : $serialNo"
    println " All device details that we have : $devicesDetails"
    return this.devicesDetails.findResult {
      it.serialNo.contains(serialNo)
      println " found $it"
    } as DeviceDetails
  }
*/

  DeviceDetails findDeviceForDirectory(File dir) {
    //this part works for emulator
    def patternDeviceNbPart = ~/\d+_/
    def deviceSerialNumber = dir.name.findAll(patternDeviceNbPart).join(".").replace("_", "")
    println deviceSerialNumber
    if (deviceSerialNumber == null || deviceSerialNumber.empty) {
      deviceSerialNumber = dir.name
    }
    def device = this.devicesDetails.find({ it.serialNo.contains(deviceSerialNumber) })
    device
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
    def dirs = [PHONE: "phoneScreenshots", SEVEN_INCH_DEVICE: "sevenInchScreenshots", TEN_INCH_DEVICE: "tenInchScreenshots"]
    return dirs.findResult { type, dir ->
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
    if (serialNo) {
      //since Groovy truth says that a null or empty string is false
      this.devicesDetails.add(new DeviceDetails(type, serialNo))
    }
  }
}
