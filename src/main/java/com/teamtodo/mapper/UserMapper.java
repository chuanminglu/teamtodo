package com.teamtodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamtodo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper
 * Example mapper to demonstrate MyBatis Plus integration
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
