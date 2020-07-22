// Vue app
var client = new Vue({
  el: '#app',
  data: {
        podList: [],
        statefulSetList: []
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/pod?type=zookeeper').then(response => (this.podList = response.data));
        axios.get('/api/statefulset?type=zookeeper').then(response => (this.statefulSetList = response.data));
      },
    },
});
