package com.test.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.test.dao.UserDao;
import com.test.entity.User;
import com.test.entity.UserDO;
import com.test.model.PageQuery;
import com.test.model.ResultVO;
import com.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type User service.
 */
@Service("user-service")
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    /**
     * Instantiates a new User service.
     * @param userDao the user dao
     */
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Gets user.
     * @return the user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)  //指定所有异常回滚操作，事务传播propagation默认是REQUIRED
    public ResultVO<List<UserDO>> getUser(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageIndex(), pageQuery.getPageSize());
        List<UserDO> list = userDao.getAllUser();
        PageInfo<UserDO> pageInfo = new PageInfo<>(list);
        LOGGER.info("getUser result:{}", JSON.toJSONString(pageInfo));
        return ResultVO.successData(pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public ResultVO<String> addUser(User user) {
        userDao.addUser(user);
        return ResultVO.success("add success");
    }

    @Override
    public ResultVO<List<User>> findUsers() {
        return ResultVO.successData(userDao.findUsers());
    }

}
