const MonitorTemplate = `
<div>
    <section class="pf-c-page__main-section pf-m-light">
        <div class="pf-l-split pf-m-gutter">
            <div class="pf-l-split__item">
                <div class="pf-c-content">
                    <h1>Monitor</h1>
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
        <div class="pf-c-toolbar">
            <div class="pf-c-toolbar__content">
                <div class="pf-c-toolbar__content-section">
                    <div class="pf-c-toolbar__item">
                        <div class="pf-l-split__item pf-c-form pf-m-horizontal">
                            <div class="pf-c-form__group no-space" style="display: block;">
                                <div class="pf-c-form__group-label">
                                    <label class="pf-c-form__label">
                                        <span class="pf-c-form__label-text">Topics:</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="pf-c-toolbar__item">
                        <select v-model="selectedTopic" v-on:change="selectTopic($event)" class="pf-c-form-control" style="width: 100%;"
                                id="topic" name="topic" aria-label="select topic">
                            <option v-for="t in topicList" v-bind:value="t.name">
                                {{ t.name }}
                            </option>
                        </select>
                    </div>
                    <hr class="pf-c-divider pf-m-vertical" />
                    <div class="pf-c-toolbar__item">
                        <div class="pf-l-split__item pf-c-form">
                            <div class="pf-c-form__group no-space" style="display: block;">
                                <div class="centered pf-c-form__group-label">
                                    <label class="pf-c-form__label">
                                        <span class="pf-c-form__label-text">Selected topics:</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="pf-c-toolbar__item">
                        <div v-if="selectedTopics.length > 0" class="pf-c-chip-group">
                            <ul v-for="topic in selectedTopics" v-bind:value="topic" class="pf-c-chip-group__list"
                                role="list"
                                aria-labelledby="Chip-group-with-categories-removable-label">
                                <li class="pf-c-chip-group__list-item">
                                    <div class="pf-c-chip">
                                        <span class="pf-c-chip__text" id="chip-name">{{topic}}</span>
                                        <button v-on:click="removeTopic(topic)" class="pf-c-button pf-m-plain"
                                                type="button"
                                                aria-label="Remove"
                                                id="remove">
                                            <i class="fas fa-times" aria-hidden="true"></i>
                                        </button>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="pf-c-toolbar__group pf-m-toggle-group pf-m-show pf-m-align-right">
                        <div v-if="selectedTopics.length > 0" class="pf-c-toolbar__item">
                        <div class="pf-c-toolbar__item">
                            <div class="pf-l-split__item">
                                <button class="pf-c-button" type="submit" v-on:click="onStart">
                                    <span class="pf-c-button__icon">
                                        <i class="fas fa-play" aria-hidden="true"></i>
                                    </span>
                                    <span class="pf-c-button__text">Start</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <canvas ref="monitorChart" id="monitorChart" width="400" height="150"></canvas>
    </section>
</div>
`

export { MonitorTemplate }