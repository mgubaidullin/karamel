package one.entropy.karamel.ui;

import javax.ws.rs.Path;

@Path("/")
public class KafkaUI {

//    @Inject
//    KafkaAPI kafkaAPI;
//
//    @Inject
//    KaramelAPI karamelAPI;
//
//    @Inject
//    Template kafka;
//
//    @GET
//    @Consumes(MediaType.TEXT_HTML)
//    @Produces(MediaType.TEXT_HTML)
//    @Path("kafka")
//    public TemplateInstance kafka() {
//        List<String> list = karamelAPI.getBrokers();
//        Collection<NodeInfo> nodeList = kafkaAPI.getNodes(list.get(0));
//
//        boolean isKubernetes = karamelAPI.isKubernetes();
//        if (isKubernetes) {
//            Collection<StatefulSetInfo> statefulSets = karamelAPI.getKafkaStatefulSets();
//            Collection<PodInfo> pods = karamelAPI.getKafkaPods();
//            Collection<ServiceInfo> bootstrapServices = karamelAPI.getBootstrapServices();
//            Collection<ServiceInfo> brokerServices = karamelAPI.getBrokerServices();
//            return kafka
//                    .data("statefulSets", statefulSets)
//                    .data("bootstrapServices", bootstrapServices)
//                    .data("brokerServices", brokerServices)
//                    .data("pods", pods)
//                    .data("isKubernetes", isKubernetes)
//                    .data("nodes", nodeList)
//                    .data("page", "kafka");
//        }
//        return kafka
//                .data("isKubernetes", isKubernetes)
//                .data("nodes", nodeList)
//                .data("page", "kafka");
//    }
//
//    @Inject
//    Template zookeeper;
//
//    @GET
//    @Consumes(MediaType.TEXT_HTML)
//    @Produces(MediaType.TEXT_HTML)
//    @Path("zookeeper")
//    public TemplateInstance zookeeper() {
//        Collection<StatefulSetInfo> statefulSets = karamelAPI.getZookeeperStatefulSets();
//        Collection<PodInfo> pods = karamelAPI.getZookeeperPods();
//        return zookeeper
//                .data("statefulSets", statefulSets)
//                .data("pods", pods)
//                .data("page", "zookeeper");
//    }
}
