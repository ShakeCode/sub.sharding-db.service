package com.test.controller;

import com.test.entity.User;
import com.test.model.ResultVO;
import com.test.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The type User controller.
 */
@Api(value = "用户模块接口", tags = "读写分离演示")
@RequestMapping("/v1/user")
@RestController
public class UserController {
    private final UserService userService;

    /**
     * Instantiates a new User controller.
     * @param userService the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Add user string.
     * @param user the user
     * @return the string
     */
    @ApiOperation("只写库写数据")
    @PostMapping("/save")
    public ResultVO<String> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }


    /**
     * Find users list.
     * @return the list
     */
    @ApiOperation("只读库读数据")
    @GetMapping("/findUsers")
    public ResultVO<List<User>> findUsers() {
        return userService.findUsers();
    }
}