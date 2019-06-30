package com.alison.redpackets.service;

import com.alison.redpackets.dao.RedPackeDao;
import com.alison.redpackets.pojo.RedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("redPacketService")
public class RedPacketSericeImpl implements RedPacketService {

    @Autowired
    RedPackeDao redPackeDao = null;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(long id) {
        return redPackeDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int decreaceRedPacket(RedPacket redPacket) {
        return redPackeDao.decreaceRedPacket(redPacket);
    }
}
