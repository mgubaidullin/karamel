import { OperatorsTemplate } from "../templates/operators-template.js";
import { OperatorEntityTemplate } from "../templates/operator-entity-template.js";
import { OperatorClusterTemplate } from "../templates/operator-cluster-template.js";
import Vue from '/js/vue.esm.browser.min.js'

const OperatorCluster = Vue.component('operator-cluster', {
  data: function () {
    return {
      clusterPods: [],
      clusterReplicaSets: [],
      clusterDeployments: []
    }
  },
  mounted: function () {
    axios.get('/api/pod?type=cluster').then(response => (this.clusterPods = response.data));
    axios.get('/api/replicaset?type=cluster').then(response => (this.clusterReplicaSets = response.data));
    axios.get('/api/deployment?type=cluster').then(response => (this.clusterDeployments = response.data));
  },
  template: OperatorClusterTemplate
});

const OperatorEntity = Vue.component('operator-entity', {
  data: function () {
    return {
      entityPods: [],
      entityDeployments: [],
      entityReplicaSets: []
    }
  },
  mounted: function () {
    axios.get('/api/pod?type=entity').then(response => (this.entityPods = response.data));
    axios.get('/api/replicaset?type=entity').then(response => (this.entityReplicaSets = response.data));
    axios.get('/api/deployment?type=entity').then(response => (this.entityDeployments = response.data));
  },
  template: OperatorEntityTemplate
});


const Operators = Vue.component('operators', {
  components: {
    'operator-cluster': OperatorCluster,
    'operator-entity': OperatorEntity
  },
  template: OperatorsTemplate
});

export { Operators }