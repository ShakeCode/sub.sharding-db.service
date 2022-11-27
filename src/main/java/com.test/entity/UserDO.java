package com.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class UserDO {
    @ApiModelProperty("主键ID")
    private Integer id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("地址")
    private String address;
}
