package one.entropy.karamel.api;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.vavr.control.Try;
import one.entropy.karamel.data.PodInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("strimziAPI")
public class StrimziAPI {

    private final KubernetesClient kubernetesClient;

    public StrimziAPI(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public CompletionStage<List<Deployment>> getClusterDeployments() {
        return getDeployments("app", "strimzi");
    }

    public CompletionStage<List<ReplicaSet>> getClusterReplicaSets() {
        return getReplicaSets("strimzi.io/kind", "cluster-operator");
    }

    public CompletionStage<List<PodInfo>> getClusterPods() {
        return getPods("strimzi.io/kind", "cluster-operator");
    }

    public CompletionStage<List<Deployment>> getEntityDeployments() {
        return getDeployments("strimzi.io/name", "my-cluster-entity-operator");
    }

    public CompletionStage<List<ReplicaSet>> getEntityReplicaSets() {
        return getReplicaSets("strimzi.io/name", "my-cluster-entity-operator");
    }

    public CompletionStage<List<PodInfo>> getEntityPods() {
        return getPods("strimzi.io/name", "my-cluster-entity-operator");
    }

    public CompletionStage<List<StatefulSet>> getZookeeperStatefulSets() {
        return getStatefulSets("strimzi.io/name", "my-cluster-zookeeper");
    }

    public CompletionStage<List<PodInfo>> getZookeeperPods() {
        return getPods("strimzi.io/name", "my-cluster-zookeeper");
    }

    public CompletionStage<List<StatefulSet>> getKafkaStatefulSets() {
        return getStatefulSets("strimzi.io/name", "my-cluster-kafka");
    }

    public CompletionStage<List<PodInfo>> getKafkaPods() {
        return getPods("strimzi.io/name", "my-cluster-kafka");
    }

    public CompletionStage<List<Service>> getBootstrapServices() {
        return getServices("strimzi.io/name", "my-cluster-kafka-bootstrap");
    }

    public CompletionStage<List<Service>> getBrokerServices() {
        return getServices("strimzi.io/name", "my-cluster-kafka-brokers");
    }

    public CompletionStage<List<String>> getBrokers() {
//        return getBrokerServices().thenApply(services -> services.stream().map(service -> {
//            String hostname = service.getMetadata().getName() + "." + service.getMetadata().getNamespace();
//            Integer port = Try.of(() -> service.getSpec().getPorts().stream().filter(sport -> Objects.equals(sport.getName(), "tcp-clients")).findFirst().get().getPort()).getOrElse(9092);
//            return hostname + ":" + port;
//        }).collect(Collectors.toList()));
        return CompletableFuture.supplyAsync(() -> List.of("192.168.64.43:32002"));
    }

    private CompletionStage<List<Deployment>> getDeployments(String label, String value) {
        return CompletableFuture.supplyAsync(() ->
                kubernetesClient.apps().deployments().list().getItems().stream().filter(deployment ->
                        Objects.equals(deployment.getMetadata().getLabels().get(label), value)).collect(Collectors.toList()))
                .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    private CompletionStage<List<ReplicaSet>> getReplicaSets(String label, String value) {
        return CompletableFuture.supplyAsync(() ->
                kubernetesClient.apps().replicaSets().list().getItems().stream().filter(replicaSet ->
                        Objects.equals(replicaSet.getMetadata().getLabels().get(label), value)).collect(Collectors.toList()))
                .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    private CompletionStage<List<PodInfo>> getPods(String label, String value) {
        return CompletableFuture.supplyAsync(() -> {
            List<Pod> pods = kubernetesClient.pods().inAnyNamespace().list().getItems().stream().filter(pod -> Objects.equals(pod.getMetadata().getLabels().get(label), value)).collect(Collectors.toList());
            return pods.stream().map(pod -> {
                boolean ready = Try.of(() -> pod.getStatus().getConditions().stream().filter(c -> c.getType().equalsIgnoreCase("Ready")).findFirst().get().getStatus().equalsIgnoreCase("True")).getOrElse(false);
                return PodInfo.builder().phase(pod.getStatus().getPhase()).ready(ready).name(pod.getMetadata().getName()).uid(pod.getMetadata().getUid()).build();
            }).collect(Collectors.toList());
        }).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    private CompletionStage<List<StatefulSet>> getStatefulSets(String label, String value) {
        return CompletableFuture.supplyAsync(() ->
                kubernetesClient.apps().statefulSets().inAnyNamespace().list().getItems().stream().filter(statefulSet ->
                        Objects.equals(statefulSet.getMetadata().getLabels().get(label), value)).collect(Collectors.toList()))
                .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    private CompletionStage<List<Service>> getServices(String label, String value) {
        return CompletableFuture.supplyAsync(() ->
                kubernetesClient.services().inAnyNamespace().list().getItems().stream().filter(service ->
                        Objects.equals(service.getMetadata().getLabels().get(label), value)).collect(Collectors.toList()))
                .completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }
}
