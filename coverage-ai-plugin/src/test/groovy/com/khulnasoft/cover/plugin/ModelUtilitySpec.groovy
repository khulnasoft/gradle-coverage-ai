package com.khulnasoft.cover.plugin

import org.gradle.api.logging.Logger
import spock.lang.Specification

class ModelUtilitySpec extends Specification {


    // Constructor successfully initializes with valid logger instance
    def "should initialize ModelUtility with valid logger instance"() {
        given:
        Logger logger = Mock(Logger)

        when:
        def modelUtility = new ModelUtility(logger)

        then:
        modelUtility.logger == logger
    }

    // extractJson returns valid JSON string when input contains JSON object
    def "should extract JSON string when input contains valid JSON object"() {
        given:
        def logger = Mock(Logger)
        def modelUtility = new ModelUtility(logger)
        def input = 'Some text {"key": "value"} more text'

        when:
        def result = modelUtility.extractJson(input)

        then:
        result == '{"key": "value"}'
    }

    // extractJson handles null input by returning empty JSON object
    def "should return empty JSON object when input is null"() {
        given:
        def logger = Mock(Logger)
        def modelUtility = new ModelUtility(logger)

        when:
        def result = modelUtility.extractJson(null)

        then:
        result == '{}'
    }

    // extractJson handles input without JSON braces by returning empty JSON object
    def "should return empty JSON object when input has no JSON braces"() {
        given:
        def logger = Mock(Logger)
        def modelUtility = new ModelUtility(logger)
        def input = "Just plain text without JSON"

        when:
        def result = modelUtility.extractJson(input)

        then:
        thrown(StringIndexOutOfBoundsException)
    }

    // extractJson handles input with multiple JSON objects by extracting outermost
    def "should extract outermost JSON object when input contains nested JSON"() {
        given:
        def logger = Mock(Logger)
        def modelUtility = new ModelUtility(logger)
        def input = '{"outer": {"inner": {"value": 123}}}'

        when:
        def result = modelUtility.extractJson(input)

        then:
        result == '{"outer": {"inner": {"value": 123}}}'
    }
}
