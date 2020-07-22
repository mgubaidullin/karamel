// Vue app
var client = new Vue({
  el: '#sidebar',
  data: {
        isKubernetes: false
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/kubernetes').then(response => (this.isKubernetes = response.data.isKubernetes));
      },
    },
});
