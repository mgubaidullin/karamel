var app = new Vue({
  el: '#client',
  data: {
    consumerVis: true,
    producerVis: false
  },
  methods: {
      showConsumer: function (event) {
        this.consumerVis = true
        this.producerVis = false
      },
      showProducer: function (event) {
        this.consumerVis = false
        this.producerVis = true
      }
    }
});

var connected = false;
      var socket;

      $( document ).ready(function() {
          var con = connect();
      });

      var connect = function() {
          if (! connected) {
              socket = new WebSocket("ws://" + location.host + "/events");
              socket.onopen = function() {
                  connected = true;
                  console.log("Connected to the web socket");
              };
              socket.onmessage = function(m) {
                location.reload();
              };
          }
      };