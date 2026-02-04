package com.teamtodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamtodo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * Task Mapper
 * Data access layer for Task entity
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
