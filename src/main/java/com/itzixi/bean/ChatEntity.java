package com.itzixi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 问诊聊天请求体。
 * 前端提交消息时，会携带当前用户标识和用户输入内容。
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ChatEntity {

    /**
     * 当前用户名称或会话标识。
     */
    private String currentUserName;

    /**
     * 用户发送的消息内容。
     */
    private String message;
}
