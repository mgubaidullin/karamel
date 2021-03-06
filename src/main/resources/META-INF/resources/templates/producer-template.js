const ProducerTemplate = `
<div>
    <div class="pf-l-gallery pf-m-gutter">
        <div class="pf-l-gallery__item">
            <div class="pf-c-card form-text-width">
                <div class="pf-c-card__body">
                    <div class="pf-c-form__group padding">
                        <label class="pf-c-form__label" for="topic">
                            <span class="pf-c-form__label-text">Topic</span>
                            <span class="pf-c-form__label-required" aria-hidden="true">&#42;</span>
                        </label>
                        <select v-model="topic" class="pf-c-form-control" id="topic" name="topic" aria-label="select topic" required>
                            <option v-for="t in topicList" v-bind:value="t.name">
                                {{ t.name }}
                            </option>
                        </select>
                    </div>
                    <div class="pf-c-form__group padding">
                        <label class="pf-c-form__label" for="key">
                            <span class="pf-c-form__label-text">Key</span>
                        </label>
                        <input class="pf-c-form-control" type="text" id="key" name="key" v-model="key"/>
                    </div>
                    <div class="pf-c-form__group padding">
                        <label class="pf-c-form__label" for="value">
                            <span class="pf-c-form__label-text">Value</span>
                            <span class="pf-c-form__label-required" aria-hidden="true">&#42;</span>
                        </label>
                        <textarea class="pf-c-form-control form-textarea-height" type="text" id="value" name="value" v-model="value" required></textarea>
                    </div>
                    <div class="pf-c-form__group pf-m-action">
                        <div class="pf-c-form__actions">
                            <button class="pf-c-button pf-m-primary" type="submit" v-on:click=onPublish>Publish</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="snackbar" v-bind:class="{ show: isDone }">
        <div class="pf-c-alert status pf-m-success" aria-label="Success alert">
            <div class="pf-c-alert__icon">
                <i class="fas fa-check-circle" aria-hidden="true"></i>
            </div>
            <h4 class="pf-c-alert__title">Done!</h4>
        </div>
    </div>
</div>
`

export { ProducerTemplate }