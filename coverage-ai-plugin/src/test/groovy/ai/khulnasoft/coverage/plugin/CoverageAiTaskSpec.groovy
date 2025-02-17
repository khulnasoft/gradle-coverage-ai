package com.khulnasoft.coverage.plugin

import dev.langchain4j.model.openai.OpenAiChatModel
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class CoverageAiTaskSpec extends Specification {

    Project project = ProjectBuilder.builder().build()
    CoverageAiTask task = project.tasks.create('coverageAiTask', CoverageAiTask)

    def "test default properties"() {
        expect:
        !task.apiKey.isPresent()
        !task.wanDBApiKey.isPresent()
        !task.iterations.isPresent()
        !task.coverageAiBinaryPath.isPresent()
        !task.coverage.isPresent()
    }

    def "test setting properties"() {
        given:
        task.apiKey.set("test-api-key")
        task.wanDBApiKey.set("test-wandb-api-key")
        task.iterations.set(10)
        task.coverageAiBinaryPath.set("/path/to/binary")
        task.coverage.set(80)

        expect:
        task.apiKey.get() == "test-api-key"
        task.wanDBApiKey.get() == "test-wandb-api-key"
        task.iterations.get() == 10
        task.coverageAiBinaryPath.get() == "/path/to/binary"
        task.coverage.get() == 80
    }

    def "test performTask"() {
        given:
        task.apiKey.set("test-api-key")
        task.wanDBApiKey.set("test-wandb-api-key")
        task.iterations.set(10)
        task.coverageAiBinaryPath.set("/path/to/binary")
        task.coverage.set(80)

        and:
        CoverageAiBuilder builderMock = Mock(CoverageAiBuilder)
        CoverageAi coverageAiMock = Mock(CoverageAi)
        OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilderMock = Mock(OpenAiChatModel.OpenAiChatModelBuilder)

        when:

        CoverageAiBuilder.builder() >> builderMock
        builderMock.project(_) >> builderMock
        builderMock.apiKey(_) >> builderMock
        builderMock.wanDBApiKey(_) >> builderMock
        builderMock.iterations(_) >> builderMock
        builderMock.coverageAiBinaryPath(_) >> builderMock
        builderMock.coverage(_) >> builderMock
        builderMock.openAiChatModelBuilder(_) >> builderMock
        builderMock.build() >> coverageAiMock
        OpenAiChatModel.builder() >> chatModelBuilderMock

        task.performTask()

        then:
        0 * builderMock.project(project)
        0 * builderMock.apiKey("test-api-key")
        0 * builderMock.wanDBApiKey("test-wandb-api-key")
        0 * builderMock.iterations(10)
        0 * builderMock.coverageAiBinaryPath("/path/to/binary")
        0 * builderMock.coverage(80)
        0 * builderMock.openAiChatModelBuilder(chatModelBuilderMock)

    }
}
