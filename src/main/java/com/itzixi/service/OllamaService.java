package com.itzixi.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Ollama 对话服务接口。
 */
public interface OllamaService {

    /**
     * 同步调用 Ollama，直接返回完整结果。
     *
     * @param msg 用户消息
     * @return AI 回复
     */
    Object aiollamachat(String msg);

    /**
     * 基于 Reactor 返回原始流式响应。
     *
     * @param msg 用户消息
     * @return 流式响应
     */
    Flux<ChatResponse> aiollamstream1(String msg);

    /**
     * 获取按分段输出聚合后的流式结果。
     *
     * @param msg 用户消息
     * @return AI 输出文本片段列表
     */
    List<String> aiollamstream2(String msg);

    /**
     * 问诊场景的流式对话处理。
     * 该方法会将内容通过 SSE 推送给指定用户。
     *
     * @param userName 用户标识
     * @param message 用户消息
     */
    void aiOllamaStream3(String userName, String message);
}
