package one.entropy.karamel.rest;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class QuteResource {

    @Inject
    Template kafka;
    @Inject
    Template client;
    @Inject
    Template topics;
    @Inject
    Template monitor;
    @Inject
    Template zookeeper;
    @Inject
    Template operators;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("{page}")
    public TemplateInstance kafka(@PathParam("page") String page) {
        switch (page) {
            case "client":
                return client.data("page", page);
            case "topics":
                return topics.data("page", page);
            case "monitor":
                return monitor.data("page", page);
            case "zookeeper":
                return zookeeper.data("page", page);
            case "operators":
                return operators.data("page", page);
            default:
                return kafka.data("page", page);
        }
    }


}
