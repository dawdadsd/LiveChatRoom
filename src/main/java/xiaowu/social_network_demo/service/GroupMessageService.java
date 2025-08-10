package xiaowu.social_network_demo.service;

import xiaowu.social_network_demo.common.Result;

public interface GroupMessageService {
    //1.消息群组发送
    //2.历史查询
    //3.成员权限

    /**
     * 发送群组消息
     * @param groupId 群组ID
     * @param senderIp 发送者IP
     * @param sessionId 发送者会话ID
     * @param content 消息内容
     * @return 发送结果
     */

    public Result<String> sendGroupMessage(Integer groupId,String senderIp,String sessionId,String content);
}
