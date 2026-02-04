package com.teamtodo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project Entity
 * Represents a project in the system
 */
@Data
@TableName("projects")
public class Project {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
