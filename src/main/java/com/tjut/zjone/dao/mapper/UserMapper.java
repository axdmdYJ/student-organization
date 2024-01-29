package com.tjut.zjone.dao.mapper;

import com.tjut.zjone.dao.entity.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @description 针对表【t_user】的数据库操作Mapper
*/
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

}




