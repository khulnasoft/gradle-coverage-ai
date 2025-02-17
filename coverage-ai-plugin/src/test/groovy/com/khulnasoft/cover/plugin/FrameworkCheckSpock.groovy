package com.khulnasoft.cover.plugin

import org.gradle.api.artifacts.Dependency
import spock.lang.Specification

class FrameworkCheckSpock extends Specification {
    // Constructor creates instance with identifier and default checkVersion=false
    def "should create instance with default checkVersion when only identifier provided"() {
        when:
        def check = new FrameworkCheck("test-id")

        then:
        check.getFramework() == "test-id"
        !check.checkVersion
        check.version == "-100"
    }

    // Constructor creates instance with explicit identifier and checkVersion values
    def "should create instance with explicit version check when version provided"() {
        when:
        def check = new FrameworkCheck("test-id", "1.0")

        then:
        check.getFramework() == "test-id"
        check.checkVersion
        check.version == "1.0"
    }
    // matches() handles empty identifier string
    def "should match dependency group containing empty identifier"() {
        given:
        def check = new FrameworkCheck("")
        Dependency dependency = Mock(Dependency)
        dependency.getGroup() >> "some-group"

        expect:
        check.matches(dependency)
    }

    // matches() handles null dependency version when checkVersion=true
    def "should return false when dependency version is null and checking version"() {
        given:
        def check = new FrameworkCheck("test-id", "1.0")
        def dependency = Mock(Dependency)
        dependency.getVersion() >> null

        expect:
        !check.matches(dependency)
    }

    def "check to have a match "() {
        given:
        def check = new FrameworkCheck("test-id", "1.0")
        Dependency dependency = Mock(Dependency)

        when:
        boolean outcome = check.matches(dependency)

        then:
        dependency.getVersion() >> "1.0"
        outcome

    }

    def "check to have a no match on version "() {
        given:
        def check = new FrameworkCheck("test-id", "1.0")
        Dependency dependency = Mock(Dependency)

        when:
        boolean outcome = check.matches(dependency)

        then:
        dependency.getVersion() >> "55.1.0"
        outcome == false

    }

    // Constructor creates instance with explicit identifier and checkVersion values
    def "should create instance with explicit version check when version provided"() {
        when:
        def check = new FrameworkCheck("test-id", "1.0")

        then:
        check.getFramework() == "test-id"
        check.checkVersion
        check.version == "1.0"
    }
}
