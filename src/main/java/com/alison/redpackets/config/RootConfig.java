package com.alison.redpackets.config;


import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * @description SpringIoC上下文配置
 */

@Configuration
@ComponentScan(value = "com.*")
@EnableTransactionManagement
public class RootConfig implements TransactionManagementConfigurer {

    private DataSource dataSource = null;

    @Bean(name = "dataSource")
    public DataSource initDataSource() {
        if(dataSource == null) {
            Properties properties = new Properties();
            properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
            properties.setProperty("url", "jdbc:mysql://localhost:3306/ssm?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
            properties.setProperty("username", "root");
            properties.setProperty("password", "mysql2087");
            properties.setProperty("maxWait", "60000");
            // 使用BasicDataSourceFactory.createDataSource 来生成dataSource
            try {
                dataSource = BasicDataSourceFactory.createDataSource(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean getSqlSessionFactory(){
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(initDataSource());
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean(name = "mapperScannerConfigurer")
    public MapperScannerConfigurer getMapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setAnnotationClass(Repository.class);
        mapperScannerConfigurer.setBasePackage("com.*");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }

    /**
      * 配置redis，使用RedisTemplate的spring的内置类，需要spring-data-redis的jar包，还需要Jedis的jar包
     *  redisTemplate的两点配置：
     *      1、JedisConnectionFactory类，即Jedis的连接工厂，这个工厂类有需要JedisPoolConfig类进行具体的连接池配置
     *      2、设定各种序列化器，至少有setDefaultSerializer，一般还要指定key和value的（具体的序列化器通过RedisSerializer来实现）
      * @return
     */
    @Bean("redisTemplate")
    public RedisTemplate initRedisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(50);             // 最大空闲数
        jedisPoolConfig.setMaxTotal(100);           //最大连接数
        jedisPoolConfig.setMaxWaitMillis(20000);    //最大等待时长ms
        // Jedis连接工厂
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.afterPropertiesSet();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setDefaultSerializer(stringSerializer);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        return redisTemplate;
    }


    /**
     * 实现接口方法，注册注解事务，当@Transactional使用时产生注解事务
     * @return 返回 注解驱动事务管理器
     */
    @Override
    @Bean(name = "annotationDrivenTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(initDataSource());
        return dataSourceTransactionManager;
    }
}
