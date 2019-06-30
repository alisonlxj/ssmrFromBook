package com.alison.redpackets.dao;

import com.alison.redpackets.pojo.RedPacket;
import org.springframework.stereotype.Repository;

/**
 * 相当于mybatis的mapper接口文件
 */
@Repository
public interface RedPackeDao {

    /**
     * 获取红包信息
     * @param id    红包id
     * @return      红包具体信息
     */
    public RedPacket getRedPacket(long id);


    /**
     * 扣减抢红包数
     * @param redPacket    红包duixiang
     * @return      影响条数
     */
    public int decreaceRedPacket(RedPacket redPacket);

}
