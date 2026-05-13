package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itzixi.bean.ChatRecord;
import com.itzixi.mapper.ChatRecordMapper;
import com.itzixi.service.ChatRecordService;
import com.itzixi.utils.ChatTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天记录服务实现类。
 */
@Service
@Slf4j
public class ChatRecordServiceImpl implements ChatRecordService {

    @Resource
    private ChatRecordMapper chatRecordMapper;

    /**
     * 将聊天记录保存到数据库。
     *
     * @param userName 用户标识
     * @param message 消息内容
     * @param chatType 消息类型
     */
    @Override
    public void saveChatRecord(String userName, String message, ChatTypeEnum chatType) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setFamilyMember(userName);
        chatRecord.setContent(message);
        chatRecord.setChatType(chatType.type);
        chatRecord.setChatTime(LocalDateTime.now());

        chatRecordMapper.insert(chatRecord);
    }

    /**
     * 查询指定用户的历史会话。
     *
     * @param who 用户标识
     * @return 历史聊天记录
     */
    @Override
    public List<ChatRecord> getChatRecordList(String who) {
        QueryWrapper<ChatRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("family_member", who);
        return chatRecordMapper.selectList(queryWrapper);
    }
}
