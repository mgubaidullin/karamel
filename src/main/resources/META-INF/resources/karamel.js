// Show snackbar
function showSnackbar() {
  var x = document.getElementById("snackbar");
  x.className = "show";
  setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2000);
}
// find JSESSIONID
function getJSessionId(){
    var jsId = document.cookie.match(/JSESSIONID=[^;]+/);
    if(jsId != null) {
        if (jsId instanceof Array)
            jsId = jsId[0].substring(11);
        else
            jsId = jsId.substring(11);
    }
    return jsId;
}

// Vue app
var client = new Vue({
  el: '#app',
  data: {
    connected: false,
    socket: null,
    bootstrapShow: false,
    brokers: '',
    consumerVis: true,
    producerVis: false,
    topic: '',
    key: '',
    value: ''
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        var str = window.location.pathname;
        var isClient = str.startsWith("/client");
        if (isClient) {
            this.onSocketConnect();
        }
      },
      onSocketConnect: function() {
        if (! this.connected) {
            this.socket = new WebSocket("ws://" + location.host + "/events/" + getJSessionId());
            this.socket.onopen = function() {
                this.connected = true;
            };
            this.socket.onmessage = function(m) {
                location.reload();
            };
        }
      },
      onDropDownBroker: function (event) {
        this.bootstrapShow = !this.bootstrapShow;
      },
      onSelectBroker: function (broker) {
        this.brokers = broker;
        axios.post('/brokers', { brokers: this.brokers })
            .then(function (response) {
              location.reload();
            });
      },
      showConsumer: function (event) {
        this.consumerVis = true
        this.producerVis = false
      },
      showProducer: function (event) {
        this.consumerVis = false
        this.producerVis = true
      },
      onPublish: function (event) {
          axios.post('/message', {
              topic: this.topic,
              key: this.key,
              value: this.value
            })
            .then(function (response) {
              showSnackbar();
            });
      },
      onReconnect: function (event) {
        axios.post('/reconnect', {})
          .then(function (response) {
          });
      }
    },
});
