package com.khulnasoft.cover.plugin;

import com.google.gson.Gson;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.logging.Logger;

public class ModelPrompter {
  private static final String SYSTEM_PROMPT = """
      The userPrompt input will be provided in JSON format following this valid json schema:{"title":
      "TestWithSourceFile","$schema": "http://json-schema.org/draft-07/schema#","type": "object","properties"
      :{"sourceFiles":{"type": "array","description": "The list of source files to pick from only, do not make
      your own up and you must pick one of these as the best match","items":{"type": "string"}},"fileName"
      :{"type": "string","description":"The file name of the Test that is testing one of the source files
      located in the json array element name sourceFiles"},"content":{"type":"string","description":
      "The contents of the test file json element name fileName this is a unit test written in either Java,
      Kotlin, or Groovy. This is the source you must use to determine the best matched source file from one of
      the json items in the json array element sourceFiles."}},"required": ["sourceFiles", "fileName", "content"]
      ,"additionalProperties":false}
      You must find the best file path that from the sourceFiles element and return the match in this mandatory
      json schema:{"title": "MatchedSourceFile" ,"$schema": "http://json-schema.org/draft-07/schema#","type":
      "object","properties":{"filepath":{"type": "string","description":"The file path of one of the entries in
      the json array element sourceFiles that is in the scheme titled TestWithSourceFile. Pick the best match of
       a file that is being tested by the json element fileName and the contents of the source in element source.
       "}},"required":["filepath"],"additionalProperties":false}
      """;
  private static final Gson GSON = new Gson();
  private static final String TEST_FILE_SYSTEM_PROMPT = """
      ### You are a Expert Software Developer in writing Unit Tests.
      #### The userPrompt input will be provided in JSON format following this valid json schema:
        ```json {"$schema":"http://json-schema.org/draft-07/schema#","type":"object","title":"TestFileRequest",
        "description":"Schema for test file request data","properties":{"sourceFilePath":{"type":"string",
        "description":"Path to the source file use this to come up with the path for the test file,
         taking into account it will not be the exact path rather instead of the source directory will be a
          test directory"},
        "sourceContent":{"type":"string","description":"Content of the source file"},"testingFramework":
        {"type":"string","description":"Name of the testing framework that you will use to create the contents
        of the Test file making sure it will compile with no test in the file"}},"required":["sourceFilePath",
        "sourceContent","testingFramework"],"additionalProperties":false}```
      #### Your response must return in the form of this mandatory json schema:
        ```json {"$schema":"http://json-schema.org/draft-07/schema#","type":"object","title":"TestFileResponse",
        "description":"Schema for TestFileResponse record class","properties":{"path":{"type":"string","description"
        :"The path of the test file"},"fileName":{"type":"string","description":"The name of the test file"},"contents"
        :{"type":"string","description":"The contents of the test file"}},"required":["path","fileName","contents"],
        "additionalProperties":false} ```
      """;
  private final Logger logger;
  private final ChatLanguageModel model;
  private final ModelUtility utility;

  public ModelPrompter(Logger logger, ChatLanguageModel model, ModelUtility util) {
    this.logger = logger;
    this.model = model;
    this.utility = util;
  }


  public TestInfoResponse chatter(List<File> sourceFiles, File testFile) throws CoverError {
    try {
      List<String> absolutePaths = sourceFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());
      SourceFilePrompt prompt = new SourceFilePrompt(absolutePaths, testFile.getName(), utility.readFile(testFile));
      String userJson = GSON.toJson(prompt);
      logger.debug("User Prompt to Model: {}", userJson);
      ChatMessage systemChat = new SystemMessage(SYSTEM_PROMPT);
      ChatMessage userChat = new UserMessage(userJson);
      Response<AiMessage> message = model.generate(systemChat, userChat);
      logger.info("Model Response {} for TestFile {}", message.content().text(), testFile);
      String jsonString = utility.extractJson(message.content().text());
      logger.debug("Json extracted {}", jsonString);
      return GSON.fromJson(jsonString, TestInfoResponse.class);
    } catch (Exception e) {
      logger.error("A failure happened trying to get the source file that matches the provided test {}",
          e.getMessage());
      throw new CoverError("Huge error happened need to fix before proceeding ", e);
    }
  }

  public TestFileResponse generateTestFile(File sourceFile, String framework) throws CoverError {
    try {
      TestFileRequest testFileRequest =
          new TestFileRequest(sourceFile.getAbsolutePath(), utility.readFile(sourceFile), framework);
      String userJson = GSON.toJson(testFileRequest);
      logger.debug("User Prompt to Model for TestFileRequest: {}", userJson);

      ChatMessage systemChat = new SystemMessage(TEST_FILE_SYSTEM_PROMPT + framework);
      ChatMessage userChat = new UserMessage(userJson);
      Response<AiMessage> message = model.generate(systemChat, userChat);
      logger.info("Model Response {} for SourceFile {}", message.content().text(), sourceFile);
      String jsonString = utility.extractJson(message.content().text());
      logger.debug("TestFileString response extracted {}", jsonString);
      return GSON.fromJson(jsonString, TestFileResponse.class);
    } catch (Exception e) {
      logger.error("A failure happened trying to get a generated test file for a source provided File {}", sourceFile,
          e);
      throw new CoverError("Error in creating a Test file for SourceFile {} ", e);
    }
  }


}
