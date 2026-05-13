package com.itzixi.utils;

/**
 * 聊天记录类型枚举。
 */
public enum ChatTypeEnum {

    USER("user", "用户发送的内容"),
    BOT("bot", "AI 回复的内容");

    /**
     * 持久化到数据库中的类型值。
     */
    public final String type;

    /**
     * 类型说明。
     */
    public final String value;

    ChatTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
