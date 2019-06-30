package com.alison.redpackets.service;


import com.alison.redpackets.pojo.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisRedPacketServiceImpl implements RedisRedPacketService {

    private static final String PREFIX = "red_packet_list_";
    // 每次1000条，避免太多
    private static final int TIME_SIZE = 1000;

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Autowired
    private DataSource dataSource = null;

    @Override
    @Async      // 开启新线程运行
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount) {
        System.err.println("开始保存数据");
        Long start = System.currentTimeMillis();
        BoundListOperations ops = redisTemplate.boundListOps(PREFIX + redPacketId);
        Long size = ops.size();
        // 每次取1000，一共取了times 次
        Long times = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
        int count = 0;
        List<UserRedPacket> userRedPacketList = new ArrayList<>(TIME_SIZE);
        for (int i = 0; i < times; i++) {
            List userIdList = null;
            if (i == 0) {
                userIdList = ops.range(i * TIME_SIZE, (i + 1) * TIME_SIZE);
            } else {
                userIdList = ops.range(i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
            }
            userRedPacketList.clear();
            for (int j = 0; j < userIdList.size(); j++) {
                String args = userIdList.get(j).toString();
                String[] arr = args.split("-");
                String userIdStr = arr[0];
                String timeStr = arr[1];
                Long userId = Long.parseLong(userIdStr);
                Long time = Long.parseLong(timeStr);
                //生成抢红包信息
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote("抢红包" + redPacketId);
                userRedPacketList.add(userRedPacket);
            }
            count += executBatch(userRedPacketList);
        }
        redisTemplate.delete(PREFIX + redPacketId);
        Long end = System.currentTimeMillis();
        System.err.println("保存数据耗时" + (end - start) + "毫秒； 共" + count + "条数据被保存");

    }


    /**
     * 使用JDBC批处理redis缓存数据
     *
     * @param userRedPacketList 抢红包列表
     * @return 抢红包插入数量
     */
    private int executBatch(List<UserRedPacket> userRedPacketList) {
        Connection connection = null;
        Statement statement = null;
        int[] count = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            for (UserRedPacket userRedPacket : userRedPacketList) {
                String sql1 = "update t_red_packet set stock = stock - 1 where id = " + userRedPacket.getRedPacketId();
                DateFormat sf = new SimpleDateFormat("yy-MM-dd HH-mm-ss");
                String sql2 = "insert into t_user_red_packet (red_packet_id, user_id, amount, grab_time, note)"
                        + "values (" + userRedPacket.getRedPacketId() + ","
                        + userRedPacket.getUserId() + ","
                        + userRedPacket.getAmount() + ","
                        + userRedPacket.getGrabTime() + ","
                        + userRedPacket.getNote() + ")";
                statement.addBatch(sql1);
                statement.addBatch(sql2);
            }
            count = statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("抢红包批量执行逻辑错误");
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 返回插入的数据记录
        return count.length / 2;
    }
}






