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
        waitingVis: false,
        topic: '',
        key: '',
        value: ''
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/api/broker').then(response => (this.brokerList = response.data));
      },
      onDropDownBroker: function (event) {
        this.bootstrapShow = !this.bootstrapShow;
      },
      onSelectBroker: function (broker) {
        this.selectedBroker = broker;
        axios.post('/api/broker', {brokers: this.selectedBroker, sessionId: getSessionId(true)});
        this.getTopics();
        this.onDropDownBroker();
        this.sourceEvents(true);
        this.waitingVis = true;
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
          this.eventSource = new EventSource("/api/message/" + getSessionId(false));
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
    },
});
