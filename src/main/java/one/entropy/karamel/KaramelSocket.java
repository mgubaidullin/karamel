package one.entropy.karamel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/events")
@ApplicationScoped
public class KaramelSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelSocket.class.getCanonicalName());

    Session session;

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Open");
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        this.session = null;
        LOGGER.error("onError", throwable);
    }

    protected void broadcast(String message) {
        session.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                LOGGER.error("Unable to send message: ", result.getException());
            }
        });
    }
}
