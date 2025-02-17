package com.khulnasoft.coverage.plugin

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import spock.lang.Specification

class CoverageAiBuilderSpec extends Specification {
    void setup() {
    }
    // Builder correctly initializes all fields
    def "should initialize all fields correctly when built"() {
        given:
        Project project = Mock(Project)
        Logger logger = Mock(Logger)
        def builder = new CoverageAiBuilder()
                .apiKey("testApiKey")
                .wanDBApiKey("testWanDBApiKey")
                .iterations(5)
                .coverage(80)
                .coverageAiBinaryPath("/path/to/binary")
                .modelPrompter(Mock(ModelPrompter))
                .javaClassPath(Optional.of("/path/to/class"))
                .javaTestClassPath(Optional.of("/path/to/test/class"))
                .projectPath("/path/to/project")
                .javaClassDir(Optional.of("/path/to/class/dir"))
                .buildDirectory("/path/to/build")
                .coverageAiExecutor(Mock(CoverageAiExecutor))
                .project(project)

        when:
        def coverageAi = builder.build()

        then:
        1 * project.getLogger() >> logger
        coverageAi.apiKey == "testApiKey"
        coverageAi.wanDBApiKey == "testWanDBApiKey"
        coverageAi.iterations == 5
        coverageAi.coverage == 80
        coverageAi.coverageAiBinaryPath == "/path/to/binary"
        coverageAi.modelPrompter != null
        coverageAi.javaClassPath.get() == "/path/to/class"
        coverageAi.javaTestClassPath.get() == "/path/to/test/class"
        coverageAi.projectPath == "/path/to/project"
        coverageAi.javaClassDir.get() == "/path/to/class/dir"
        coverageAi.buildDirectory == "/path/to/build"
        coverageAi.coverageAiExecutor != null
        coverageAi.project != null
    }

}
