## Mybatis Generator Plugin
### Getting Started
* * *
#### add Mybatis Generator Plugin
In your build.gradle, add following
```groovy
plugins {
    id 'io.github.neallon.mybatis-generator' version '1.0'
}

// dependencies missing default dependencies org.mybatis.generator:mybatis-generator-core:1.4.2 & com.mysql:mysql-connector-j:8.0.32 &tk.mybatis:mapper-generator:4.2.2
// You can use mybatisGenerator to re-specify other versions
dependencies {
    mybatisGenerator 'org.mybatis.generator:mybatis-generator-core:xxx'
}

configurations {
    mybatisGenerator
}

mybatisGenerator {
    // missing default true
    overwrite  = true
    // missing default false
    verbose = false
    // missing default src/main/resources/generator/generatorConfig.xml
    configFile = 'src/main/resources/generator/generatorConfig.xml'
    // missing default src/main/resources/generator/config.properties
    propertyPath = 'src/main/resources/generator/config.properties'
}

```
#### plugin action
1. **gradle generateMybatisConfigFile**  
This task will generate two configuration files (config.properties & generatorConfig.xml) under the specified main resource directory     
   src/main/resources/generator/config.properties, as follows
    ```properties
    #generate by generator plugin
    #Thu Apr 06 02:03:17 CST 2023
    driver-class-name=com.mysql.cj.jdbc.Driver
    url=jdbc\:mysql\://localhost\:3306/demo?useUnicode\=true&characterEncoding\=utf8&useSSL\=false&allowPublicKeyRetrieval\=true&serverTimezone\=GMT%2b8
    username=root
    password=root
    java-client-target-package=com.zh.springboot3nativedemo.mapper
    java-model-target-package=com.zh.springboot3nativedemo.model
    sql-map-package=mapper
    ```
   + The database information uses the **data source** specified in **application.yml**/**application.properties** by **default**
   + **java-client-target-package**/**java-model-target-package**/**sql-map-package** needs to be reset  
 
   src/main/resources/generator/generatorConfig.xml, as follows
    ```xml 
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE generatorConfiguration
            PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
            "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
    <generatorConfiguration>
    <context id="Mysql" targetRuntime="MyBatis3">

        <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
            <property name="mappers" value="com.example.springboot3nativedemo.core.MyMapper"/>
        </plugin>

        <commentGenerator>
            <property name="suppressDate" value="true"></property>
            <property name="javaFileEncoding" value="utf-8"/>
        </commentGenerator>

        <!-- jdbc 连接信息  -->
        <jdbcConnection driverClass="${driver-class-name}"
                        connectionURL="${url}"
                        userId="${username}"
                        password="${password}">
        </jdbcConnection>

        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>

        <!-- 实体类所在包名 -->
        <javaModelGenerator targetPackage="${java-model-target-package}" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"></property>
            <property name="trimStrings" value="true"></property>
        </javaModelGenerator>

        <sqlMapGenerator targetPackage="${sql-map-package}"
                         targetProject="src/main/resources">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>

        <!-- mapper 所在包名 -->
        <javaClientGenerator targetPackage="${java-client-target-package}" targetProject="src/main/java" type="XMLMAPPER">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>

        <table tableName="t_user"
               enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false"
               enableSelectByExample="false" selectByExampleQueryId="false"
        >
            <!--mysql 配置-->
            <generatedKey column="id" sqlStatement="Mysql" identity="true"/>
        </table>
    </context>
    </generatorConfiguration>
    ```
   
2. **gradle generateMybatis**  
   This task will generate mybatis source code

