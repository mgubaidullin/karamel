const OperatorEntityTemplate = `
<div>
    <section class="pf-c-page__main-section dashboard-subtitle">
        <h1 class="pf-c-title pf-m-xl">Entity operator</h1>
    </section>
    <section class="pf-c-page__main-section">
        <div class="pf-l-gallery pf-m-gutter dashboard">
            <div v-for="deployment in entityDeployments" :key="deployment.uid" class="pf-c-card pf-m-hoverable pf-m-compact">
                <div class="pf-c-card__title pf-m-md status">
                    <p>{{deployment.kind}}</p>
                    <div class="pf-c-content">
                        <small>{{deployment.uid}}</small>
                        <small>{{deployment.name}}</small>
                    </div>
                </div>
                <div class="pf-c-card__footer">
                    <div class="pf-c-alert status pf-m-success" aria-label="Success alert">
                        <div class="pf-c-alert__icon">
                            <i class="fas fa-check-circle" aria-hidden="true"></i>
                        </div>
                        <h4 class="pf-c-alert__title">Desired: {{deployment.replicas}}, Ready: {{deployment.readyReplicas}}</h4>
                    </div>
                </div>
            </div>
            <div v-for="replicaSet in entityReplicaSets" :key="replicaSet.uid" class="pf-c-card pf-m-hoverable pf-m-compact">
                <div class="pf-c-card__title pf-m-md status">
                    <p>{{replicaSet.kind}}</p>
                    <div class="pf-c-content">
                        <small>{{replicaSet.uid}}</small>
                        <small>{{replicaSet.uid}}</small>
                    </div>
                </div>
                <div class="pf-c-card__footer">
                    <div class="pf-c-alert status pf-m-success" aria-label="Success alert">
                        <div class="pf-c-alert__icon">
                            <i class="fas fa-check-circle" aria-hidden="true"></i>
                        </div>
                        <h4 class="pf-c-alert__title">Desired: {{replicaSet.replicas}}, Ready: {{replicaSet.readyReplicas}}</h4>
                    </div>
                </div>
            </div>
            <div v-for="pod in entityPods" :key="pod.uid" class="pf-c-card pf-m-hoverable pf-m-compact">
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
        <div v-show="entityPods.length === 0 && entityReplicaSets.length === 0 && entityDeployments.length ===0" class="pf-l-bullseye">
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

export { OperatorEntityTemplate }