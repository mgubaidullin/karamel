const ZookeeperTemplate = `
<div>
    <section class="pf-c-page__main-section pf-m-light">
        <div class="pf-l-split pf-m-gutter">
            <div class="pf-l-split__item">
                <div class="pf-c-content">
                    <h1>Zookeeper</h1>
                </div>
            </div>
        </div>
    </section>
    <section class="pf-c-page__main-section">
        <!--  Show  data  -->
        <div class="pf-l-gallery pf-m-gutter dashboard">
            <div v-for="statefulSet in statefulSetList" :key="statefulSet.uid" class="pf-c-card pf-m-hoverable pf-m-compact">
                <div class="pf-c-card__title pf-m-md status">
                    <p>{{statefulSet.kind}}</p>
                    <div class="pf-c-content">
                        <small>{{statefulSet.name}}</small>
                        <small>{{statefulSet.uid}}</small>
                    </div>
                </div>
                <div class="pf-c-card__footer">
                    <div class="pf-c-alert status pf-m-success" aria-label="Success alert">
                        <div class="pf-c-alert__icon">
                            <i class="fas fa-check-circle" aria-hidden="true"></i>
                        </div>
                        <h4 class="pf-c-alert__title">Desired: {{statefulSet.replicas}}, Ready: {{statefulSet.readyReplicas}}</h4>
                    </div>
                </div>
            </div>
            <div v-for="pod in podList" :key="pod.uid" class="pf-c-card pf-m-hoverable pf-m-compact">
                <div class="pf-c-card__title pf-m-md status">
                    <p>Pod</p>
                    <div class="pf-c-content">
                        <small>{{pod.uid}}</small>
                        <small>{{pod.name}}</small>
                    </div>
                </div>
                <div class="pf-c-card__footer">
                    <div v-bind:class="[pod.ready ? 'pf-m-success' : 'pf-m-danger']" class="pf-c-alert pf-m-inline status" aria-label="Success alert">
                        <div class="pf-c-alert__icon">
                            <i v-bind:class="[pod.ready ? 'fa-check-circle' : 'fa-exclamation-circle']" class="fas" aria-hidden="true"></i>
                        </div>
                        <h4 class="pf-c-alert__title">{{pod.phase}}</h4>
                    </div>
                </div>
            </div>
        </div>
        <!--  Show spinner while no data  -->
        <div v-show="podList.length === 0 && statefulSetList.length === 0" class="pf-l-bullseye">
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

export { ZookeeperTemplate }