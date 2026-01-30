package com.teamtodo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TeamTodo Backend Application
 * Spring Boot 3.x with MyBatis Plus
 */
@SpringBootApplication
@MapperScan("com.teamtodo.mapper")
public class TeamTodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamTodoApplication.class, args);
    }
}
