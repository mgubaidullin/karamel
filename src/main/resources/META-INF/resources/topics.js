var app = new Vue({
  el: '#bootstraps',
  data: {
    bootstrapShow: false,
    brokers: ""
  },
  methods: {
      dropdown: function (event) {
        this.bootstrapShow = !this.bootstrapShow
      },
      select: function (broker) {
        this.brokers = broker;
        this.bootstrapShow = false;
      },
      onSubmit: function(e) {
        var formAction = e.target.action;
      },
  },
})

