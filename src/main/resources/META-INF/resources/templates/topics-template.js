const TopicsTemplate = `
<div>
<section class="pf-c-page__main-section pf-m-light">
    <div class="pf-l-split pf-m-gutter">
        <div class="pf-l-split__item">
            <div class="pf-c-content">
                <h1>Topics</h1>
            </div>
        </div>
        <div class="pf-l-split__item pf-m-fill"></div>
        <div class="pf-l-split__item pf-c-form pf-m-horizontal">
            <div class="pf-c-form__group no-space" style="display: block;">
                <div class="pf-c-form__group-label">
                    <label class="pf-c-form__label">
                        <span class="pf-c-form__label-text">Brokers:</span>
                    </label>
                </div>
            </div>
        </div>
        <div class="pf-l-split__item">
            <div class="pf-c-dropdown pf-m-expanded">
                <button class="pf-c-dropdown__toggle" type="button" id="dropdown-broker" aria-expanded="true"
                        v-on:click="onDropDownBroker">
                    <span class="pf-c-dropdown__toggle-text">{{selectedBroker}}</span>
                    <i class="fas fa-caret-down pf-c-dropdown__toggle-icon" aria-hidden="true"></i>
                </button>
                <ul class="pf-c-dropdown__menu pf-m-align-right" aria-labelledby="dropdown-expanded-button"
                    v-show="bootstrapShow">
                    <li v-for="broker in brokerList" :key="broker">
                        <button class="pf-c-dropdown__menu-item" type="submit" v-on:click="onSelectBroker(broker)">
                            {{broker}}
                        </button>
                    </li>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</section>
<section class="pf-c-page__main-section">
    <table class="pf-c-table pf-m-grid-md" role="grid">
        <thead>
        <tr>
            <th>Name</th>
            <th>Partitions</th>
            <th>Internal</th>
        </tr>
        </thead>
        <tr v-show="topicList.length === 0" class="pf-m-height-auto" role="row">
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
        <tr v-show="topicList.length != 0" v-for="topic in topicList" :key="topic.name">
            <td>{{topic.name}}</td>
            <td>{{topic.partitions}}</td>
            <td>{{topic.internal}}</td>
            </td>
        </tr>
    </table>
</section>
</div>
`

export { TopicsTemplate }