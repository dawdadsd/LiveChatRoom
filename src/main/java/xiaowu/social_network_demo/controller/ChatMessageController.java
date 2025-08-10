package xiaowu.social_network_demo.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.service.GroupMessageService;

@RestController
@RequestMapping("/api/chat")
@Data
public class ChatMessageController {
    private final GroupMessageService messageService;
    @PostMapping
    private Result<String> sendGroupMessage(Integer groupId,String content,String UserIp,String sessionId)
    {
        return messageService.sendGroupMessage(groupId,content,UserIp,sessionId);
    }
}
