package com.teamtodo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Task Entity
 */
@Data
@TableName("tasks")
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long projectId;
    
    private String title;
    
    private String description;
    
    private String priority;
    
    private LocalDateTime deadline;
    
    private Long creatorId;
    
    private Long assigneeId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
