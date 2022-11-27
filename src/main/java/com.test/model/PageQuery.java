package com.test.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Page query vo.
 */
@ApiModel("分页参数实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageQuery {
    @ApiModelProperty("当前页")
    private Integer pageIndex;

    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
