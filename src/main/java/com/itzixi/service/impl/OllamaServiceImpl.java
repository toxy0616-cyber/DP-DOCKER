package com.itzixi.service.impl;

import com.itzixi.service.ChatRecordService;
import com.itzixi.service.OllamaService;
import com.itzixi.utils.ChatTypeEnum;
import com.itzixi.utils.SSEMsgType;
import com.itzixi.utils.SSEServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

@Service
@Slf4j
public class OllamaServiceImpl implements OllamaService {

    @Resource
    private OllamaChatModel OllamaChatModel;

    @Resource
    private ChatRecordService chatRecordService;

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    private Prompt buildPrompt(String msg) {
        return new Prompt(new UserMessage(msg));
    }

    @Override
    public Object aiollamachat(String msg) {
        return OllamaChatModel.call(msg);
    }

    @Override
    public Flux<ChatResponse> aiollamstream1(String msg) {
        Prompt prompt = buildPrompt(msg);
        return OllamaChatModel.stream(prompt);
    }

    @Override
    public List<String> aiollamstream2(String msg) {
        Prompt prompt = buildPrompt(msg);
        Flux<ChatResponse> streamResponse = OllamaChatModel.stream(prompt);

        return streamResponse.toStream().map(chatResponse -> {
            String content = chatResponse.getResult().getOutput(). getContent() ;
            System.out.println(content);
            log.info(content);
            return content;
        }).collect(Collectors.toList());
        // 修复：移除了重复的 return list; 语句，list 变量未定义
    }

    @Override
    public void aiOllamaStream3(String userName, String message) {
        try {
            chatRecordService.saveChatRecord(userName, message, ChatTypeEnum.USER);
        } catch (Exception e) {
            log.error("save chat record failed", e);
        }

        try {
            Prompt prompt = buildPrompt(message);
            Flux<ChatResponse> streamResponse = OllamaChatModel.stream(prompt);
            StringBuilder aiReplyBuilder = new StringBuilder();

            streamResponse.subscribe(
                    chatResponse -> {
                        String content = chatResponse.getResult().getOutput().getContent();
                        aiReplyBuilder.append(content);
                        SSEServer.sendMessage(userName, content, SSEMsgType.ADD);
                        log.info(content);
                    },
                    error -> {
                        log.error("Error during streaming", error);
                        String errorMessage = "当前AI服务不可用，请检查Ollama是否启动以及模型是否存在。";
                        if (aiReplyBuilder.length() == 0) {
                            aiReplyBuilder.append(errorMessage);
                        }
                        try {
                            chatRecordService.saveChatRecord(userName, aiReplyBuilder.toString(), ChatTypeEnum.BOT);
                        } catch (Exception e) {
                            log.error("save ai chat record failed", e);
                        }
                        SSEServer.sendMessage(userName, errorMessage, SSEMsgType.ADD);
                        SSEServer.sendMessage(userName, "over", SSEMsgType.FINISH);
                    },
                    () -> {
                        if (aiReplyBuilder.length() > 0) {
                            try {
                                chatRecordService.saveChatRecord(userName, aiReplyBuilder.toString(), ChatTypeEnum.BOT);
                            } catch (Exception e) {
                                log.error("save ai chat record failed", e);
                            }
                        }
                        SSEServer.sendMessage(userName, "over", SSEMsgType.FINISH);
                    }
            );
        } catch (Exception e) {
            log.error("failed to start streaming", e);
            String errorMessage = "当前AI服务不可用，请检查Ollama是否启动以及模型是否存在。";
            try {
                chatRecordService.saveChatRecord(userName, errorMessage, ChatTypeEnum.BOT);
            } catch (Exception ex) {
                log.error("save ai chat record failed", ex);
            }
            SSEServer.sendMessage(userName, errorMessage, SSEMsgType.ADD);
            SSEServer.sendMessage(userName, "over", SSEMsgType.FINISH);
        }
    }
}
