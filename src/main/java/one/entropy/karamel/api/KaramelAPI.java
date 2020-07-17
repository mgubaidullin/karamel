package one.entropy.karamel.api;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import io.vavr.control.Try;
import one.entropy.karamel.data.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("karamelAPI")
public class KaramelAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelAPI.class.getCanonicalName());
    private static final String CLUSTER_LABEL = "strimzi.io/cluster";
    private static final String NAME_LABEL = "strimzi.io/name";
    private static final String KIND_LABEL = "strimzi.io/kind";

    @ConfigProperty(name = "karamel.brokers", defaultValue = "")
    List<String> brokers;

    private final KubernetesClient kubernetesClient;

    public KaramelAPI(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    void onStart(@Observes StartupEvent ev) {
        if (isKubernetes()) {
            LOGGER.info("Karamel kubernetes is starting...");
        } else {
            LOGGER.info("Karamel standalone is starting...");
        }
    }

    public boolean isKubernetes() {
        return Files.exists(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace"));
    }

    public List<DeploymentInfo> getClusterDeployments() {
        return getDeployments("app", "strimzi");
    }

    public List<ReplicaSetInfo> getClusterReplicaSets() {
        return getReplicaSets(KIND_LABEL, "cluster-operator");
    }

    public List<PodInfo> getClusterPods() {
        return getPods(KIND_LABEL, "cluster-operator");
    }

    public List<DeploymentInfo> getEntityDeployments() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getDeployments(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
    }

    public List<ReplicaSetInfo> getEntityReplicaSets() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getReplicaSets(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
    }

    public List<PodInfo> getEntityPods() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
    }

    public List<DeploymentInfo> getConnectDeployments() {
        List<String> clusterNames = getClusterNames();
        return clusterNames.stream().flatMap(cluster -> getDeployments(KIND_LABEL, "KafkaConnect").stream()).collect(Collectors.toList());
    }

    public List<ReplicaSetInfo> getConnectReplicaSets() {
        List<String> clusterNames = getClusterNames();
        return clusterNames.stream().flatMap(cluster -> getReplicaSets(KIND_LABEL, "KafkaConnect").stream()).collect(Collectors.toList());
    }

    public List<PodInfo> getConnectPods() {
        List<String> clusterNames = getClusterNames();
        return clusterNames.stream().flatMap(cluster -> getPods(KIND_LABEL, "KafkaConnect").stream()).collect(Collectors.toList());
    }

    public List<ServiceInfo> getConnectServices() {
        List<String> clusterNames = getClusterNames();
        return clusterNames.stream().flatMap(cluster -> getServices(KIND_LABEL, "KafkaConnect").stream())
                .map(s -> new ServiceInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getSpec().getType(), s.getMetadata().getNamespace()))
                .collect(Collectors.toList());
    }

    public List<StatefulSetInfo> getZookeeperStatefulSets() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getStatefulSets(NAME_LABEL, cluster + "-zookeeper").stream())
                    .map(s -> new StatefulSetInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getStatus().getReplicas(), s.getStatus().getReadyReplicas()))
                    .collect(Collectors.toList());
    }

    public List<PodInfo> getZookeeperPods() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-zookeeper").stream()).collect(Collectors.toList());
    }

    public List<StatefulSetInfo> getKafkaStatefulSets() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getStatefulSets(NAME_LABEL, cluster + "-kafka").stream())
                    .map(s -> new StatefulSetInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getStatus().getReplicas(), s.getStatus().getReadyReplicas()))
                    .collect(Collectors.toList());
    }

    public List<PodInfo> getKafkaPods() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-kafka").stream()).collect(Collectors.toList());
    }

    public List<ServiceInfo> getBootstrapServices() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getServices(NAME_LABEL, cluster + "-kafka-bootstrap").stream())
                    .map(s -> new ServiceInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getSpec().getType(), s.getMetadata().getNamespace()))
                    .collect(Collectors.toList());
    }

    public List<ServiceInfo> getBrokerServices() {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getServices(NAME_LABEL, cluster + "-kafka-brokers").stream())
                    .map(s -> new ServiceInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getSpec().getType(), s.getMetadata().getNamespace()))
                    .collect(Collectors.toList());
    }

    public List<String> getBrokers() {
        if (!isKubernetes()) {
            return brokers;
        } else {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getServices(NAME_LABEL, cluster + "-kafka-brokers").stream()).map(service -> {
                String hostname = service.getMetadata().getName() + "." + service.getMetadata().getNamespace();
                Integer port = Try.of(() -> service.getSpec().getPorts().stream().filter(sport -> Objects.equals(sport.getName(), "tcp-clients")).findFirst().get().getPort()).getOrElse(9092);
                return hostname + ":" + port;
            }).collect(Collectors.toList());
        }
    }

    private List<DeploymentInfo> getDeployments(String label, String value) {
        return kubernetesClient.apps().deployments().list().getItems().stream().filter(deployment ->
                Objects.equals(deployment.getMetadata().getLabels().get(label), value))
                .map(s -> new DeploymentInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getStatus().getReplicas(), s.getStatus().getReadyReplicas()))
                .collect(Collectors.toList());
    }

    private List<ReplicaSetInfo> getReplicaSets(String label, String value) {
        return kubernetesClient.apps().replicaSets().list().getItems().stream().filter(replicaSet ->
                Objects.equals(replicaSet.getMetadata().getLabels().get(label), value))
                .map(s -> new ReplicaSetInfo(s.getKind(), s.getMetadata().getUid(), s.getMetadata().getName(), s.getStatus().getReplicas(), s.getStatus().getReadyReplicas()))
                .collect(Collectors.toList());
    }

    private List<PodInfo> getPods(String label, String value) {
        List<Pod> pods = kubernetesClient.pods().inAnyNamespace().list().getItems().stream().filter(pod -> Objects.equals(pod.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
        return pods.stream().map(pod -> {
            boolean ready = Try.of(() -> pod.getStatus().getConditions().stream().filter(c -> c.getType().equalsIgnoreCase("Ready")).findFirst().get().getStatus().equalsIgnoreCase("True")).getOrElse(false);
            return new PodInfo(pod.getMetadata().getUid(), pod.getMetadata().getName(), pod.getStatus().getPhase(), ready);
        }).collect(Collectors.toList());
    }

    private List<StatefulSet> getStatefulSets(String label, String value) {
        return kubernetesClient.apps().statefulSets().inAnyNamespace().list().getItems().stream().filter(statefulSet ->
                Objects.equals(statefulSet.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
    }

    private List<Service> getServices(String label, String value) {
        return kubernetesClient.services().inAnyNamespace().list().getItems().stream().filter(service ->
                Objects.equals(service.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
    }

    private List<String> getClusterNames() {
        return kubernetesClient.apps().statefulSets().inAnyNamespace().list().getItems().stream()
                .filter(statefulSet -> statefulSet.getMetadata().getLabels().containsKey(CLUSTER_LABEL))
                .map(statefulSet -> statefulSet.getMetadata().getLabels().get(CLUSTER_LABEL))
                .distinct()
                .collect(Collectors.toList());
    }

}
