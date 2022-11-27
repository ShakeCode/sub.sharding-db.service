package com.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDao {
    /**
     * 新增订单
     */
    @Insert("INSERT INTO order(user_id,product_name,COUNT) VALUES(#{user_id},#{product_name},#{count})")
    int insertOrder(@Param("user_id") int user_id, @Param("product_name") String product_name, @Param("count") int count);

    /**
     * 查询订单
     */
    @Select({"<script>" +
            "select * from order p where p.order_id in " +
            "<foreach collection='orderIds' item='id' open='(' separator = ',' close=')'>#{id}</foreach>"
            + "</script>"})
    List<Map> findOrderByIds(@Param("orderIds") List<Long> orderIds);


    @Insert("INSERT INTO order_info(user_id,product_name,COUNT) VALUES(#{user_id},#{product_name},#{count})")
    int insertOrderFk(@Param("user_id") int user_id, @Param("product_name") String product_name, @Param("count") int count);


    @Select({"<script>"+
            "select * from order_info p where p.order_id in " +
            "<foreach collection='orderIds' item='id' open='(' separator = ',' close=')'>#{id}</foreach>"
            +"</script>"})
    List<Map> findOrderByIdsFk(@Param("orderIds") List<Long> orderIds);
}
