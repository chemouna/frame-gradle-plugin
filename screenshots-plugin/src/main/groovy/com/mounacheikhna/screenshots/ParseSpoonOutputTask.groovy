package com.mounacheikhna.screenshots

import com.novoda.gradle.command.AndroidCommandPluginExtension
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction
/**
 * Created by m.cheikhna on 01/01/2016.
 */
class ParseSpoonOutputTask extends DefaultTask {

    static final String PLAY_FOLDER_RELATIVE_PATH = "src/main/play"

    //public Device[] allDevices
    //TODO: make a plugin and make these there extensions
    def buildTypeInput
    def productFlavorInput

    public String PHONE = "phone"
    public String SEVEN_INCH_DEVICE = "sevenInch"
    public String TEN_INCH_DEVICE = "tenInch"
    private List<DeviceDetails> devicesDetails

    //TODO: this list should be all possible locales
    //CharSequence[] locales = ["fr_FR", "es_ES", "en_US", "it_IT"] //, "en_GB"

    @TaskAction
    void performTask() {
        println "Perform task "
        initDevicesDetails()

        buildTypeInput = project.screenshots.buildType
        productFlavorInput = project.screenshots.productFlavor
        println project.screenshots.screenshotsClassName

        def pluginEx = project.android.extensions.findByType(AndroidCommandPluginExtension)
        List<String> locales = project.screenshots.locales
        for (String local : locales) {
            def (language, country) = local.tokenize('-')
            devicesDetails.each { DeviceDetails device ->
                //println " changing local for device $device.serialNo to $local"

               /* [["shell", "setprop", "persist.sys.language $language"],
                 ["shell", "setprop", "persist.sys.country $country"],
                 ["shell", "stop"],
                 ["shell", "sleep 5"],
                 ["shell", "start"]
                ].each {
                    println " command for $it"
                    AdbCommand command = [adb: pluginEx.getAdb(), deviceId: device.serialNo, parameters: it]
                    println command.execute().text.trim()
                }*/

                //TODO: need to wait here
                println " end of adb commands to change local of $device.serialNo to $local"
                println " run spoon task for $device.serialNo to $local"

                //println "There ${project.tasks['spoonScreenshotsAndroidTest'].name}"

                //def task = project.tasks.create("spoon$language$country", dependsOn: spoon)
                //task dependsOn Spoon
                //task.execute()

                println " end of spoon task for $device.serialNo to $local"
            }
            //maybe we will need to wait somehow for these adb commands to finish
        }

        putScreenshotsImagesInPlayFolders()
    }

    private File[] putScreenshotsImagesInPlayFolders() {
        getScreenshotsImagesFolder()
                .listFiles({ it.isDirectory() } as FileFilter)
                .each { dir ->
            println " dir : $dir "
            DeviceDetails device = findDeviceForDirectory(dir)
            println "*************"
            println " Device: $device.serialNo , Type: $device.type"
            if (device != null) {
                dir.eachFileRecurse {
                    def foundlocalIndex = StringUtils.indexOfAny(it.name, project.screenshots.locales)
                    println " eachFileRecurse : $it.name & foundlocalIndex : " + foundlocalIndex
                    if (it.isFile() && it.name.contains(".png") && foundlocalIndex != -1) {
                        println " Passed by file ${it.name}"
                        def locale = it.name.substring(foundlocalIndex, foundlocalIndex + 5)
                        def localeFolder = getPlayLocalFolderName(locale)
                        copyImageToPlayFolder(it.name, it.path, playImagesDir(device, localeFolder), locale)
                    }
                }
            }
        }
    }

    File getScreenshotsImagesFolder() {
        //TODO: get these dynamicaly
        def buildType = "debug"
        def productFlavor = "defaultConfig"
        //TODO : when there is only one flavor its just buildType directly
        //def path = "${getProject().getBuildDir()}/custom-report-dir/$productFlavor/$buildType/image/"
        def path = "${project.buildDir}/custom-report-dir/$buildType/image/"
        new File(path)
    }

    /**
     * To get play locale folder name from a locale that has the format fr_FR
     * Examples : fr_FR -> fr-FR
     *            ro_RU -> ro ..
     *
     */
    String getPlayLocalFolderName(String locale) {
        locale.replace("_", "-") //this may not be enough for cases like in russia where instead
        // of ro-RU we need to have play folder for RO
    }

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
        println "--------"
        println " Copying screenshot $fileName from $path to $playImagesDir for $locale"
        println "--------"
        project.tasks.create("copy$fileName", Copy) {
            from path
            into playImagesDir
            rename "(.*)_($locale)_(.*).png", '$3.png'
        }.execute()
    }

    String playImagesDir(DeviceDetails deviceDetails, String localeFolder) {
        def playImagesDir = "${project.getProjectDir()}/$PLAY_FOLDER_RELATIVE_PATH/$localeFolder/listing/"
        //temp -> should depend on local
        if (deviceDetails.type == PHONE) {
            playImagesDir += "phoneScreenshots"
        } else if (deviceDetails.type == SEVEN_INCH_DEVICE) {
            playImagesDir += "sevenInchScreenshots"
        } else if (deviceDetails.type == TEN_INCH_DEVICE) {
            playImagesDir += "tenInchScreenshots"
        }
        playImagesDir
    }

    private void initDevicesDetails() {
        this.devicesDetails = new ArrayList<>(3)

        String phone = project.screenshots.phone
        println " phone : $phone"
        if (phone != null && !phone.empty) {
            this.devicesDetails.add(new DeviceDetails(PHONE, phone))
        }

        String sevenInchDevice = project.screenshots.sevenInchDevice
        println " sevenInchDevice : $sevenInchDevice"
        if (sevenInchDevice != null && !sevenInchDevice.empty) {
            this.devicesDetails.add(new DeviceDetails(SEVEN_INCH_DEVICE, sevenInchDevice))
        }

        String tenInchDevice = project.screenshots.tenInchDevice
        println " tenInchDevice : $tenInchDevice"
        if (tenInchDevice != null && !tenInchDevice.empty) {
            this.devicesDetails.add(new DeviceDetails(TEN_INCH_DEVICE, tenInchDevice))
        }
    }

}
