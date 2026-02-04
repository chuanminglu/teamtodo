package com.teamtodo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Task Entity
 * Represents a task in the system
 */
@Data
@TableName("tasks")
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String description;
    
    private String priority;
    
    private String status;
    
    private Long projectId;
    
    private Long creatorId;
    
    private Long assigneeId;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
