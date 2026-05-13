package com.itzixi.Application.controller;

import com.itzixi.utils.SSEMsgType;
import com.itzixi.utils.SSEServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 连接控制器。
 * 负责建立长连接、发送消息、广播、关闭连接以及查询在线人数。
 */
@Slf4j
@RestController
@RequestMapping("sse")
@CrossOrigin(origins = "*")
public class SSEcontroller {

    /**
     * 建立 SSE 长连接。
     *
     * @param userId 当前连接对应的用户标识
     * @return SSE 发射器
     */
    @GetMapping(path = "connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam(defaultValue = "defaultUserId") String userId) {
        log.info("Received userId: {}", userId);
        return SSEServer.connect(userId);
    }

    /**
     * 向指定用户发送一条普通消息。
     *
     * @param userId 用户标识
     * @param message 消息内容
     * @return 固定响应
     */
    @GetMapping("sendMessage")
    public Object sendMessage(@RequestParam String userId, @RequestParam String message) {
        log.info("Sending message to userId: {}, message: {}", userId, message);
        SSEServer.sendMessage(userId, message, SSEMsgType.MESSAGE);
        return "ok";
    }

    /**
     * 向所有在线用户广播消息。
     *
     * @param message 广播内容
     * @return 固定响应
     */
    @GetMapping("sendMessageAll")
    public Object sendMessageAll(@RequestParam String message) {
        log.info("Sending message to all users: {}", message);
        SSEServer.sendMessageToALLUsers(message);
        return "ok";
    }

    /**
     * 向指定用户模拟分段推送多条消息。
     *
     * @param userId 用户标识
     * @param message 基础消息内容
     * @return 固定响应
     * @throws Exception 线程休眠异常
     */
    @GetMapping("sendMessageAdd")
    public Object sendMessageAdd(@RequestParam String userId, @RequestParam String message) throws Exception {
        log.info("Sending multiple messages to userId: {}, base message: {}", userId, message);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(200);
            SSEServer.sendMessage(userId, message + "-" + i, SSEMsgType.ADD);
        }
        return "ok";
    }

    /**
     * 主动关闭指定用户的 SSE 连接。
     *
     * @param userId 用户标识
     * @return 固定响应
     * @throws Exception 保留原方法签名
     */
    @GetMapping("stop")
    public Object stopServer(@RequestParam String userId) throws Exception {
        SSEServer.stopServer(userId);
        return "ok";
    }

    /**
     * 查询当前在线 SSE 连接数量。
     *
     * @return 在线人数
     * @throws Exception 保留原方法签名
     */
    @GetMapping("getOnlineCounts")
    public Object getOnlineCounts() throws Exception {
        return SSEServer.getOnlineCounts();
    }
}
