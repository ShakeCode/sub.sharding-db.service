package com.test.service;


import com.test.entity.User;
import com.test.entity.UserDO;
import com.test.model.PageQuery;
import com.test.model.ResultVO;

import java.util.List;

/**
 * The type User service.
 */
public interface UserService {
    /**
     * Gets user.
     * @param pageQuery the page query
     * @return the user
     */
    ResultVO<List<UserDO>> getUser(PageQuery pageQuery);

    /**
     * Add user result vo.
     * @param user the user
     * @return the result vo
     */
    ResultVO<String> addUser(User user);

    /**
     * Find users result vo.
     * @return the result vo
     */
    ResultVO<List<User>> findUsers();

}
