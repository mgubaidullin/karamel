// Vue app
var client = new Vue({
  el: '#app',
  data: {
    isKubernetes: false,
    nodeList: [],
    podList: [],
    statefulSetList: [],
    brokerServices: [],
    bootstrapServices: []
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/kubernetes').then(response => {
            this.isKubernetes = response.data.isKubernetes
            if (this.isKubernetes) {
                this.loadKubernetesData();
            }
            axios.get('/api/node').then(response => (this.nodeList = response.data));
        });
      },
      loadKubernetesData: function() {
            axios.get('/api/pod?type=kafka').then(response => (this.podList = response.data));
            axios.get('/api/statefulset?type=kafka').then(response => (this.statefulSetList = response.data));
            axios.get('/api/service?type=broker').then(response => (this.brokerServices = response.data));
            axios.get('/api/service?type=bootstrap').then(response => (this.bootstrapServices = response.data));
      }
    },
});
