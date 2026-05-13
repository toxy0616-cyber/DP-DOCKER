package com.itzixi.utils;

/**
 * SSE 消息类型枚举。
 * 前端通过事件名区分普通消息、增量消息和结束消息。
 */
public enum SSEMsgType {

    MESSAGE("message", "单次发送的普通消息"),
    ADD("add", "追加消息，用于流式推送"),
    FINISH("finish", "结束消息"),
    DONE("done", "完成消息");

    /**
     * SSE 事件名称。
     */
    public final String type;

    /**
     * 事件说明。
     */
    public final String value;

    SSEMsgType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
