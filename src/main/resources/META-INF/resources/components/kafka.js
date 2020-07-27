import { KafkaTemplate } from "../templates/kafka-template.js";
import { KafkaKubernetesTemplate } from "../templates/kafka-kubernetes-template.js";
import { KafkaClusterTemplate } from "../templates/kafka-cluster-template.js";
import Vue from '/js/vue.esm.browser.min.js'

const KafkaCluster = Vue.component('kafka-cluster', {
  data: function () {
    return {
      nodeList: []
    }
  },
  mounted: function () {
    axios.get('/api/node').then(response => (this.nodeList = response.data));
  },
  template: KafkaClusterTemplate
});

const KafkaKubernetes = Vue.component('kafka-kubernetes', {
  data: function () {
    return {
      podList: [],
      statefulSetList: [],
      brokerServices: [],
      bootstrapServices: []
    }
  },
  mounted: function () {
    axios.get('/api/kubernetes').then(response => {
      this.isKubernetes = response.data.isKubernetes
      if (this.isKubernetes) {
        this.loadKubernetesData();
      }
    });
  },
  methods: {
    loadKubernetesData: function () {
      axios.get('/api/pod?type=kafka').then(response => (this.podList = response.data));
      axios.get('/api/statefulset?type=kafka').then(response => (this.statefulSetList = response.data));
      axios.get('/api/service?type=broker').then(response => (this.brokerServices = response.data));
      axios.get('/api/service?type=bootstrap').then(response => (this.bootstrapServices = response.data));
    }
  },
  template: KafkaKubernetesTemplate
});


const Kafka = Vue.component('kafka', {
  data: function () {
    return {
      isKubernetes: false
    }
  },
  components: {
    'kafka-cluster': KafkaCluster
  },
  template: KafkaTemplate
});

export { Kafka }