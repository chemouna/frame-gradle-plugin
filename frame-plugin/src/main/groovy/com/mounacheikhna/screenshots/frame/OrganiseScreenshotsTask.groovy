package com.mounacheikhna.screenshots.frame

import groovy.io.FileType
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

public class OrganiseScreenshotsTask extends DefaultTask implements ProcessScreenshotsSpec {

    static final String PLAY_FOLDER_RELATIVE_PATH = "src/main/play"
    private static final String PHONE = "phone"
    private static final String SEVEN_INCH_DEVICE = "sevenInch"
    private static final String TEN_INCH_DEVICE = "tenInch"

    private List<DeviceDetails> devicesDetails
    private String[] localesValues
    private String screenshotsOutputDir
    private String phoneSerialNo
    private String sevenInchDeviceSerialNo
    private String tenInchDeviceSerialNo

    @TaskAction
    void performTask() {
        initDevicesDetails()
        if (!project.plugins.hasPlugin('android')) {
            throw new StopExecutionException("The 'android' plugin is required.")
        }
        putScreenshotsImagesInPlayFolders()
    }

    private void putScreenshotsImagesInPlayFolders() {
        File screenshotsOutputFileFolder
        /*if (screenshotsSource == null) {
            screenshotsOutputFileFolder = new File("${project.projectDir}/spoon-output/image/")
        } else { */
        screenshotsOutputFileFolder = new File("${project.projectDir}/$screenshotsOutputDir")
        //}
        //ok pb is here that screenshotsOutputFileFolder doesn't contain folder with devices serialNbs

        def localArray = localesValues;
        screenshotsOutputFileFolder.eachFileRecurse(FileType.DIRECTORIES) {
            dir ->
                DeviceDetails device = findDeviceForDirectory(dir)
                //TODO: fix the pb here where device is found but it isn't working
                if (device != null) {
                    dir.eachFileRecurse(FileType.FILES) {
                        def foundlocalIndex = StringUtils.indexOfAny(it.name, localArray);
                        if (it.name.contains(".png") && foundlocalIndex != -1) {
                            def locale = it.name.substring(foundlocalIndex, foundlocalIndex + 5)
                            def localeFolder = getPlayLocalFolderName(locale)
                            copyImageToPlayFolder(it, playImagesDir(device, localeFolder), locale)
                        }
                    }
                }
        }
    }

    /**
     * To get play locale folder name from a locale that has the format fr_FR
     * Examples : fr_FR -> fr-FR
     *            ro_RU -> ro ..
     */
    String getPlayLocalFolderName(String locale) {
        locale.replace("_", "-")
    }

    //TODO: fix this, right now it works only emulators names and not phone.
    DeviceDetails findDeviceForDirectory(File dir) {
        //TODO: adapt this for #t types of devices : emu, real devices, ...
        /**
         it needs to find and accept :
         "d5246a5f" & "192.168.56.101:5555"
         */
        def patternDeviceNbPart = ~/\d+_/
        def deviceSerialNumber = dir.name.findAll(patternDeviceNbPart).join(".").replace("_", "")
        if (deviceSerialNumber == null || deviceSerialNumber.empty) {
            deviceSerialNumber = dir.name
        }
        this.devicesDetails.find({ it.serialNo.contains(deviceSerialNumber) })
    }

    void copyImageToPlayFolder(File file, playImagesDir, locale) {
        //temp check here -> it shldnt create it multiple times
        def name = "copy${file.name}"
        if (project.tasks.findByName(name)) {
            System.out.println(" task duplicated : $name ")
            return
        }
        //
        project.tasks.create(name, Copy) {
            from file.path
            into playImagesDir
            rename "(.*)_($locale)_(.*).png", '$3.png'
        }.execute()
    }

    String playImagesDir(DeviceDetails deviceDetails, String localeFolder) {
        def playImagesDir = "${project.getProjectDir()}/$PLAY_FOLDER_RELATIVE_PATH/$localeFolder/listing/"
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
        devicesDetails = new ArrayList<>(3)
        if (phoneSerialNo != null && !phoneSerialNo.empty) {
            devicesDetails.add(new DeviceDetails(PHONE, phoneSerialNo))
        }

        if (sevenInchDeviceSerialNo != null && !sevenInchDeviceSerialNo.empty) {
            devicesDetails.add(new DeviceDetails(SEVEN_INCH_DEVICE, sevenInchDeviceSerialNo))
        }

        if (tenInchDeviceSerialNo != null && !tenInchDeviceSerialNo.empty) {
            this.devicesDetails.add(new DeviceDetails(TEN_INCH_DEVICE, tenInchDeviceSerialNo))
        }
    }

    @Override
    void screenshotsOutputDir(String dir) {
        this.screenshotsOutputDir = dir
    }

    @Override
    void localesValues(String[] localesValues) {
        this.localesValues = localesValues
    }

    @Override
    void phoneSerialNo(String phoneSerialNo) {
        this.phoneSerialNo = phoneSerialNo
    }

    @Override
    void sevenInchDeviceSerialNo(String sevenInchDeviceSerialNo) {
        this.sevenInchDeviceSerialNo = sevenInchDeviceSerialNo
    }

    @Override
    void tenInchDeviceSerialNo(String tenInchDeviceSerialNo) {
        this.tenInchDeviceSerialNo = tenInchDeviceSerialNo
    }

}
