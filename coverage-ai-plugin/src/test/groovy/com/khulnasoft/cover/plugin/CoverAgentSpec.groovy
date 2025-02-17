package com.khulnasoft.cover.plugin

import dev.langchain4j.model.openai.OpenAiChatModel
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.*
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Paths

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O

class CoverageAiSpec extends Specification {

    @Shared
    CoverageAiBuilder builder = CoverageAiBuilder.builder()

    void setup() {}

    def "Not Found JavaCompileTask null "() {
        given:
        Project project = Mock(Project)
        builder.project(project)
        CoverageAi coverageAi = builder.build()
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)

        when:
        coverageAi.setJavaTestClassPath()

        then:
        1 * project.getTasks() >> container
        1 * container.withType(JavaCompile.class) >> collection
        1 * collection.findByName("compileTestJava") >> null
    }

    def "init directories "() {
        given:
        Logger logger = Mock(Logger)
        Project project = Mock(Project)
        builder.project(project)
        ProjectLayout projectLayout = Mock(ProjectLayout)
        Directory projectDirectory = Mock(Directory)
        File direct = Mock(File)
        DirectoryProperty directoryProperty = Mock(DirectoryProperty)
        Provider buildDirectoryProvider = Mock(Provider)
        File realFile = new File('src/test/resources/build.gradle')

        when:
        CoverageAi coverageAi = builder.build()
        coverageAi.initDirectories()

        then:
        2 * project.getLayout() >> projectLayout
        1 * projectLayout.getProjectDirectory() >> projectDirectory
        1 * projectDirectory.getAsFile() >> realFile
        1 * projectLayout.getBuildDirectory() >> directoryProperty
        1 * directoryProperty.getAsFile() >> buildDirectoryProvider
        1 * buildDirectoryProvider.get() >> direct
        1 * project.getLogger() >> logger
        1 * direct.exists() >> false
        1 * direct.mkdirs() >> outcome

        where:
        outcome << [true, false]

    }

    // need to mock out javaCompileCommand
    def "init method initializes correctly"() {
        given:
        Logger logger = Mock(Logger)
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)
        JavaCompile javaTestCompileTask = Mock(JavaCompile)
        DirectoryProperty directoryProperty = Mock(DirectoryProperty)
        Directory directory = Mock(Directory)
        File file = Mock(File)
        FileCollection fileCollection = Mock(FileCollection)
        File realFile = new File('src/test/resources/build.gradle')
        Set<File> fileSet = Set.of(realFile)
        FileTree fileTree = Mock(FileTree)

        ProjectLayout projectLayout = Mock(ProjectLayout)
        Directory projectDirectory = Mock(Directory)

        Provider buildDirectoryProvider = Mock(Provider)

        Project project = Mock(Project)
        OpenAiChatModel.OpenAiChatModelBuilder aiChatModelBuilder = Mock(OpenAiChatModel.OpenAiChatModelBuilder)
        OpenAiChatModel aiChatModel = Mock(OpenAiChatModel)
        builder.project(project).openAiChatModelBuilder(aiChatModelBuilder)

        CompileOptions javaCompileOptions = Mock(CompileOptions)

        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)
        Configuration testConfiguration = Mock(Configuration)
        DependencySet testDependencySet = Mock(DependencySet)


        when:
        CoverageAi coverageAi = builder.build()
        coverageAi.init()

        then:
        1 * project.getConfigurations() >> configurationContainer
        1 * configurationContainer.getByName("testImplementation") >> testConfiguration
        1 * testConfiguration.getDependencies() >> testDependencySet
        //_ * testDependencySet.isEmpty() >> true

        _ * javaTestCompileTask.getOptions() >> javaCompileOptions
        _ * javaCompileOptions.getAllCompilerArgs() >> [""]
        _ * aiChatModelBuilder.apiKey(_) >> aiChatModelBuilder
        _ * aiChatModelBuilder.modelName(GPT_4_O) >> aiChatModelBuilder
        _ * aiChatModelBuilder.maxTokens(500) >> aiChatModelBuilder
        _ * aiChatModelBuilder.build() >> aiChatModel
        _ * project.getLogger() >> logger
        _ * logger.debug("Root Project path {}", _)
        _ * logger.debug("Build directory already exists: {}", _)
        _ * project.getTasks() >> container
        _ * container.withType(JavaCompile.class) >> collection
        _ * collection.findByName(_) >> javaTestCompileTask
        _ * javaTestCompileTask.getDestinationDirectory() >> directoryProperty
        _ * directoryProperty.get() >> directory
        _ * directory.getAsFile() >> file
        _ * file.getAbsolutePath() >> "/apth"
        _ * javaTestCompileTask.getClasspath() >> fileCollection
        _ * fileCollection.getFiles() >> fileSet
        _ * javaTestCompileTask.getSource() >> fileTree
        _ * fileTree.getFiles() >> fileSet
        _ * project.getLayout() >> projectLayout
        _ * projectLayout.getProjectDirectory() >> projectDirectory
        _ * projectDirectory.getAsFile() >> realFile
        _ * projectLayout.getBuildDirectory() >> directoryProperty
        _ * directoryProperty.getAsFile() >> buildDirectoryProvider
        _ * buildDirectoryProvider.get() >> realFile
    }

    // Successful execution of the invoke method with valid project setup
    def "should execute invoke method successfully with valid project setup"() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)
        def modelPrompter = Mock(ModelPrompter)
        def coverageAiExecutor = Mock(CoverageAiExecutor)
        def builder = new CoverageAiBuilder()
                .apiKey("validApiKey")
                .wanDBApiKey("validWanDBApiKey")
                .iterations(10)
                .coverage(80)
                .coverageAiBinaryPath("/path/to/binary")
                .project(project)
                .openAiChatModelBuilder(Mock(OpenAiChatModel.OpenAiChatModelBuilder))
                .modelPrompter(modelPrompter)
                .coverageAiExecutor(coverageAiExecutor)
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)
        JavaCompile javaTestCompileTask = Mock(JavaCompile)
        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)
        DependencyHandler dependencyHandler = Mock(DependencyHandler)
        Dependency dependency = Mock(Dependency)
        Configuration conf = Mock(Configuration)
        Set<File> files = [new File('src/test/resources/build.gradle')]

        when:
        CoverageAi coverageAi = builder.build()
        coverageAi.javaCompileCommand = Optional.of("src/test/resources/mock.sh")
        coverageAi.javaTestCompileCommand = Optional.of("src/test/resources/mock.sh")
        coverageAi.javaTestSourceFiles.add(new File('src/test/resources/CalcTest.java'))
        coverageAi.invoke()

        then:
        _ * conf.resolve() >> files
        _ * project.getDependencies() >> dependencyHandler
        _ * project.getConfigurations() >> configurationContainer
        _ * dependencyHandler.create(_) >> dependency
        _ * configurationContainer.detachedConfiguration(_) >> conf
        _ * project.getTasks() >> container
        _ * container.withType(JavaCompile.class) >> collection
        _ * collection.findByName(_) >> javaTestCompileTask

        _ * project.getLogger() >> logger
        _ * modelPrompter.chatter(_, _) >> new TestInfoResponse("sourceFilePath")
        _ * coverageAiExecutor.execute(_, _, _, _, _, _) >> "success"
    }

    def "should delete file if it exists and log info"() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)

        // Set up the project to return the mocked logger
        project.getLogger() >> logger

        // Use the real builder to set the project
        builder.project(project)
        CoverageAi fileDeletion = builder.build() // Assuming build() creates a CoverageAi instance
        def filePath = "existingFile.txt"
        def path = Paths.get(filePath)
        Files.createFile(path) // Create a file to ensure it exists

        when:
        fileDeletion.deleteFileIfExists(filePath)

        then:
        _ * Files.delete(path)
        1 * logger.info("Deleted file: {}", filePath)

        cleanup:
        Files.deleteIfExists(path) // Clean up the file if it still exists
    }

    @TempDir
    File tempDir

    def "should create test file with content in new directory"() {
        given:
        TestFileResponse response = new TestFileResponse(tempDir.absolutePath + "/newdir",
                "test.txt",
                "test content")
        def project = Mock(Project)
        def logger = Mock(Logger)

        // Set up the project to return the mocked logger
        project.getLogger() >> logger

        // Use the real builder to set the project
        builder.project(project)
        CoverageAi createTestFileAgent = builder.build()

        when:
        def result = createTestFileAgent.createTestFile(response)

        then:
        result.exists()
        result.parentFile.exists()
        result.text == "test content"
        result.name == "test.txt"
    }

    def "test the javaAgentCommand creation  "() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)

        // Set up the project to return the mocked logger
        project.getLogger() >> logger

        // Use the real builder to set the project
        builder.project(project)
        CoverageAi coverageAiExec = builder.build()
        ConfigurationContainer c = Mock(ConfigurationContainer)
        DependencyHandler handler = Mock(DependencyHandler)
        Configuration configuration = Mock(Configuration)
        File file = new File(tempDir.absolutePath+ "/temp.jar")
        Set<File> files = Set.of(file)

        when:
        def result = coverageAiExec.javaAgentCommand("executePath")

        then:
        _ * project.getConfigurations() >> c
        _ * project.getDependencies() >> handler
        _ * c.detachedConfiguration(_) >> configuration
        _ * configuration.resolve() >> files
        result != null

    }
}
