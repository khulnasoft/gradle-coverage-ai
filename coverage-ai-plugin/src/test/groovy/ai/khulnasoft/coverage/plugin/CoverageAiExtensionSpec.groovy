package com.khulnasoft.coverage.plugin
import org.gradle.api.provider.Property;
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import spock.lang.Specification

class CoverageAiExtensionSpec extends Specification {
    void setup() {
    }
    // Initializes com.khulnasoft.coverage.plugin.ConcreteCoverageAiExtension with a valid Project object
    def "should initialize with valid Project object"() {
        given:
        Project project = Mock(Project)
        ObjectFactory objectFactory = Mock(ObjectFactory)
        Property<String> stringProperty = Mock(Property<String>)
        Property<Integer> intProperty = Mock(Property<Integer>)

        when:
        ConcreteCoverageAiExtension extension = new ConcreteCoverageAiExtension(project)


        then:
        1 * project.getObjects() >> objectFactory
        3 * objectFactory.property(String.class) >> stringProperty
        2 * objectFactory.property(Integer.class) >> intProperty
        extension.getApiKey() == stringProperty
        extension.getCoverage() == intProperty
        extension.getCoverageAiBinaryPath() == stringProperty
        extension.getIterations() == intProperty
        extension.getWanDBApiKey() == stringProperty
    }

}
