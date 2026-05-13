package com.itzixi.Application.controller;

import com.itzixi.bean.ChatEntity;
import com.itzixi.bean.ChatRecord;
import com.itzixi.service.ChatRecordService;
import com.itzixi.service.OllamaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 对话控制器。
 * 提供普通问答、流式问答以及问诊聊天记录查询接口。
 */
@Slf4j
@RestController
@RequestMapping("ollama")
public class OllamaController {

    @Resource
    private OllamaService ollamaService;

    @Resource
    private ChatRecordService chatRecordService;

    /**
     * 同步调用 Ollama，返回一次性完整回复。
     *
     * @param msg 用户问题
     * @return AI 回复
     */
    @GetMapping("ai/chat")
    public Object aiOllamaChat(@RequestParam String msg) {
        return ollamaService.aiollamachat(msg);
    }

    /**
     * 返回原始流式响应。
     *
     * @param msg 用户问题
     * @return 流式聊天响应
     */
    @GetMapping("ai/stream1")
    public Flux<ChatResponse> aiOllamaStream1(@RequestParam String msg) {
        return ollamaService.aiollamstream1(msg);
    }

    /**
     * 将流式结果收集为字符串列表后返回。
     *
     * @param msg 用户问题
     * @return 分段响应列表
     */
    @GetMapping("ai/stream2")
    public List<String> aiOllamaStream2(@RequestParam String msg) {
        return ollamaService.aiollamstream2(msg);
    }

    /**
     * 问诊场景的流式接口。
     * 用户消息入库后，AI 回复通过 SSE 持续推送给前端。
     *
     * @param chatEntity 请求体
     */
    @PostMapping("ai/doctor/stream3")
    public void aiOllamaStream3(@RequestBody ChatEntity chatEntity) {
        log.info(chatEntity.toString());
        String userName = chatEntity.getCurrentUserName();
        String message = chatEntity.getMessage();
        ollamaService.aiOllamaStream3(userName, message);
    }

    /**
     * 查询指定用户的聊天记录。
     *
     * @param userName 用户标识
     * @return 历史聊天记录
     */
    @GetMapping("ai/doctor/records")
    public List<ChatRecord> getDoctorChatRecords(@RequestParam String userName) {
        return chatRecordService.getChatRecordList(userName);
    }
}
