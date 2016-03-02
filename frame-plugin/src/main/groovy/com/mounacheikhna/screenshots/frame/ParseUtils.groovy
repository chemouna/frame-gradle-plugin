package com.mounacheikhna.screenshots.frame

final class ParseUtils {

    public static Properties parseProperties(String path) {
        Properties properties = new Properties()
        File propertiesFile = new File(path)
        properties.load(propertiesFile.newDataInputStream())
        properties
    }

}