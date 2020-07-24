// Vue app
var client = new Vue({
  el: '#app',
  data: {
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
  },
  mounted: function () {
    this.onLoadPage();
  },
  methods: {
    onLoadPage: function (event) {
      var ctx = document.getElementById('monitorChart').getContext('2d');
      this.chart = new Chart(ctx, this.chartConfig());
      console.log(this.chart);
      window.addEventListener('beforeunload', this.leaving);
      axios.get('/api/broker').then(response => (this.brokerList = response.data));
      this.sourceEvents(true);
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
    sourceEvents: function (clear) {
      if (clear) {
        this.messages = new Array();
      }
      var messages = this.messages;
      var selectedLimit = this.selectedLimit;

      if (this.eventSource != null) {
        this.eventSource.close();
      }
      this.eventSource = new EventSource('/api/aggregate/' + getSessionId(false));
      this.eventSource.addEventListener('message', function(e) {
        console.log(e.data);
      }, false);

      this.eventSource.addEventListener('open', function(e) {
        console.log('open: ' + e);
      }, false);

      this.eventSource.addEventListener('error', function(e) {
        if (e.readyState == EventSource.CLOSED) {
          console.log('close: ' + e);
        }
      }, false);
//      this.eventSource.onmessage = function (event) {
//        json = JSON.parse(event.data);
//        messages.unshift(json);
//        while (messages.length > selectedLimit) {
//          messages.pop();
//        }
//      };
    },
    getTopics: function (event) {
      axios.get('/api/topic?brokers=' + this.selectedBroker).then(response => (this.topicList = response.data));
    },
    startConsumer() {
      axios.post('/api/aggregate/start', { sessionId: getSessionId(false), broker: this.selectedBroker, filter: this.filter })
        .then(function (response) {
          console.log("start consumer");
          this.showSpinner = false;
        });
    },
    leaving() {
      var client = new XMLHttpRequest();
      client.open("POST", '/api/aggregate/stop/' + getSessionId(false), false);
      client.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      client.send({});
    },
    createDataset(name) {
        return {label: name, fill: false, borderWidth: 2, pointRadius: 1, pointHoverRadius: 2,
        backgroundColor: '#06c', borderColor: '#06c', data: [0]};
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
});


function getRandomInt() {
  var min = Math.ceil(0);
  var max = Math.floor(200);
  return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}

function showMonitor() {
  var ctx = document.getElementById('monitorChart').getContext('2d');
  var chart = new Chart(ctx, chartConfig());

  for (let i = 1; i < 1000; i++) {
    setTimeout(function timer() {
      if (chart.data.labels.length >= steps) {
        chart.data.labels.shift();
      }
      chart.data.labels.push('');
      chart.data.datasets.forEach(function (dataset) {
        if (dataset.data.length >= steps) { dataset.data.shift(); }
        dataset.data.push(getRandomInt());
      });
      chart.update();
    }, i * 1000);
  }
};
