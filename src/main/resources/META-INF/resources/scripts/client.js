// Vue app
var client = new Vue({
  el: '#app',
  data: {
        bootstrapShow: false,
        limitShow: false,
        brokerList: null,
        selectedBroker: 'Select',
        limits: [10, 25, 50, 100, 200],
        selectedLimit: 10,
        messages: new Array(),
        message: null,
        eventSource: null,
        topicList: [],
        tab: 'consumer',
        showSpinner: false,
        topic: '',
        key: '',
        value: ''
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        window.addEventListener('beforeunload', this.leaving);
        axios.get('/api/broker').then(response => (this.brokerList = response.data));
      },
      onDropDownBroker: function (event) {
        this.bootstrapShow = !this.bootstrapShow;
      },
      onSelectBroker: function (broker) {
        this.selectedBroker = broker;
        this.getTopics();
        this.onDropDownBroker();
        this.sourceEvents(false);
        this.showSpinner = true;
      },
      sourceEvents: function (clear) {
          if (clear){
            this.messages = new Array();
          }
          var messages = this.messages;
          var selectedLimit = this.selectedLimit;

          if (this.eventSource != null){
            this.eventSource.close();
          }
          this.eventSource = new EventSource("/api/message/" + getSessionId(false) + "/" + this.selectedBroker);
          this.eventSource.onmessage = function (event) {
                  json = JSON.parse(event.data);
                  json.url = '/message/' + json.topic + '/' + json.partition + '/' + json.offset;
                  messages.unshift(json);
                  while (messages.length > selectedLimit){
                      messages.pop();
                  }
              };
      },
      onDropDownLimit: function (event) {
        this.limitShow = !this.limitShow;
      },
      onSelectLimit: function (limit) {
        this.selectedLimit = limit;
        this.onDropDownLimit();
        this.sourceEvents(false);
      },
      showConsumer: function (event) {
        this.tab = 'consumer';
      },
      showProducer: function (event) {
        this.getTopics();
        this.tab = 'producer';
      },
      showMessage: function (message) {
         this.tab = 'message';
         this.message = message;
      },
      closeMessage: function () {
         this.tab = 'consumer';
      },
      getTopics: function (event) {
        axios.get('/api/topic?brokers=' + this.selectedBroker).then(response => (this.topicList = response.data));
      },
      onPublish: function (event) {
        axios.post('/api/message', { broker:this.selectedBroker, topic: this.topic, key: this.key, value: this.value })
            .then(function (response) {
              showSnackbar();
            });
      },
      leaving() {
          var client = new XMLHttpRequest();
          client.open("POST", '/api/message/stop/' + getSessionId(false), false);
          client.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
          client.send({});
      }
    },
});
