package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Controller
@RequestMapping("/community/chats")
public class ChatController {

    @Autowired
    private ChatService chatsService;

}


