package one.entropy.karamel.api;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import io.vavr.control.Try;
import one.entropy.karamel.data.PodInfo;
import one.entropy.karamel.ui.ClientUI;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("karamelAPI")
public class KaramelAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUI.class.getCanonicalName());
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
        return true; //Files.exists(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace"));
    }

    public CompletionStage<List<Deployment>> getClusterDeployments() {
        return CompletableFuture.supplyAsync(() -> getDeployments("app", "strimzi")).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<ReplicaSet>> getClusterReplicaSets() {
        return CompletableFuture.supplyAsync(() -> getReplicaSets(KIND_LABEL, "cluster-operator")).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<PodInfo>> getClusterPods() {
        return CompletableFuture.supplyAsync(() -> getPods(KIND_LABEL, "cluster-operator")).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<Deployment>> getEntityDeployments() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getDeployments(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<ReplicaSet>> getEntityReplicaSets() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getReplicaSets(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<PodInfo>> getEntityPods() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-entity-operator").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<StatefulSet>> getZookeeperStatefulSets() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getStatefulSets(NAME_LABEL, cluster + "-zookeeper").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<PodInfo>> getZookeeperPods() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-zookeeper").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<StatefulSet>> getKafkaStatefulSets() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getStatefulSets(NAME_LABEL, cluster + "-kafka").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<PodInfo>> getKafkaPods() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getPods(NAME_LABEL, cluster + "-kafka").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<Service>> getBootstrapServices() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getServices(NAME_LABEL, cluster + "-kafka-bootstrap").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<Service>> getBrokerServices() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> clusterNames = getClusterNames();
            return clusterNames.stream().flatMap(cluster -> getServices(NAME_LABEL, cluster + "-kafka-brokers").stream()).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    public CompletionStage<List<String>> getBrokers() {
        if (!isKubernetes()) {
            return CompletableFuture.supplyAsync(() -> brokers);
        } else {
            return getBrokerServices().thenApply(services -> services.stream().map(service -> {
                String hostname = service.getMetadata().getName() + "." + service.getMetadata().getNamespace();
                Integer port = Try.of(() -> service.getSpec().getPorts().stream().filter(sport -> Objects.equals(sport.getName(), "tcp-clients")).findFirst().get().getPort()).getOrElse(9092);
                return hostname + ":" + port;
            }).collect(Collectors.toList()));
        }
    }

    private List<Deployment> getDeployments(String label, String value) {
        return kubernetesClient.apps().deployments().list().getItems().stream().filter(deployment ->
                Objects.equals(deployment.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
    }

    private List<ReplicaSet> getReplicaSets(String label, String value) {
        return kubernetesClient.apps().replicaSets().list().getItems().stream().filter(replicaSet ->
                Objects.equals(replicaSet.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
    }

    private List<PodInfo> getPods(String label, String value) {
        List<Pod> pods = kubernetesClient.pods().inAnyNamespace().list().getItems().stream().filter(pod -> Objects.equals(pod.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
        return pods.stream().map(pod -> {
            boolean ready = Try.of(() -> pod.getStatus().getConditions().stream().filter(c -> c.getType().equalsIgnoreCase("Ready")).findFirst().get().getStatus().equalsIgnoreCase("True")).getOrElse(false);
            return PodInfo.builder().phase(pod.getStatus().getPhase()).ready(ready).name(pod.getMetadata().getName()).uid(pod.getMetadata().getUid()).build();
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
                .collect(Collectors.toList());
    }

}
