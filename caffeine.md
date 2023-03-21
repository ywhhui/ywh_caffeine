本地缓存兜底

apollo配置中心的启动教程(window版)

1. **系统要求：**

   1. jdk1.8+
   2. mysql5.6.5+

2. **创建数据库**

   找到当前目录下的sql脚本文件夹，用navicat/sqlyong直接运行这两个sql脚本

3. **运行三个jar包**

   运行之前需要编辑这个文件**（数据库的账号密码以及地址进行修改，还可以修改服务的端口号）**

   双击startUp.bat文件

4. **注意事项**

   如果启动第一个configserver报错，报错内容(第一行)

   ```java
   The server time zone value '?й???????' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support
   ```

   说明数据库的时区对应不上

   解决：

   (仅本次有效)

   cmd下进入mysql

   ```dos
   1. mysql - u root -p
   2. 输入密码
   3. set global time_zone=’+8:00’
   ```
在MySQL 5.5中等价于`c1` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';

在MySQL 5.6中等价于`c1` timestamp NULL DEFAULT '0000-00-00 00:00:00';

在MySQL 5.7中等价于`c1` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';

java -Xms256m -Xmx256m -Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloConfigDB?serverTimezone=UTC -Dspring.datasource.username=root -Dspring.datasource.password=root -Dsever.port=8080 -jar apollo-configservice-1.8.0.jar

java -Xms256m -Xmx256m -Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloConfigDB?serverTimezone=UTC -Dspring.datasource.username=root -Dspring.datasource.password=root -Dsever.port=8090 -jar apollo-adminservice-1.8.0.jar

java -Xms256m -Xmx256m -Dapollo_profile=github,auth -Ddev_meta=http://localhost:8080/ -Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloPortalDB?serverTimezone=UTC -Dspring.datasource.username=root -Dspring.datasource.password=root -Dapollo_profile=github,auth -Ddev_meta=http://localhost:8080/ -Dsever.port=8070 -jar apollo-portal-1.8.0.jar

当前项目有环境缺失,请点击页面左侧 补齐环境 补齐数据

java -Xms256m -Xmx256m -Denv=dev -Ddev_meta=http://localhost:8080/ -Dspring.datasource.url=jdbc:mysql://localhost:3306/ApolloPortalDB?serverTimezone=UTC -Dspring.datasource.username=root -Dspring.datasource.password=root -Dsever.port=8070 -jar apollo-portal-1.8.0.jar

 系统出错,请重试或联系系统负责人 ---  configdb 的serverconfig表 的： eureka.service.url 可以要重启 
 都改成localhost， 或者ip 就可以了  eureka.service.url = http://localhost:8080/eureka/  改为 http://127.0.0.1:8080/  http://localhost:8080/ 
 http://192.168.37.114:8080/eureka/
 192.168.37.114

访问  http://localhost:8070  地址，默认登录账号密码为u: apollo p: admin

   apollo服务的启动顺序， configserver ----> adminserver ----> portal 

