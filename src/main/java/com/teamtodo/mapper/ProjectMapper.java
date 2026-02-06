package com.teamtodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamtodo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * Project Mapper
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
