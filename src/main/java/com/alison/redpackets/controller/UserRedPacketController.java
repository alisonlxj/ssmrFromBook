package com.alison.redpackets.controller;


import com.alison.redpackets.service.UserRedPacketService;
import com.alison.redpackets.service.UserRedPacketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/userRP")
public class UserRedPacketController {

    @Autowired
    private UserRedPacketService userRedPacketService = null;

    @RequestMapping("/grabRedPacket")
    @ResponseBody
    public Map<String, Object> grabRedPacket(Long redPacketId, Long userId){
//        System.out.println("第" + userId + "次请求");
        int result = userRedPacketService.grabRedPacket(redPacketId, userId);
        boolean flag = result > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success? ", flag);
        map.put("msg", flag ? "抢红包成功" : "抢红包失败");
        return map;
    }

    @RequestMapping("/grabByRedis")
    @ResponseBody
    public Map<String, Object> grabRedPacketByRedis(Long redPacketId, Long userId){
        Map<String, Object> resultMap = new HashMap<>();
        Long result = userRedPacketService.grabRedPacketByRedis(redPacketId, userId);
        boolean flag = result > 0;
        resultMap.put("result ", flag);
        resultMap.put("msg", flag ? "抢红包成功" : "抢红包失败");
        return resultMap;
    }

    @RequestMapping("/start")
    public String startGrab(){
        return "startGrab";
    }

}
