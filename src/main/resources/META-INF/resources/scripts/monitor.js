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
        selectedTopics: new Array()
  },
  mounted:function(){
    this.onLoadPage();
    },
  methods: {
      onLoadPage: function (event) {
        axios.get('/broker').then(response => (this.brokerList = response.data));
      },
      onDropDownBroker: function (event) {
        this.bootstrapShow = !this.bootstrapShow;
      },
      selectTopic: function (event) {
          if (!this.selectedTopics.includes(event.target.value)){
              this.selectedTopics.push(event.target.value);
          }
          this.selectedTopic = null;
      },
      removeTopic: function (topic) {
        var index = this.selectedTopics.indexOf(topic);
        this.selectedTopics.splice(index, 1);
      },
      onSelectBroker: function (broker) {
        this.selectedBroker = broker;
        this.getTopics();
        this.onDropDownBroker();
      },
      getTopics: function (event) {
        axios.get('/topic?brokers=' + this.selectedBroker).then(response => (this.topicList = response.data));
      },
    },
});


var steps = 50;

// get ChartConfig
function chartConfig() {
  var config = {
      type: 'line',
      data: {
          labels: new Array(steps).fill(''),
          datasets: [{
              label: 'topic1',
              fill: false,
              borderWidth: 2,
              pointRadius: 1,
              pointHoverRadius: 2,
              backgroundColor: '#06c',
              borderColor:'#06c',
              data: [0],
          }, {
              label: 'topic2',
              fill: false,
              borderWidth: 2,
              pointRadius: 1,
              pointHoverRadius: 2,
              backgroundColor: '#4cb140',
              borderColor: '#4cb140',
              data: [0],
          }, {
               label: 'topic3',
               fill: false,
               borderWidth: 2,
               pointRadius: 1,
               pointHoverRadius: 2,
               backgroundColor: '#f4c145',
               borderColor: '#f4c145',
               data: [0],
           }]
      },
      options: { responsive: true, scales: { y: {min: 10, max: 50} } }
  };
  return config;
}

function getRandomInt() {
  var min = Math.ceil(0);
  var max = Math.floor(200);
  return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}

function showMonitor() {
    var ctx = document.getElementById('monitorChart').getContext('2d');
    var chart = new Chart(ctx, chartConfig());

    for (let i=1; i<1000; i++) {
        setTimeout( function timer(){
            if (chart.data.labels.length >= steps){
                chart.data.labels.shift();
            }
            chart.data.labels.push('');
            chart.data.datasets.forEach(function(dataset) {
            if (dataset.data.length >= steps) {dataset.data.shift();}
                dataset.data.push(getRandomInt());
            });
            chart.update();
        }, i*1000 );
    }
};
