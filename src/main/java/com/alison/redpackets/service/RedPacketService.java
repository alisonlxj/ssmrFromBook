package com.alison.redpackets.service;

import com.alison.redpackets.pojo.RedPacket;

public interface RedPacketService {

    /**
     * 获取红包信息
     * @param id    红包id
     * @return      红包具体信息
     */
    public RedPacket getRedPacket(long id);


    /**
     * 扣减抢红包数
     * @param redPacket    红包id
     * @return      影响条数
     */
    public int decreaceRedPacket(RedPacket redPacket);

}
