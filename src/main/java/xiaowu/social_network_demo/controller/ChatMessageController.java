package xiaowu.social_network_demo.controller;

import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.service.ChatMessageService;
import xiaowu.social_network_demo.service.GroupMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Data
public class ChatMessageController {
    private final GroupMessageService messageService;

    @Resource
    private  final ChatMessageService chatMessageService;


    @PostMapping
    private Result<String> sendGroupMessage(Integer groupId,String content,String UserIp,String sessionId)
    {
        return messageService.sendGroupMessage(groupId,content,UserIp,sessionId);
    }

    public Result<List<ChatMessage>> getConversation(Integer groupId) {
        return Result.success("获取对话记录成功",chatMessageService.getGroupMessages(groupId) );
    }
}
