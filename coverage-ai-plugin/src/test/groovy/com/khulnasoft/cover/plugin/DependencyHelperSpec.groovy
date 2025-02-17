package com.khulnasoft.cover.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.logging.Logger
import spock.lang.Specification
import org.gradle.api.artifacts.Configuration;
class DependencyHelperSpec extends Specification {

    // Successfully resolve and return jar paths for valid Maven dependency
    def "should resolve and return jar paths when valid maven dependency provided"() {
        given:
        Project project = Mock(Project)
        Logger logger = Mock(Logger)
        ConfigurationContainer configs = Mock(ConfigurationContainer)
        DependencyHandler handler = Mock(DependencyHandler)
        Dependency dependency = Mock(Dependency)
        Configuration configuration = Mock(Configuration)
        def jarFile = new File("test.jar")
        def helper = new DependencyHelper(project, logger)

        project.getConfigurations() >> configs
        project.getDependencies() >> handler
        handler.create("org.test:test:1.0") >> dependency
        configs.detachedConfiguration(dependency) >> configuration
        configuration.resolve() >> [jarFile].toSet()

        when:
        def result = helper.findNeededJars("org.test:test:1.0")

        then:
        result == [jarFile.absolutePath]
    }

    // Log debug messages for each found jar path
    def "should log debug message for each resolved jar path"() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)
        def configs = Mock(ConfigurationContainer)
        def handler = Mock(DependencyHandler)
        def dependency = Mock(Dependency)
        def configuration = Mock(Configuration)
        def jarFile1 = new File("test1.jar")
        def jarFile2 = new File("test2.jar")
        def helper = new DependencyHelper(project, logger)

        project.getConfigurations() >> configs
        project.getDependencies() >> handler
        handler.create("org.test:test:1.0") >> dependency
        configs.detachedConfiguration(dependency) >> configuration
        configuration.resolve() >> [jarFile1, jarFile2].toSet()

        when:
        helper.findNeededJars("org.test:test:1.0")

        then:
        1 * logger.debug("Found jar path {}", jarFile1.absolutePath)
        1 * logger.debug("Found jar path {}", jarFile2.absolutePath)
    }

    // Handle null Maven dependency path
    def "should throw CoverError when null dependency path provided"() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)
        def configs = Mock(ConfigurationContainer)
        def handler = Mock(DependencyHandler)
        def helper = new DependencyHelper(project, logger)

        project.getConfigurations() >> configs
        project.getDependencies() >> handler
        handler.create(null) >> { throw new NullPointerException() }

        when:
        helper.findNeededJars(null)

        then:
        def error = thrown(CoverError)
        error.message == "Failed to find null"
        1 * logger.error("Failed to find needed jars {}", null, _ as NullPointerException)
    }
}
