package com.tjut.zjone.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tjut.zjone.common.convention.exception.ServiceException;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.mq.idempotent.MessageQueueIdempotentHandler;
import com.tjut.zjone.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class StudentPutInfoConsumer implements MessageListener {

    @Resource
    private UserService userService;

    @Resource
    MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    @Override
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "student.que"),
            exchange = @Exchange(name = "student.fanout", type = ExchangeTypes.FANOUT)))
    public void onMessage(Message message) {
        String id = message.getMessageProperties().getMessageId();
        // 判断消息是否被处理过
        if (messageQueueIdempotentHandler.isMessageProcessed(id)){
            // 判断当前的这个消息流程是否执行完成，如果完成，则直接返回，不做处理
            if (messageQueueIdempotentHandler.isAccomplish(id)) {
                return;
            }
            // 如果消息未完成流程，抛出 ServiceException，通知消息队列进行重试
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }
        try {
            byte[] body = message.getBody();
            try {
                // 将字节数组反序列化为 UserDO 对象
                UserDO userDO = JSONUtil.toBean(new String(body), UserDO.class);
                AsyncSave(userDO);
            } catch (Exception e) {
                // 处理反序列化异常
                e.printStackTrace();
            }
        } catch (Throwable ex) {
            // 捕捉异常，处理异常情况
            // 记录未完成流程的消息，用于消息队列重试
            messageQueueIdempotentHandler.delMessageProcessed(id);
            log.error("记录短链接监控消费异常", ex);
        }
        // 设置消息为已完成流程，防止重复处理
        messageQueueIdempotentHandler.setAccomplish(id);
    }

    public void AsyncSave(UserDO userDO)
    {
        log.info("接收到存储学生信息的消息,{}", JSONUtil.toJsonStr(userDO));
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                            .eq(UserDO::getUsername, userDO.getUsername());
        boolean success = false;
        try {
            success = userService.update(userDO, queryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("学生信息存储完成?{}",success);
    }


}

