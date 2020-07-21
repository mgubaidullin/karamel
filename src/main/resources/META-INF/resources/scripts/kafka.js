// Vue app
var client = new Vue({
  el: '#app',
  data: {
        nodeList: null
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/node').then(response => (this.nodeList = response.data));
      },
    },
});
