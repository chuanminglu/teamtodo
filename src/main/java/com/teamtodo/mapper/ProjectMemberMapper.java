package com.teamtodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamtodo.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProjectMember Mapper
 * Data access layer for ProjectMember entity
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
