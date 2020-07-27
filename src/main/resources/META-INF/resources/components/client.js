import { ClientTemplate } from '../templates/client-template.js'
import Vue from '/js/vue.esm.browser.min.js'
import getEventHub from './event-hub.js'
import { ConsumerTemplate } from "../templates/consumer-template.js";
import { ProducerTemplate } from "../templates/producer-template.js";

const Consumer = Vue.component('consumer', {
  data: function () {
    return {
      messages: new Array(),
      showSpinner: false,
      selectedLimit: 10
    }
  },
  created: function () {
    getEventHub().$on('messages', this.onMessage);
    getEventHub().$on('config', this.onConfig);
  },
  beforeDestroy: function () {
    getEventHub().$off('messages', this.onMessage);
    getEventHub().$off('config', this.onConfig);
  },
  methods: {
    onMessage: function (event) {
      console.log("event : " + event);
      event.url = '/message/' + event.topic + '/' + event.partition + '/' + event.offset;
      this.messages.unshift(event);
      while (this.messages.length > this.selectedLimit) {
        this.messages.pop();
      }
    },
    onConfig: function (event) {
      if (event.type === 'limit') {
        this.selectedLimit = event.limit;
      }
    }
  },
  template: ConsumerTemplate
});

const Producer = Vue.component('producer', {
  data: function () {
    return {
      topicList: [],
      topic: '',
      key: '',
      value: '',
      isDone: false
    }
  },
  computed: {
    selectedBroker() {
      return this.$store.state.selectedBroker;
    }
  },
  created: function () {
    getEventHub().$on('brokerChanged', this.getTopics);
  },
  beforeDestroy: function () {
    getEventHub().$off('brokerChanged', this.getTopics);
  },
  mounted: function () {
    this.getTopics();
  },
  methods: {
    getTopics: function (event) {
      axios.get('/api/topic?brokers=' + this.selectedBroker).then(response => (this.topicList = response.data));
    },
    onPublish: function (event) {
      axios.post('/api/message', { broker: this.selectedBroker, topic: this.topic, key: this.key, value: this.value })
        .then(response => (this.showSnackbar()))
        .catch(function (error) {
          console.log(error);
        });
    },
    showSnackbar: function () {
      this.isDone = true;
      setTimeout(function(scope) { scope.isDone = false; }, 2000, this);
    },
  },
  template: ProducerTemplate
});


const Client = Vue.component('client', {
  data: function () {
    return {
      filter: '.*',
      bootstrapShow: false,
      limitShow: false,
      brokerList: null,
      selectedBroker: 'Select',
      limits: [10, 25, 50, 100, 200],
      selectedLimit: 10,
      message: null,
      tab: 'consumer'
    }
  },
  components: {
    'consumer': Consumer
  },
  mounted: function () {
    axios.get('/api/broker').then(response => (this.brokerList = response.data));
  },
  methods: {
    onReconnect: function (event) {
      this.showSpinner = true;
      this.startConsumer();
    },
    onDropDownBroker: function (event) {
      this.bootstrapShow = !this.bootstrapShow;
    },
    onSelectBroker: function (broker) {
      this.selectedBroker = broker;
      this.$store.commit('setBroker', this.selectedBroker);
      getEventHub().$emit('brokerChanged', {});
      this.onDropDownBroker();
      this.showSpinner = true;
      this.startConsumer();
    },
    onDropDownLimit: function (event) {
      this.limitShow = !this.limitShow;
    },
    onSelectLimit: function (limit) {
      getEventHub().$emit('config', { type: "limit", limit: limit });
      this.onDropDownLimit();
    },
    showConsumer: function (event) {
      this.tab = 'consumer';
    },
    showProducer: function (event) {
      this.tab = 'producer';
    },
    showMessage: function (message) {
      this.tab = 'message';
      this.message = message;
    },
    closeMessage: function () {
      this.tab = 'consumer';
    },
    startConsumer() {
      axios.post('/api/message/start', { sessionId: getSessionId(false), broker: this.selectedBroker, filter: this.filter })
        .then(response => (this.showSpinner = false));
    },
  },
  template: ClientTemplate
})

export { Client }
