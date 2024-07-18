package ru.olbreslavets.tgbank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.olbreslavets.tgbank.constant.Commands;

import java.io.Serializable;

@RedisHash("Context")
@NoArgsConstructor
@Getter
@Setter
public class Context implements Serializable {

    @Id
    private String chatId;
    private Commands command;
    private String stage;
    private String messageIn;
    private String messageOut;

}
