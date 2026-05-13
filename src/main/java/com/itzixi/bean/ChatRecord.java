package com.itzixi.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
@ToString
@Data
@TableName("chat_record")
public class ChatRecord {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @TableField("content")
    private String content;
    @TableField("chat_type")
    private String chatType;
    @TableField("chat_time")
    private LocalDateTime chatTime;
    @TableField("family_member")
    private String familyMember;
}
