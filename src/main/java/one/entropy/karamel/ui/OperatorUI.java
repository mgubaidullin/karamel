package one.entropy.karamel.ui;

import javax.ws.rs.Path;

@Path("/")
public class OperatorUI {

//    @Inject
//    KaramelAPI karamelAPI;
//
//    @Inject
//    Template operators;
//
//    @GET
//    @Consumes(MediaType.TEXT_HTML)
//    @Produces(MediaType.TEXT_HTML)
//    @Path("operators")
//    public TemplateInstance operators() {
//        Collection<DeploymentInfo> clusterDeployments = karamelAPI.getClusterDeployments();
//        Collection<DeploymentInfo> entityDeployments = karamelAPI.getEntityDeployments();
//        Collection<DeploymentInfo> connectDeployments = karamelAPI.getConnectDeployments();
//
//        Collection<ReplicaSetInfo> clusterReplicaSets = karamelAPI.getClusterReplicaSets();
//        Collection<ReplicaSetInfo> entityReplicaSets = karamelAPI.getEntityReplicaSets();
//        Collection<ReplicaSetInfo> connectReplicaSets = karamelAPI.getConnectReplicaSets();
//
//        Collection<PodInfo> clusterPods = karamelAPI.getClusterPods();
//        Collection<PodInfo> entityPods = karamelAPI.getEntityPods();
//        Collection<PodInfo> connectPods = karamelAPI.getConnectPods();
//
//        Collection<ServiceInfo> connectServices = karamelAPI.getConnectServices();
//        return operators
//                .data("clusterDeployments", clusterDeployments)
//                .data("entityDeployments", entityDeployments)
//                .data("connectDeployments", connectDeployments)
//                .data("clusterReplicaSets", clusterReplicaSets)
//                .data("entityReplicaSets", entityReplicaSets)
//                .data("connectReplicaSets", connectReplicaSets)
//                .data("clusterPods", clusterPods)
//                .data("entityPods", entityPods)
//                .data("connectPods", connectPods)
//                .data("connectServices", connectServices)
//                .data("page", "operators");
//    }
}
