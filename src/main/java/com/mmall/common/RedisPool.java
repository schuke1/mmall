package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author schuke
 * @date 2019/4/8 14:20
 */
public class RedisPool {
    //jedis连接池
    private static JedisPool pool;

    //最大连接数
    private static Integer maxTotal =
            Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));

    //在jedispool中最大的idle状态（空闲）的jedis实例的个数
    private static Integer maxIdle =
            Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));

    //在jedispool中最小的idle状态（空闲）的jedis实例的个数
    private static Integer minIdle =
            Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));

    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例肯定是可以用的
    private static Boolean testOnBorrow =
            Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));

    ////在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则放回的jedis实例肯定是可以用的
    private static Boolean testOnReturn =
            Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));

    //Redis端口号
    private static Integer redisPort =
            Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    //Redis-Ip
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");


    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);


        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞知道超时，默认为true
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }


    static {
        initPool();
    }


    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("schuke", "shuckea");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");
    }

}
