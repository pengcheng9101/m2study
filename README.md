1. 项目结构层之间的关系
pojo 简单的数据库对象
vo 对pojo 封装 返回给 controller

2. mybatis-generator 插件
根据数据ku自动生成pojo ,dao 和 mybatis 的xml 文件
- 安装
将插件配置到pom 文件中
在resources 创建 generatorConfig.xml,设置相关配置制定jdbc链接地址,数据库登录账号密码,还有<table/>标签,这个标签的tableName属性必须跟数据库的表明一致.
- 工作流程
先创建表.然后在generatorConfig.xml 声明table标签指定表的名字.然后在idea 右边 的maven projects ,点击 plusgins 目录下的 mybatis-generator 生成代码
