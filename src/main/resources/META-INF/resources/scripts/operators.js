// Vue app
var client = new Vue({
  el: '#app',
  data: {
    clusterPods: [],
    clusterReplicaSets: [],
    clusterDeployments: [],
    entityPods: [],
    entityDeployments: [],
    entityReplicaSets: []
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/pod?type=cluster').then(response => (this.clusterPods = response.data));
        axios.get('/api/replicaset?type=cluster').then(response => (this.clusterReplicaSets = response.data));
        axios.get('/api/deployment?type=cluster').then(response => (this.clusterDeployments = response.data));
        axios.get('/api/pod?type=entity').then(response => (this.entityPods = response.data));
        axios.get('/api/replicaset?type=entity').then(response => (this.entityReplicaSets = response.data));
        axios.get('/api/deployment?type=entity').then(response => (this.entityDeployments = response.data));
      },
    },
});
