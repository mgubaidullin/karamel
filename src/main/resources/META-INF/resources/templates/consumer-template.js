const ConsumerTemplate = `
<table class="pf-c-table pf-m-grid-md" role="grid">
    <thead>
    <tr>
        <th class="pf-m-width-20" scope="col">Topic</th>
        <th class="pf-m-width-10" scope="col">Partition</th>
        <th class="pf-m-width-10" scope="col">Offset</th>
        <th class="pf-m-width-20" scope="col">Timestamp</th>
        <th class="pf-m-width-20" scope="col">Key</th>
        <th class="pf-m-width-20" scope="col">Value</th>
    </tr>
    </thead>

    <tr v-show="messages.length === 0" class="pf-m-height-auto" role="row">
        <td role="cell" colspan="8">
            <div class="pf-l-bullseye">
                <div class="pf-c-empty-state pf-m-sm">
                    <div v-show="showSpinner === false" class="pf-c-empty-state__content">
                        <i class="fas fa- fa-search pf-c-empty-state__icon" aria-hidden="true"></i>
                        <h2 class="pf-c-title pf-m-lg">No results found</h2>
                        <div class="pf-c-empty-state__body">No results match the select criteria.</div>
                    </div>
                    <div v-show="showSpinner === true" class="pf-c-empty-state__content">
                        <div class="pf-c-empty-state__icon">
                        <span class="pf-c-spinner" role="progressbar" aria-valuetext="Loading...">
                          <span class="pf-c-spinner__clipper"></span>
                          <span class="pf-c-spinner__lead-ball"></span>
                          <span class="pf-c-spinner__tail-ball"></span>
                        </span>
                        </div>
                    </div>
                </div>
            </div>
        </td>
    </tr>
    <tr v-show="messages.length > 0" v-for="message in messages" :key="message.id">
        <td>{{message.topic}}</td>
        <td>{{message.partition}}</td>
        <td>{{message.offset}}</td>
        <td>{{message.timestamp}}</td>
        <td v-show="JSON.stringify(message.key).length < 100" class="value">{{message.key}}</td>
        <td v-show="JSON.stringify(message.key).length >= 100" class="value">
            <a v-on:click="showMessage(message)">View</a>
        </td>
        <td v-show="JSON.stringify(message.value).length < 100" class="value">{{message.value}}</td>
        <td v-show="JSON.stringify(message.value).length >= 100" class="value">
            <a v-on:click="showMessage(message)">View</a>
        </td>
    </tr>
</table>
`

export { ConsumerTemplate }