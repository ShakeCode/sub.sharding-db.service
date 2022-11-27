package com.test.dao;

import com.test.entity.User;
import com.test.entity.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * The interface User dao.
 */
@Mapper
public interface UserDao {
    /**
     * Gets all user.
     * @return the all user
     */
    @Select(" select             id, name, age, address from t_user_m")
    List<UserDO> getAllUser();


    @Insert("insert into t_user(nickname,password,sex,birthday) values(#{nickname},#{password},#{sex},#{birthday})")
    void addUser(User user);

    @Select("select * from t_user")
    List<User> findUsers();
}
