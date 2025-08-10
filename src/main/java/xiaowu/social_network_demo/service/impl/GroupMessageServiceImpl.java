package xiaowu.social_network_demo.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.mapper.GroupMapper;
import xiaowu.social_network_demo.mdoel.ChatMessage;
import xiaowu.social_network_demo.mdoel.Group;
import xiaowu.social_network_demo.service.ChatMessageService;
import xiaowu.social_network_demo.service.ConnectionManager;
import xiaowu.social_network_demo.service.GroupMessageService;
import xiaowu.social_network_demo.service.MessageRouter;

import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    private final GroupMapper groupMapper;
    private final MessageRouter messageRouter;
    private final ConnectionManager connectionManager;
    private final ChatMessageService chatMessageService;
    @Override
    public Result<String> sendGroupMessage(Integer groupId, String senderIp, String sessionId, String content) {
        try {
            Group group = groupMapper.selectById(groupId);
            if(group == null)
            {
                return Result.error("群组不存在");
            }
            boolean isMember = groupMapper.isMemberInGroup(groupId,senderIp);
            if(!isMember)
            {
                return Result.error("您不是群组成员，无法发送消息");
            }
            ChatMessage groupMessage = new ChatMessage();
            groupMessage.setMessageId(UUID.randomUUID().toString());
            groupMessage.setFromIp(senderIp);
            groupMessage.setGroupId(groupId);
            groupMessage.setSessionId(sessionId);
            groupMessage.setMessageType(ChatMessage.MessageType.GROUP);
            groupMessage.setContent(content);
            groupMessage.setTimestamp(System.currentTimeMillis());
            // 4. 保存消息到数据库
            chatMessageService.saveMessage(groupMessage);
            //广播消息
            broadcastGroupMessage(groupMessage,group);
            return Result.success("群组消息发送成功");
        }catch (Exception e)
        {
            return Result.error("群组消息发送失败 " + e.getMessage());
        }
    }
    public void broadcastGroupMessage(ChatMessage message,Group group){
        try {
            List<String> memberIps = groupMapper.getGroupMemberIps(message.getGroupId());
            String jsonMessage = messageRouter.serializeMessage(message);
            for(String memberIp : memberIps)
            {
                WebSocketSession session = connectionManager.getSession(memberIp);
                try{
                    session.sendMessage(new TextMessage(jsonMessage));
                }catch (Exception e) {
                    log.error("向群组发送消息失败 :{}", e.getMessage());
                }
            }
        }catch (Exception e)
        {
            log.error("群组消息广播失败{}", e.getMessage());
        }
    }
}
