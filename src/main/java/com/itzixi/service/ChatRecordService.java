package com.itzixi.service;

import com.itzixi.bean.ChatRecord;
import com.itzixi.utils.ChatTypeEnum;

import java.util.List;

/**
 * 聊天记录服务接口。
 */
public interface ChatRecordService {

    /**
     * 保存用户或 AI 的聊天记录。
     *
     * @param userName 用户标识
     * @param message 消息内容
     * @param chatType 消息类型
     */
    void saveChatRecord(String userName, String message, ChatTypeEnum chatType);

    /**
     * 查询指定用户的历史聊天记录。
     *
     * @param userName 用户标识
     * @return 聊天记录列表
     */
    List<ChatRecord> getChatRecordList(String userName);
}
