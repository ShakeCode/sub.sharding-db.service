package com.test.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User.
 */
@ApiModel("用户实体")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;

    private String nickname;

    private String password;

    private Integer sex;

    private String birthday;
}
