###分库分表工具
1. mycat
2. sharding-jdbc


```   
Sharding-JDBC 最早是当当网内部使用的一款分库分表框架，到2017年的时候才开始对外开源，这几年在大量社区贡献者的不断迭代下，功能也逐渐完善，现已更名为 ShardingSphere，2020年4⽉16日正式成为 Apache 软件基金会的顶级项目。


ShardingSphere-Jdbc定位为轻量级Java框架，在Java的Jdbc层提供的额外服务。它使用客户端直连数据库，以jar包形式提供服务，可理解为增强版的Jdbc驱动，完全兼容Jdbc和各种ORM框架。


随着版本的不断更迭 ShardingSphere 的核心功能也变得多元化起来。

从最开始 Sharding-JDBC 1.0 版本只有数据分片，到 Sharding-JDBC 2.0 版本开始支持数据库治理（注册中心、配置中心等等），再到 Sharding-JDBC 3.0版本又加分布式事务 （支持 Atomikos、Narayana、Bitronix、Seata），如今已经迭代到了 Sharding-JDBC 4.0 版本。
现在的 ShardingSphere 不单单是指某个框架而是一个生态圈，这个生态圈 Sharding-JDBC、Sharding-Proxy 和 Sharding-Sidecar 这三款开源的分布式数据库中间件解决方案所构成。  

【分库分表带来的问题】
1.事务问题
首先，分库分表最大的隐患就是，事务的一致性， 当需要更新的内容同时分布在不同的库时，不可避免的会产生跨库的事务问题。

原来在一个数据库操 作，本地事务就可以进行控制，分库之后 一个请求可能要访问多个数据库，如何保证事务的一致性，目前还没有简单的解决方案。

2.无法联表的问题
还有一个就是，没有办法进行联表查询了，因为，原来在一个库中的一些表，被分散到多个库，并且这些数据库可能还不在一台服务器，无法关联查询，所以相对应的业务代码可能就比较多了。

3.分页问题
分库并行查询时，如果用到了分页，每个库返回的结果集本身是无序的，只有将多个库中的数据先查出来，然后再根据排序字段在内存中进行排序，如果查询结果过大也是十分消耗资源的。

【分库分表的技术】
目前比较流行的就两种，一种是MyCat，另外一种则是Sharding-Jdbc，都是可以进行分库的，

MyCat是一个数据库中间件，Sharding-Jdbc是以 jar 包提供服务的jdbc框架。

Mycat和Sharding-jdbc 实现原理也是不同：

Mycat的原理中最重要的一个动词是“拦截”，它拦截了用户发送过来的SQL语句，首先对SQL语句做了一些特定的分析：如分库分表分析、路由分析、读写分离分析、缓存分析等，然后将此SQL发往后端的真实数据库，并将返回的结果做适当的处理，最终再返回给用户。

而Sharding-JDBC的原理是接受到一条SQL语句时，会陆续执行SQL解析 => 查询优化 => SQL路由 => SQL改写 => SQL执行 => 结果归并 ，最终返回执行结果。
```

###官方文档：
1. https://shardingsphere.apache.org/document/current/cn/overview/

### 创建数据库
CREATE DATABASE If Not Exists `sharding-jdbc-db`;

use `sharding-jdbc-db`;

CREATE TABLE If Not Exists `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `birthday` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS order_1;
CREATE TABLE order_1 (
	order_id BIGINT(20) PRIMARY KEY AUTO_INCREMENT ,
	user_id INT(11) ,
	product_name VARCHAR(128),
	COUNT INT(11)
);

DROP TABLE IF EXISTS order_2;
CREATE TABLE order_2 (
	order_id BIGINT(20) PRIMARY KEY AUTO_INCREMENT ,
	user_id INT(11) ,
	product_name VARCHAR(128),
	COUNT INT(11)
);

### 地址
1. https://mp.weixin.qq.com/s/G4s6Bwhu9v2xcv3B4H_psQ

### 读写分离- 启动初始化3格数据源
[2022-11-27 15:15:36.865]  INFO [data.center.service] 20636 [restartedMain]  [com.alibaba.druid.pool.DruidDataSource  ] 1010 : {dataSource-1} inited
[2022-11-27 15:15:36.941]  INFO [data.center.service] 20636 [restartedMain]  [com.alibaba.druid.pool.DruidDataSource  ] 1010 : {dataSource-2} inited
[2022-11-27 15:15:36.946]  INFO [data.center.service] 20636 [restartedMain]  [com.alibaba.druid.pool.DruidDataSource  ] 1010 : {dataSource-3} inited

### 读写分离- 读取数据
[2022-11-27 15:16:27.707]  INFO [data.center.service] 20636 [http-nio-8059-exec-2]  [com.test.filter.LoginFilter             ] 47 : ------------- LoginFilter start -------------
[2022-11-27 15:16:27.708]  INFO [data.center.service] 20636 [http-nio-8059-exec-2]  [com.test.filter.LoginFilter             ] 48 : 请求地址:http://127.0.0.1:8059/v1/user/findUsers,GET
[2022-11-27 15:16:27.708]  INFO [data.center.service] 20636 [http-nio-8059-exec-2]  [com.test.filter.LoginFilter             ] 49 : 远程地址:127.0.0.1
[2022-11-27 15:16:27.755]  INFO [data.center.service] 20636 [http-nio-8059-exec-2]  [ShardingSphere-SQL                      ] 89 : Rule Type: master-slave
[2022-11-27 15:16:27.756]  INFO [data.center.service] 20636 [http-nio-8059-exec-2]  [ShardingSphere-SQL                      ] 89 : SQL: select * from t_user ::: DataSources: ds2

###分表写库日志
```
[2022-11-27 15:27:45.227]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.228]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.228]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.228]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_2 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [100, 空调0, 10, 803647374747500545]
[2022-11-27 15:27:45.244]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.244]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.245]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.246]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_1 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [101, 空调1, 10, 803647374990770176]
[2022-11-27 15:27:45.248]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.248]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.249]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.249]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_2 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [102, 空调2, 10, 803647375007547393]
[2022-11-27 15:27:45.251]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.251]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.252]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.253]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_1 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [103, 空调3, 10, 803647375020130304]
[2022-11-27 15:27:45.256]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.256]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.256]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.257]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_2 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [104, 空调4, 10, 803647375041101825]
[2022-11-27 15:27:45.259]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.259]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.259]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.261]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_1 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [105, 空调5, 10, 803647375053684736]
[2022-11-27 15:27:45.263]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.263]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.263]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.263]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_2 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [106, 空调6, 10, 803647375070461953]
[2022-11-27 15:27:45.265]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.266]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.266]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.266]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_1 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [107, 空调7, 10, 803647375078850560]
[2022-11-27 15:27:45.268]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.269]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.269]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.269]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_2 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [108, 空调8, 10, 803647375091433473]
[2022-11-27 15:27:45.272]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 15:27:45.272]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 15:27:45.272]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=17)], parametersIndex=3, logicSQL=INSERT INTO order(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@67d8eb93, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@39efbbf8, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@aa0a44a])])
[2022-11-27 15:27:45.273]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_1 (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [109, 空调9, 10, 803647375108210688]
[2022-11-27 15:27:45.289]  INFO [data.center.service] 6720 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 60 : ------------- LoginFilter end, url: http://127.0.0.1:8059/v1/order/save  waste time:396 ms -------------
```  

###分表查询聚合数据日志
```
[2022-11-27 16:36:15.008]  INFO [data.center.service] 33132 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:36:15.008]  INFO [data.center.service] 33132 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: select * from order p where p.order_id in  (  ? , ? )
[2022-11-27 16:36:15.008]  INFO [data.center.service] 33132 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: SelectStatement(super=DQLStatement(super=AbstractSQLStatement(type=DQL, tables=Tables(tables=[Table(name=order, alias=Optional.of(p))]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=order_id, tableName=order), operator=IN, compareOperator=null, positionValueMap={}, positionIndexMap={0=0, 1=1})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order, quoteCharacter=NONE, schemaNameLength=0)], parametersIndex=2, logicSQL=select * from order p where p.order_id in  (  ? , ? ))), containStar=true, firstSelectItemStartIndex=7, selectListStopIndex=7, groupByLastIndex=0, items=[StarSelectItem(owner=Optional.absent())], groupByItems=[], orderByItems=[], limit=null, subqueryStatement=null, subqueryStatements=[], subqueryConditions=[])
[2022-11-27 16:36:15.008]  INFO [data.center.service] 33132 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: select * from order_1 p where p.order_id in  (  ? , ? ) ::: [803647374990770176, 803647374747500545]
[2022-11-27 16:36:15.009]  INFO [data.center.service] 33132 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: select * from order_2 p where p.order_id in  (  ? , ? ) ::: [803647374990770176, 803647374747500545]
[{user_id=101, COUNT=10, order_id=803647374990770176, product_name=空调1}, {user_id=100, COUNT=10, order_id=803647374747500545, product_name=空调0}]
```            

###分库分表
1. 创建库
create DATABASE IF NOT EXISTS order1;

create DATABASE IF NOT EXISTS order2;


2. 2个数据库下分表创建表
use `order1`;
DROP TABLE IF EXISTS order_info;
CREATE TABLE order_info (
	order_id BIGINT(20) PRIMARY KEY AUTO_INCREMENT ,
	user_id INT(11) ,
	product_name VARCHAR(128),
	COUNT INT(11)
);

use `order2`;
DROP TABLE IF EXISTS order_info;
CREATE TABLE order_info (
	order_id BIGINT(20) PRIMARY KEY AUTO_INCREMENT ,
	user_id INT(11) ,
	product_name VARCHAR(128),
	COUNT INT(11)
);

###分库分表日志
``` 
[2022-11-27 16:42:57.794]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 48 : 请求地址:http://127.0.0.1:8059/v1/order/saveFk,POST
[2022-11-27 16:42:57.794]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 49 : 远程地址:127.0.0.1
[2022-11-27 16:42:58.131]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.131]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.133]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.133]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [0, 空调0, 1, 803666303234605057]
[2022-11-27 16:42:58.150]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.150]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.152]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.152]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [1, 空调1, 1, 803666303490457600]
[2022-11-27 16:42:58.156]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.156]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.156]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.157]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [2, 空调2, 1, 803666303515623425]
[2022-11-27 16:42:58.159]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.159]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.160]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.161]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [3, 空调3, 1, 803666303528206336]
[2022-11-27 16:42:58.163]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.164]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.164]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.164]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [4, 空调4, 1, 803666303544983553]
[2022-11-27 16:42:58.166]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.167]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.168]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.168]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [5, 空调5, 1, 803666303557566464]
[2022-11-27 16:42:58.171]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.171]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.171]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.171]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [6, 空调6, 1, 803666303578537985]
[2022-11-27 16:42:58.174]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.174]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.175]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.175]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [7, 空调7, 1, 803666303591120896]
[2022-11-27 16:42:58.177]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.177]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.177]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.178]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [8, 空调8, 1, 803666303603703809]
[2022-11-27 16:42:58.180]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:42:58.180]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)
[2022-11-27 16:42:58.181]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=order_info, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=user_id, tableName=order_info), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=22)], parametersIndex=3, logicSQL=INSERT INTO order_info(user_id,product_name,COUNT) VALUES(?,?,?)), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[user_id, product_name, COUNT], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@3e31ae84, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@77eeea, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@64e3989c])])
[2022-11-27 16:42:58.182]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: INSERT INTO order_info (user_id, product_name, COUNT, order_id) VALUES (?, ?, ?, ?) ::: [9, 空调9, 1, 803666303616286720]
[2022-11-27 16:42:58.199]  INFO [data.center.service] 20068 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 60 : ------------- LoginFilter end, url: http://127.0.0.1:8059/v1/order/saveFk  waste time:404 ms -------------
```                               

###分库分表聚合查询日志
```
[2022-11-27 16:44:25.872]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 48 : 请求地址:http://127.0.0.1:8059/v1/order/findFk,GET
[2022-11-27 16:44:25.872]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 49 : 远程地址:127.0.0.1
[2022-11-27 16:44:26.198]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Rule Type: sharding
[2022-11-27 16:44:26.199]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Logic SQL: select * from order_info p where p.order_id in  (  ? , ? )
[2022-11-27 16:44:26.200]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : SQLStatement: SelectStatement(super=DQLStatement(super=AbstractSQLStatement(type=DQL, tables=Tables(tables=[Table(name=order_info, alias=Optional.of(p))]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=order_info, quoteCharacter=NONE, schemaNameLength=0)], parametersIndex=2, logicSQL=select * from order_info p where p.order_id in  (  ? , ? ))), containStar=true, firstSelectItemStartIndex=7, selectListStopIndex=7, groupByLastIndex=0, items=[StarSelectItem(owner=Optional.absent())], groupByItems=[], orderByItems=[], limit=null, subqueryStatement=null, subqueryStatements=[], subqueryConditions=[])
[2022-11-27 16:44:26.200]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db1 ::: select * from order_info p where p.order_id in  (  ? , ? ) ::: [803666303234605057, 803666303557566464]
[2022-11-27 16:44:26.200]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [ShardingSphere-SQL                      ] 89 : Actual SQL: db2 ::: select * from order_info p where p.order_id in  (  ? , ? ) ::: [803666303234605057, 803666303557566464]
[{user_id=0, COUNT=1, order_id=803666303234605057, product_name=空调0}, {user_id=5, COUNT=1, order_id=803666303557566464, product_name=空调5}]
[2022-11-27 16:44:26.271]  INFO [data.center.service] 5632 [http-nio-8059-exec-1]  [com.test.filter.LoginFilter             ] 60 : ------------- LoginFilter end, url: http://127.0.0.1:8059/v1/order/findFk  waste time:399 ms -------------
```

```
当把SQL发送给 Sharding 之后，Sharding 会经过五个步骤，然后返回接口，这五个步骤分别是：

SQL解析
SQL路由
SQL改写
SQL执行
结果归并
SQL解析：编写SQL查询的是逻辑表，执行时 ShardingJDBC 要解析SQL，解析的目的是为了找到需要改写的位置。

SQL路由：SQL的路由是指将对逻辑表的操作，映射到对应的数据节点的过程. ShardingJDBC会获取分片键判断是否正确，正确 就执行分片策略(算法) 来找到真实的表。

SQL改写：程序员面向的是逻辑表编写SQL，并不能直接在真实的数据库中执行，SQL改写用于将逻辑 SQL改为在真实的数据库中可以正确执行的SQL。

SQL执行：通过配置规则 order_$->{order_id % 2 + 1}，可以知道当 order_id 为偶数时，应该向 order_1表中插入数据，为奇数时向 order_2表插入数据。

结果归并：将所有真正执行sql的结果进行汇总合并，然后返回。

都知道，要是用Sharding分库分表，那么自然就会有相对应的配置，而这些配置才是比较重要的地方，而其中比较经典的就是分片策略了。

分片策略
分片策略分为分表策略和分库策略，它们实现分片算法的方式基本相同，没有太大的区别，无非一个是针对库，一个是针对表。

而一般分片策略主要是分为如下的几种：

standard：标准分片策略
complex：复合分片策略
inline：行表达式分片策略，使用Groovy的表达式.
hint：Hint分片策略，对应HintShardingStrategy。
none：不分片策略，对应NoneShardingStrategy。
标准分片策略StandardShardingStrategy
使用场景：SQL 语句中有>，>=, <=，<，=，IN 和 BETWEEN AND 操作符，都可以应用此分片策略。

也就是说，SQL 语句中频繁的出现这些符号的时候，而且这个时候还想要进行分库分表的时候，就可以采用这个策略了。

但是这个时候要谨记一些内容，那就是标准分片策略（StandardShardingStrategy），它只支持对单个分片键（字段）为依据的分库分表，并提供了两种分片算法 PreciseShardingAlgorithm（精准分片）和 RangeShardingAlgorithm（范围分片）。

在使用标准分片策略时，精准分片算法是必须实现的算法，用于 SQL 含有 = 和 IN 的分片处理；范围分片算法是非必选的，用于处理含有 BETWEEN AND 的分片处理。

复合分片策略
使用场景：SQL 语句中有>，>=，<=，<，=，IN 和 BETWEEN AND 等操作符，不同的是复合分片策略支持对多个分片键操作。

这里要注意的就是多个分片键，也就是说，如果分片的话需要使用两个字段作为分片键，自定义复合分片策略。

行表达式分片策略
它的配置相当简洁，这种分片策略利用inline.algorithm-expression书写表达式。

这里就是使用的这个，来完成的分片，而且行表达式分片策略适用于做简单的分片算法，无需自定义分片算法，省去了繁琐的代码开发，是几种分片策略中最为简单的。

但是要注意，行表达式分片策略，它只支持单分片键。

Hint分片策略
Hint分片策略（HintShardingStrategy）和其他的分片策略都不一样了，这种分片策略无需配置分片键，分片键值也不再从 SQL中解析，而是由外部指定分片信息，让 SQL在指定的分库、分表中执行。

不分片策略
不分片策略这个没啥可说的，不分片的话，用Sharing-JDBC的话，可能就没啥意思了。毕竟玩的就是分片。
```