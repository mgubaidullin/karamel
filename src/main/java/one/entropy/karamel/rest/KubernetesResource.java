package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import one.entropy.karamel.data.*;
import one.entropy.karamel.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
public class KubernetesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResource.class.getCanonicalName());

    private enum TYPE {bootstrap, broker, kafka, cluster, entity, zookeeper, connect}

    @Inject
    KubernetesService kubernetesService;

    @GET
    @Path("/kubernetes")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> isKubernetes() {
        JsonObject result = new JsonObject().put("isKubernetes", kubernetesService.isKubernetes());
        return Uni.createFrom().item(result);
    }

    @GET
    @Path("/statefulset")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<StatefulSetInfo> getStatefulSetInfo(@QueryParam("type") String type) {
        switch (TYPE.valueOf(type)) {
            case kafka:
                return Multi.createFrom().iterable(kubernetesService.getKafkaStatefulSets());
            case zookeeper:
                return Multi.createFrom().iterable(kubernetesService.getZookeeperStatefulSets());
            default:
                return Multi.createFrom().iterable(List.of());
        }
    }

    @GET
    @Path("/pod")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<PodInfo> getPodInfo(@QueryParam("type") String type) {
        LOGGER.info(type);
        LOGGER.info("{}", TYPE.valueOf(type));
        switch (TYPE.valueOf(type)) {
            case kafka:
                return Multi.createFrom().iterable(kubernetesService.getKafkaPods());
            case cluster:
                return Multi.createFrom().iterable(kubernetesService.getClusterPods());
            case entity:
                return Multi.createFrom().iterable(kubernetesService.getEntityPods());
            case zookeeper:
                return Multi.createFrom().iterable(kubernetesService.getZookeeperPods());
            case connect:
                return Multi.createFrom().iterable(kubernetesService.getConnectPods());
            default:
                return Multi.createFrom().iterable(List.of());
        }
    }

    @GET
    @Path("/service")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<ServiceInfo> getServiceInfo(@QueryParam("type") String type) {
        switch (TYPE.valueOf(type)) {
            case bootstrap:
                return Multi.createFrom().iterable(kubernetesService.getBootstrapServices());
            case broker:
                return Multi.createFrom().iterable(kubernetesService.getBrokerServices());
            case connect:
                return Multi.createFrom().iterable(kubernetesService.getConnectServices());
            default:
                return Multi.createFrom().iterable(List.of());
        }
    }

    @GET
    @Path("/deployment")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<DeploymentInfo> getDeploymentInfo(@QueryParam("type") String type) {
        switch (TYPE.valueOf(type)) {
            case cluster:
                return Multi.createFrom().iterable(kubernetesService.getClusterDeployments());
            case connect:
                return Multi.createFrom().iterable(kubernetesService.getConnectDeployments());
            case entity:
                return Multi.createFrom().iterable(kubernetesService.getEntityDeployments());
            default:
                return Multi.createFrom().iterable(List.of());
        }
    }

    @GET
    @Path("/replicaset")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<ReplicaSetInfo> getReplicaSetInfo(@QueryParam("type") String type) {
        switch (TYPE.valueOf(type)) {
            case cluster:
                return Multi.createFrom().iterable(kubernetesService.getClusterReplicaSets());
            case connect:
                return Multi.createFrom().iterable(kubernetesService.getConnectReplicaSets());
            case entity:
                return Multi.createFrom().iterable(kubernetesService.getEntityReplicaSets());
            default:
                return Multi.createFrom().iterable(List.of());
        }
    }
}
