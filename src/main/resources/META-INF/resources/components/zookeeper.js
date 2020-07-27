import { ZookeeperTemplate } from '../templates/zookeeper-template.js'
import Vue from '/js/vue.esm.browser.min.js'

const Zookeeper = Vue.component('zookeeper', {
  data: function () {
    return {
      podList: [],
      statefulSetList: []
    }
  },
  mounted: function () {
    this.onLoadPage();
  },
  methods: {
    onLoadPage: function (event) {
      axios.get('/api/pod?type=zookeeper').then(response => (this.podList = response.data));
      axios.get('/api/statefulset?type=zookeeper').then(response => (this.statefulSetList = response.data));
    },
  },
  template: ZookeeperTemplate
})

export { Zookeeper }