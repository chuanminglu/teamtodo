package com.teamtodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamtodo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * Project Mapper
 * Data access layer for Project entity
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
