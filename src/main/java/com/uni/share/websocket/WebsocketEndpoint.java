package com.uni.share.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//TODO FR comments
@ServerEndpoint("/ws/{userId}")
public class WebsocketEndpoint {

    //IMPORTANT: please provide a short description of the content that is associated with the key
    public static final String HEARTBEAT = "HEARTBEAT";
    public static final String TRANSACTION_PENDING = "TRANSACTION_PENDING"; //Content: String containing receiver address with checksum
    public static final String TRANSACTION_CONFIRMED = "TRANSACTION_CONFIRMED"; //Content: AddressTO with index balance and address(with checksum)
    public static final String ORDER_COMPLETE = "ORDER_COMPLETE"; //Content: Long orderId
    public static final String RECEIVED_PENDING_PAYMENT = "RECEIVED_PENDING_PAYMENT"; //Content: ReceivedPendingPaymentTO
    public static final String UPDATE_AVAILABLE_BALANCE = "UPDATE_AVAILABLE_BALANCE"; //Content: ReceivedPendingPaymentTO
    public static final String UPDATE_SERVER_STATUS = "UPDATE_SERVER_STATUS"; //Content: String ServerStatus 'up' or 'down'
    public static final String GROUP_CHANGED = "GROUP_CHANGED"; //Content: String groupTitle
    public static final String NEW_GROUP_INVITATION = "NEW_GROUP_INVITATION"; //Content: String groupTitle


    //private static Set<Session> userSessions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());
    private static ObjectMapper objMapper = new ObjectMapper();

    //Key:String = session.getId | Value:Pair of userId:Long and session:Session
    private static HashMap<String, Pair<Long, Session>> sessions = new HashMap<>();


    @OnOpen
    public void onOpen(@PathParam("userId") long userId, Session session) {
        sessions.put(session.getId(), Pair.of(userId, session));
        System.out.println("Added sessiont for userId: " + userId + " --> " + session.getId());
    }


    @OnClose
    public void onClose(Session session) {
        System.out.println(
                "Removed session for userId" + sessions.get(session.getId()).getKey() + " --> " + session.getId());
        sessions.remove(session.getId());

    }


    public static void sendMessage(String key, Object content, boolean broadcast, Long... userIds) {
        String userIdString = "";
        for(Long userid:userIds){
            userIdString += userid+", ";
        }

        if(key != HEARTBEAT){
            System.out.println("WS message ["+broadcast+"]: "+key+" | "+content+" | "+userIdString);
        }

        List<String> sessionIdsToSendTo = new ArrayList<>();
        if (broadcast) {
            sessions.forEach((entryKey, entryValue) -> sessionIdsToSendTo.add(entryKey));
        } else {
            for (Long userId : userIds) {
                //userId of 0 signals no user
                if (userId > 0) {
                    sessions.forEach((entryKey, entryValue) -> {
                        if (entryValue.getKey().equals(userId)) {
                            sessionIdsToSendTo.add(entryKey);
                        }
                    });
                }
            }
        }

        final OutgoingWebSocketMessage outgoingWebSocketMessage = new OutgoingWebSocketMessage(key, content);

        for (String sessionId : sessionIdsToSendTo) {
            try {
                sessions.get(sessionId).getValue().getAsyncRemote().sendText(
                        objMapper.writeValueAsString(outgoingWebSocketMessage));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
    }

}


