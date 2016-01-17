package com.mounacheikhna.screenshots

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.StopExecutionException

/**
 * Created by m.cheikhna on 31/12/2015.
 */
class ScreenshotsPlugin implements Plugin<Project> {

    private static final String TASK_PREFIX = "screenshots"
    private static final String GROUP_SCREENSHOTS = "screenshots"

    @Override
    void apply(Project project) {

        if (!hasPlugin(project, AppPlugin)) {
            throw new StopExecutionException("The 'com.android.application' plugin is required.")
        }
        project.extensions.add("screenshots", ScreenshotsExtension)

        String taskName = "${TASK_PREFIX}"
        Task screenshotsTask = project.tasks.create(taskName, ProcessSpoonOutputTask)
        screenshotsTask.group = GROUP_SCREENSHOTS
        screenshotsTask.description = "Takes screenshots generated by spoon on all the connected devices for variation and copies them into play folder each in the right place."

        project.afterEvaluate {
            Task imageMagicAll = createImageMagicAllTask(project)
            /*Task spoonTask = project.tasks.find {
                it.name.contains("spoon") && it.name.contains("Screenshots")
            }
            imageMagicAll.dependsOn spoonTask
            */
            Task runSpoonTask = createSpoonRunTask(project)
            imageMagicAll.dependsOn runSpoonTask
            screenshotsTask.dependsOn imageMagicAll
        }
    }

    private Task createSpoonRunTask(Project project) {
        String productFlavor = project.screenshots.productFlavor
        String prefixApk = "${project.buildDir}/outputs/apk/app-$productFlavor-${project.screenshots.buildType}"
        println "prefixApk : $prefixApk"
        String apkPath = "$prefixApk-unaligned.apk"
        println "apkPath : $apkPath"
        //TODO: maybe replace -unaligned part with regex match like **
        String testApkPath = "$prefixApk-androidTest-unaligned.apk"
        println "testApkPath : $testApkPath"
        String spoonRunnerLibPath = "${project.rootDir}/" +
            "" + "screenshots-plugin/lib/spoon-runner-1.3.1-jar-with-dependencies.jar"
        println "spoonRunnerLibPath : $spoonRunnerLibPath"
        println " -> cmd : java -jar $spoonRunnerLibPath --apk $apkPath --test-apk $testApkPath"

        //let's try 1st expl by putting in lib/ dir

        Task task = project.tasks.create("spoonRunTask", Exec) {
            commandLine "java", "-jar", "$spoonRunnerLibPath",
                "--apk", "$apkPath", "--test-apk", "$testApkPath"
        }
        def flavorTaskName = productFlavor.capitalize()
        println " flavorTaskName : $flavorTaskName"
        println "looking for task assemble$flavorTaskName"
        task.dependsOn project.tasks.findByName("assemble$flavorTaskName")
        task.dependsOn project.tasks.findByName("assembleAndroidTest")
        task
    }

    private Task createImageMagicAllTask(Project project) {
        String buildDestDir = project.screenshots.buildDestDir ?: project.buildDir
        String imagesParentFolder = "$buildDestDir/${project.screenshots.buildType}/image/"
        println " imagesParentFolder : $imagesParentFolder"

        String frameFileName = "${project.projectDir}/frames/galaxy_nexus_port_back.png";
        String deviceFrameRequiredSize = "1270x1290"
        String labelTextSize = "40"
        String topOffset = "40"
        String screenshotsTitle = "Title for this screenshot"

        Task imageMagicAll = project.tasks.create("imageMagicAll")

        //TODO: some parameters shld be provided by user such as background color, text label, frame file path
        new File(imagesParentFolder).listFiles({ it.isDirectory() } as FileFilter)
                .each {
            dir ->
                dir.eachFileRecurse {
                    if (it.isFile() && it.name.contains(".png")) {
                        String imageFileName = it.name
                        String imTaskName = "im${dir.name}${imageFileName.replace(".png", "").replace("_", "")}Task"
                        def newTask = project.tasks.create(imTaskName) {
                            doLast {
                                //not the best way -> TODO: improve later
                                def taskSuffixName = "${dir.name}$imageFileName"
                                def c1 = project.tasks.create("c1$taskSuffixName", Exec) {
                                    workingDir dir
                                    commandLine "convert", "$imageFileName", "-resize", deviceFrameRequiredSize, "$imageFileName"
                                }.execute()

                                def c2 = project.tasks.create("c2$taskSuffixName", Exec) {
                                    workingDir dir
                                    commandLine "convert", "$frameFileName", "$imageFileName",
                                            "-gravity", "center", "-compose", "over", "-composite",
                                            "-fill", "gold", "-channel", "RGBA", "-opaque", "none", "$imageFileName"
                                }.execute()

                                def c3 = project.tasks.create("c3$taskSuffixName", Exec) {
                                    workingDir dir
                                    commandLine "convert", "$imageFileName", "-background", "Gold", "-gravity", "North",
                                            "-pointsize", "$labelTextSize", "-density", "100", "-fill", "white",
                                            "-annotate", "+0+$topOffset", "$screenshotsTitle",
                                            "$imageFileName"
                                }.execute()
                            }
                        }
                        imageMagicAll.dependsOn newTask
                    }
                }
        }

        imageMagicAll.group = GROUP_SCREENSHOTS
        imageMagicAll
    }

    static def hasPlugin(Project project, Class<? extends Plugin> plugin) {
        return project.plugins.hasPlugin(plugin)
    }


}
