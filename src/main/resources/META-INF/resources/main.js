import Vue from '/js/vue.esm.browser.min.js'
import { MainTemplate } from './templates/main-template.js'
import { Kafka } from './components/kafka.js'
import { Topics } from './components/topics.js'

Vue.use(VueRouter);

const router = new VueRouter({
  routes: [
    { path: '/', redirect: "/kafka" },
    { path: '/kafka', component: Kafka, name: "Kafka"},
    { path: '/topics', component: Topics, name: "Topics"}
  ]
})

var client = new Vue({
    el: '#app',
    components: {
    },
    router,
    template: MainTemplate
})
