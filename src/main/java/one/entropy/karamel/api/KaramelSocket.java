package one.entropy.karamel.api;

import io.quarkus.vertx.ConsumeEvent;
import io.vavr.control.Try;
import io.vertx.reactivex.core.eventbus.Message;
import one.entropy.karamel.data.KEventIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/events/{sessionId}")
@ApplicationScoped
public class KaramelSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelSocket.class.getCanonicalName());
    public static final String ADDRESS = "KARAMEL_SOCKET";

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        LOGGER.info("{}, {}", sessionId, session);
        sessions.put(sessionId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String sessionId) {
        sessions.remove(sessionId);
    }

    @OnError
    public void onError(Session session, @PathParam("sessionId") String sessionId, Throwable throwable) {
        sessions.remove(sessionId);
    }

    @ConsumeEvent(value = ADDRESS)
    public void consume(Message<KEventIn> message) {
        broadcast(message.body().getSessionId(), message.body().toString());
    }

    public void broadcast(String sessionId, String message) {
        Try.run(() -> sessions.get(sessionId).getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
                LOGGER.error("", result.getException());
            }
        })).onFailure(throwable -> LOGGER.error("", throwable));
    }
}
