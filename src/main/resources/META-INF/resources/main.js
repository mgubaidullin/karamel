import Vue from '/js/vue.esm.browser.min.js'
import { MainTemplate } from './templates/main-template.js'
import { Kafka } from './components/kafka.js'
import { Topics } from './components/topics.js'
import { Client } from './components/client.js'
import getEventHub from './components/event-hub.js'

// Store
Vue.use(Vuex);
const store = new Vuex.Store({
  state: {
    selectedBroker: null
  },
  mutations: {
    setBroker (state, broker) {
      state.selectedBroker = broker;
    }
  }
})

// Router
Vue.use(VueRouter);
const router = new VueRouter({
  routes: [
    { path: '/', redirect: "/kafka" },
    { path: '/kafka', component: Kafka, name: "Kafka" },
    { path: '/topics', component: Topics, name: "Topics" },
    { path: '/client', component: Client, name: "Client" },
  ]
})

// Application
var client = new Vue({
  el: '#app',
  data: {
    eventSource: null
  },
  store: store,
  components: {
  },
  router,
  template: MainTemplate,
  mounted: function () {
    try {
      if (this.eventSource != null) {
        this.eventSource.close();
      }
      this.eventSource = new EventSource('/api/message/' + getSessionId(false));
      // console.log('eventSource: ' + this.eventSource);
      this.eventSource.onmessage = function (event) {
        console.log('received : ' + event.data);
        var json = JSON.parse(event.data);
        if (json.type === 'message') {
          getEventHub().$emit('messages', json);
        } else if (json.type === 'aggregation') {
          getEventHub().$emit('aggregations', json);
        }
      };
    }
    catch (err) {
      console.log('Error ' + err);
    }
  },
  destroyed: function () {
    var client = new XMLHttpRequest();
    client.open("POST", '/api/message/stop/' + getSessionId(false), false);
    client.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    client.send({});
  }
})
