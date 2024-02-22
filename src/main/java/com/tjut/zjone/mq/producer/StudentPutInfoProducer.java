package com.tjut.zjone.mq.producer;


import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tjut.zjone.dao.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentPutInfoProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(UserDO userDO) {
        // 将 UserDO 序列化为 JSON
        String jsonString = JSON.toJSONString(userDO);

        // 创建一个带有序列化后 UserDO 的 Message 对象
        Message message = MessageBuilder
                .withBody(jsonString.getBytes())
                .build();

        // 将消息发送到 "student.que" 队列
        rabbitTemplate.convertAndSend("student.que", message);
    }
}
