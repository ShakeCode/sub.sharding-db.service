package com.test.controller;

import com.test.dao.OrderDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Order controller.
 */
@Api(value = "订单模块接口", tags = "分库分表演示")
@RequestMapping("/v1/order")
@RestController
public class OrderController {
    @Autowired
    private OrderDao orderDao;

    /**
     * Test insert order string.
     * @return the string
     */
    @ApiOperation("分表新增数据")
    @PostMapping("/save")
    public String testInsertOrder() {
        for (int i = 0; i < 10; i++) {
            orderDao.insertOrder(100 + i, "空调" + i, 10);
        }
        return "success";
    }

    /**
     * Test find order by ids list.
     * @return the list
     */
    @ApiOperation("分表查询数据")
    @GetMapping("find")
    public List<Map> testFindOrderByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(803647374990770176L);
        ids.add(803647374747500545L);

        List<Map> list = orderDao.findOrderByIds(ids);
        System.out.println(list);
        return list;
    }

    /**
     * Save fk string.
     * @return the string
     */
    @ApiOperation("分库分表新增数据")
    @PostMapping("/saveFk")
    public String saveFk() {
        for (int i = 0; i < 10; i++) {
            orderDao.insertOrderFk(i, "空调" + i, 1);
        }
        return "success";
    }

    /**
     * Test find order by ids fk list.
     * @return the list
     */
    @ApiOperation("分库分表后的查询")
    @GetMapping("findFk")
    public List<Map> testFindOrderByIdsFk() {
        List<Long> ids = new ArrayList<>();
        ids.add(803666303234605057L);
        ids.add(803666303557566464L);

        List<Map> list = orderDao.findOrderByIdsFk(ids);
        System.out.println(list);
        return list;
    }
}