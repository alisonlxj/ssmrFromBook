package com.alison.redpackets.dao;

import com.alison.redpackets.pojo.UserRedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedPacketDao {


    /**
     * 插入抢红包信息
     * @param userRedPacket 抢红包信息
     * @return 影响条数
     */
    public int grabRedPacket(UserRedPacket userRedPacket);

}
