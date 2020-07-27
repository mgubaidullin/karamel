// Global event bus
import Vue from '/js/vue.esm.browser.min.js'

var EventHub = new Vue()

export default function getEventHub() {
    return EventHub;
}

export {getEventHub}