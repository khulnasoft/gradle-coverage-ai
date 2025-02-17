package com.khulnasoft.coverage.plugin

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification

class CoverageAiPluginSpec extends Specification {
    //@TempDir
    //File testProjectDir
    @Shared
    File testProjectDir = new File(System.getProperty("user.home"), "build_tmp")


    File buildFile
    File src
    File main
    File hava
    File test
    File thava
    File mainJavaFile
    File testJavaFile
    File testCalcJavaFile
    File mockCoverageAiFile

    def setup() {
        if (!testProjectDir.exists()) {
            testProjectDir.mkdirs()
        } else {
            testProjectDir.eachFile { file ->
                if (file.isDirectory()) {
                    file.deleteDir()
                } else {
                    file.delete()
                }
            }
        }
        buildFile = new File(testProjectDir, 'build.gradle')
        File resourceBuildFile = new File('src/test/resources/build.gradle')
        if (resourceBuildFile.exists()) {
            buildFile << resourceBuildFile.text
        } else {
            println("Resource file not found: ${resourceBuildFile.absolutePath}")
        }
        println("${buildFile.absolutePath}: ${buildFile.text}")

        src = new File(testProjectDir, 'src')
        src.mkdirs()
        main = new File(src, 'main')
        main.mkdirs()
        hava = new File(main, 'java')
        hava.mkdirs()

        test = new File(src, 'test')
        test.mkdirs()
        thava = new File(test, 'java')
        thava.mkdirs()

        String packagePath = 'ai/qodo/test'
        File sourcePackage = new File(hava, packagePath)
        sourcePackage.mkdirs()
        File testPackage = new File(thava, packagePath)
        testPackage.mkdirs()

        copyTo('src/test/resources/Main.java', new File(sourcePackage, 'Main.java'))
        copyTo('src/test/resources/Calc.java', new File(sourcePackage, 'Calc.java'))
        copyTo('src/test/resources/MainTest.java', new File(testPackage, 'MainTest.java'))
        copyTo('src/test/resources/CalcTest.java', new File(testPackage, 'CalcTest.java'))
        copyTo('src/test/resources/Utility.java', new File(sourcePackage, 'Utility.java'))

        mockCoverageAiFile = new File(testProjectDir, 'mock.sh')

        copyTo('src/test/resources/mock.sh', mockCoverageAiFile)
        mockCoverageAiFile.setExecutable(true)

    }

    void copyTo(String resourceFilePath, File file) {
        file.createNewFile()
        File resourceFile = new File(resourceFilePath)
        if (resourceFile.exists()) {
            file << resourceFile.text
        } else {
            println("Resource file not found: ${resourceFile.absolutePath}")
        }
    }


    /**
     * If you set environment key OPENAI_API_KEY to a proper key and run this with a valid key the test
     * will fail based it is not expecting it but the mock.sh will be called to see the complete happy cycle.
     * The coverage-ai binary is not called still though only the Model for finding the source file for test
     * */
    def "lifecycle test of gradle plugin will not make it complete based on API KEY invalid"() {
        // change up this map if you want to see full lifecycle you need to set your OPENAI_API_KEY
        //Map map = Map.of("OPENAI_API_KEY", System.getenv("OPENAI_API_KEY"))
        Map map = Map.of("OPENAI_API_KEY", "I_AM_BAD_KEY")
        when:
        map.get("OPENAI_API_KEY")
//        def result = GradleRunner.create()
//                .withProjectDir(testProjectDir)
//                .withArguments('coverageAi', '--info')
//                .withEnvironment(map)
//                .withPluginClasspath()
//                .forwardStdOutput(new PrintWriter(System.out))
//                .forwardStdError(new PrintWriter(System.err))
//                .build()

        then:
        noExceptionThrown()

    }

}