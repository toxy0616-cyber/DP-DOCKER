package com.itzixi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * SSE 连接管理工具。
 * 统一维护用户与 SseEmitter 的映射关系，并提供消息推送、广播、断开连接等能力。
 */
public class SSEServer {

    private static final Logger log = LoggerFactory.getLogger(SSEServer.class);

    /**
     * 保存在线用户与 SSE 连接的映射关系。
     */
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    /**
     * 当前在线连接数量。
     */
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 建立 SSE 连接并注册回调。
     *
     * @param userId 用户标识
     * @return 对应的 SseEmitter
     */
    public static SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(completionCallback(userId));
        emitter.onError(errorCallback(userId));
        emitter.onTimeout(timeoutCallback(userId));

        sseClients.put(userId, emitter);
        log.info("创建新的 SSE 连接，userId={}", userId);
        onlineCount.getAndIncrement();
        return emitter;
    }

    /**
     * 向指定用户发送消息。
     *
     * @param userId 用户标识
     * @param message 消息内容
     * @param msgType 消息类型
     */
    public static void sendMessage(String userId, String message, SSEMsgType msgType) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }
        if (sseClients.containsKey(userId)) {
            SseEmitter emitter = sseClients.get(userId);
            sendEmitterMessage(emitter, userId, message, msgType);
        }
    }

    /**
     * 向所有在线用户广播普通消息。
     *
     * @param message 广播内容
     */
    public static void sendMessageToALLUsers(String message) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }
        sseClients.forEach((userId, sseEmitter) -> sendEmitterMessage(sseEmitter, userId, message, SSEMsgType.MESSAGE));
    }

    /**
     * 通过指定的 SseEmitter 实际发送消息。
     *
     * @param sseEmitter SSE 发射器
     * @param userId 用户标识
     * @param message 消息内容
     * @param msgType 消息类型
     */
    public static void sendEmitterMessage(SseEmitter sseEmitter, String userId, String message, SSEMsgType msgType) {
        try {
            SseEmitter.SseEventBuilder msg = SseEmitter.event()
                    .id(userId)
                    .data(message)
                    .name(msgType.type);
            sseEmitter.send(msg);
        } catch (IOException e) {
            log.error("用户[{}]消息推送异常", userId);
            removeConnection(userId);
        }
    }

    /**
     * 主动关闭指定用户的 SSE 连接。
     *
     * @param userId 用户 ID
     */
    public static void stopServer(String userId) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }
        SseEmitter sseEmitter = sseClients.get(userId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            removeConnection(userId);
            log.info("SSE 连接关闭成功，userId={}", userId);
        } else {
            log.warn("连接不存在，无需重复关闭");
        }
    }

    /**
     * 连接正常完成时的回调。
     *
     * @param userId 用户 ID
     * @return 回调函数
     */
    private static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE 连接完成并关闭，userId={}", userId);
            removeConnection(userId);
        };
    }

    /**
     * 连接超时时的回调。
     *
     * @param userId 用户 ID
     * @return 回调函数
     */
    private static Runnable timeoutCallback(String userId) {
        return () -> {
            log.info("SSE 连接超时，userId={}", userId);
            removeConnection(userId);
        };
    }

    /**
     * 连接异常时的回调。
     *
     * @param userId 用户 ID
     * @return 回调函数
     */
    private static Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.info("SSE 连接发生错误，userId={}", userId, throwable);
            removeConnection(userId);
        };
    }

    /**
     * 删除连接并更新在线人数。
     *
     * @param userId 用户 ID
     */
    public static void removeConnection(String userId) {
        sseClients.remove(userId);
        log.info("已移除 SSE 连接，userId={}", userId);
        onlineCount.getAndDecrement();
    }

    /**
     * 获取当前在线人数。
     *
     * @return 在线连接数
     */
    public static int getOnlineCounts() {
        return onlineCount.intValue();
    }
}
