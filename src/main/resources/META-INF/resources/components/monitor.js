import { MonitorTemplate } from '../templates/monitor-template.js'
import Vue from '/js/vue.esm.browser.min.js'
import getEventHub from './event-hub.js'

const Monitor = Vue.component('monitor', {
  data: function () {
    return {
      bootstrapShow: false,
      brokerList: null,
      selectedBroker: 'Select',
      limits: [10, 25, 50, 100, 200],
      topicList: [],
      selectedTopic: null,
      selectedTopics: new Array(),
      eventSource: null,
      chart: null,
      steps: 50,
      colors: ['#06c', '#f4c145', '#4cb140']
    }
  },
  mounted: function () {
    this.onLoadPage();
  },
  methods: {
    onLoadPage: function (event) {
      var ctx = this.$refs.monitorChart.getContext('2d');
      this.chart = new Chart(ctx, this.chartConfig());
      window.addEventListener('beforeunload', this.leaving);
      axios.get('/api/broker').then(response => (this.brokerList = response.data));
    },
    onStart: function (event) {
//          this.showSpinner = true;
//          this.startConsumer();
    },
    onDropDownBroker: function (event) {
      this.bootstrapShow = !this.bootstrapShow;
    },
    onSelectBroker: function (broker) {
      this.selectedBroker = broker;
      this.getTopics();
      this.onDropDownBroker();
      this.showSpinner = true;
    },
    selectTopic: function (event) {
      if (!this.selectedTopics.includes(event.target.value)) {
        this.selectedTopics.push(event.target.value);
        this.chart.config.data.datasets.push(this.createDataset(event.target.value));
      }
      this.selectedTopic = null;
    },
    removeTopic: function (topic) {
      var index = this.selectedTopics.indexOf(topic);
      this.selectedTopics.splice(index, 1);
    },
    getTopics: function (event) {
      axios.get('/api/topic?brokers=' + this.selectedBroker).then(response => (this.topicList = response.data));
    },
    startConsumer() {
//      axios.post('/api/aggregate/start', { sessionId: getSessionId(false), broker: this.selectedBroker, filter: this.filter })
//        .then(function (response) {
//          console.log("start consumer");
//          this.showSpinner = false;
//        });
    },
    createDataset(name) {
      return {
        label: name, fill: false, borderWidth: 2, pointRadius: 1, pointHoverRadius: 2,
        backgroundColor: '#06c', borderColor: '#06c', data: [0]
      };
    },
    chartConfig() {
      var config = {
        type: 'line',
        data: {
          labels: new Array(this.steps).fill(''),
          datasets: []
        },
        options: {
          legend: { position: 'bottom' },
          responsive: true,
          scales: {
            y: { min: 10, max: 50 },
            yAxes: [{
              ticks: {
                beginAtZero: true
              }
            }]
          }
        }
      };
      return config;
    }
  },
  template: MonitorTemplate
})

export { Monitor }
