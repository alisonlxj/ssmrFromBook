package com.alison.redpackets.service;

import com.alison.redpackets.dao.RedPackeDao;
import com.alison.redpackets.dao.UserRedPacketDao;
import com.alison.redpackets.pojo.RedPacket;
import com.alison.redpackets.pojo.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;

@Service("userRedPacketService")
public class UserRedPacketServiceImpl implements UserRedPacketService {

    @Autowired
    UserRedPacketDao userRedPacketDao = null;

    @Autowired
    RedPackeDao redPackeDao = null;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisRedPacketService redisRedPacketService;

    private static final int FAILED = 0;

    // lua脚本
    String luaScript = "local listKey = 'red_packet_list_'..KEYS[1]\n"
            + "local redPacket = 'red_packet_'..KEYS[1]\n"
            + "local stock = tonumber(redis.call('hget', redPacket, 'stock'))\n"
            + "if stock <= 0 then return 0 end\n"
            + "stock = stock - 1\n"
            + "redis.call('hset', redPacket, 'stock', tostring(stock))\n"
            + "redis.call('rpush', listKey, ARGV(1))\n" +
            "if stock == 0 then return 2 end\n" +
            "return 1";
    // 缓存lua脚本后，保存返回的32位SHA1编码，使用它执行缓存的lua脚本
    String sha1 = null;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacket(Long redPacketId, Long userId) {
        RedPacket redPacket = redPackeDao.getRedPacket(redPacketId);
        if (redPacket == null) {
            return FAILED;
        }
        if (redPacket.getStock() > 0) {
            int update = redPackeDao.decreaceRedPacket(redPacket);
            if (update == 0) {
                System.out.println("用户" + userId + "请求失败");
                return FAILED;
            }
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setUserId(userId);
            userRedPacket.setNote("抢红包 " + redPacketId);
            int result = userRedPacketDao.grabRedPacket(userRedPacket);
            return result;
        }

        return FAILED;
    }

    @Override
    public Long grabRedPacketByRedis(Long redPacketId, Long userId) {
        String args = userId + "-" + System.currentTimeMillis();
        Long result = null;
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        try {
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(luaScript);
            }
            // 执行脚本，返回结果
            Object res = jedis.evalsha(sha1, 1, redPacketId+"", args);
            result = (Long)res;
            // 返回2时，是最后一个红包，此时将抢红包信息通过异步保存到数据库中
            if(result == 2){
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.err.println("thread_name = " + Thread.currentThread().getName() + "");
                // 这个方法会启动一个新线程执行
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId,unitAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null && jedis.isConnected()) {
                jedis.close();
            }
        }

        return result;
    }

}
