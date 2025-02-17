package com.khulnasoft.cover.plugin

import spock.lang.Specification

class TestFileResponseSpock extends Specification {

    // Create TestFileResponse with valid path, fileName and contents
    def "should create TestFileResponse with valid values"() {
        when:
        def response = new TestFileResponse("/some/path", "test.txt", "file contents")

        then:
        response.path() == "/some/path"
        response.fileName() == "test.txt"
        response.contents() == "file contents"
    }
}
