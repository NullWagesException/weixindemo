package com.zf.weixindemo.controller;

import com.zf.weixindemo.utils.WeChatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: nullWagesException
 * @Date: 2019/9/4 15:06
 * @Description:
 */
@ServerEndpoint(value = "/payWebsocket/{id}")
@Component
public class PayWebSocket {

    /**
     * 订单号
     */
    private String id;

    /**
     * 当前的连接
     */
    private Session session;

    /**
     * 存放订单和session连接关系
     */
    private static Map<String,Session> Clients = new ConcurrentHashMap<>();

    private static Logger log = LoggerFactory.getLogger(PayWebSocket.class);


    /**
     * 建立连接时调用
     * @param id 订单号
     * @param session session
     */
    @OnOpen
    public void onOpen(@PathParam("id") String id, Session session){
        this.id = id;
        this.session = session;
        Clients.put(id, session);
        log.info("建立了连接，订单号为：" + id);
    }

    /**
     * 关闭连接
     * @param session session
     */
    @OnClose
    public void onClose(Session session){
        Clients.remove(id);
        log.info("关闭了连接");
    }

    /**
     * 发生错误时
     * @param session
     */
    @OnError
    public void onError(Session session,Throwable t){
        if (session != null && session.isOpen()){
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Clients.remove(id);

    }

    @OnMessage
    public static void onMessage(Session session,String content){
        if (session!=null && session.isOpen()){
            session.getAsyncRemote().sendText(content);
        }
    }

    public static void sendMessage(String id,String message){
        if (id != null) {
            onMessage(Clients.get(id),message);
        }
    }

    public static Map<String, Session> getClients() {
        return Clients;
    }
}
