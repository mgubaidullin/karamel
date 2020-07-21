package one.entropy.karamel.rest;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class QuteResource {

    @Inject
    Template kafka;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("kafka")
    public TemplateInstance kafka() {
        return kafka.data("page", "kafka");
    }

    @Inject
    Template client;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("client")
    public TemplateInstance client() {
        return client.data("page", "client");
    }

    @Inject
    Template topics;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics")
    public TemplateInstance topics() {
        return topics.data("page", "topics");
    }

    @Inject
    Template monitor;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("monitor")
    public TemplateInstance monitor() {
        return monitor.data("page", "monitor");
    }
}
