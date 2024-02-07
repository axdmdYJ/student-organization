package com.tjut.zjone.common.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjut.zjone.common.biz.user.UserContext;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AsyncSaveVoucherListener {

    @Resource
    private UserService voucherOrderService;

    @RabbitListener(queuesToDeclare = {@Queue(name= "student.que")})
    public void AsyncSave(UserDO userDO)
    {
        log.info("接收到存储订单信息的消息,{}", JSONUtil.toJsonStr(userDO));
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                            .eq(UserDO::getUsername, userDO.getUsername());
        boolean success = voucherOrderService.update(userDO, queryWrapper);
        log.info("订单信息存储完成?{}",success);
    }

}

