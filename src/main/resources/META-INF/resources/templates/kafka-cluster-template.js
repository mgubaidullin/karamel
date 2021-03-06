const KafkaClusterTemplate = `
<div>
    <section class="pf-c-page__main-section dashboard-subtitle">
        <h1 class="pf-c-title pf-m-xl">Cluster</h1>
    </section>
    <section class="pf-c-page__main-section">
        <!--  Show  data  -->
        <div class="pf-l-gallery pf-m-gutter dashboard">
            <div v-for="node in nodeList" :key="node.id" class="pf-c-card pf-m-hoverable pf-m-compact">
                <div class="pf-c-card__title pf-m-md status">
                    <p>Node {{node.id}}</p>
                    <div class="pf-c-content">
                        <small>{{node.host}}:{{node.port}}</small>
                        <small>{{node.rack}}</small>
                    </div>
                </div>
                <div class="pf-c-card__footer">
                    <div class="pf-c-alert pf-m-inline pf-m-success" aria-label="Success alert">
                        <div class="pf-c-alert__icon">
                            <i class="fas fa-check-circle" aria-hidden="true"></i>
                        </div>
                        <h4 class="pf-c-alert__title">Running</h4>
                    </div>
                </div>
            </div>
        </div>
        <!--  Show spinner while no data  -->
        <div v-show="nodeList.length === 0" class="pf-l-bullseye">
            <div class="pf-c-empty-state pf-m-sm">
                <div class="pf-c-empty-state__content">
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
    </section>
</div>
`

export { KafkaClusterTemplate }