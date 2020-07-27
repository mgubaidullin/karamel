const KafkaTemplate = `
<div>
    <section class="pf-c-page__main-section pf-m-light">
        <div class="pf-l-split pf-m-gutter">
            <div class="pf-l-split__item">
                <div class="pf-c-content">
                    <h1>Kafka</h1>
                </div>
            </div>
        </div>
    </section>
    <div v-if="isKubernetes">
        <kafka-kubernetes></kafka-kubernetes>
    </div>
    <kafka-cluster></kafka-cluster>
</div>
`

export { KafkaTemplate }