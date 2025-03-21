
Zeebe telepítése k3d-be

```
helm repo add camunda https://helm.camunda.io
helm repo update
```

```
helm install camunda-platform camunda/camunda-platform \
    -f values.yaml
```

Csak upgrade

```
helm upgrade camunda-platform camunda/camunda-platform -f values.yaml
```


```
NAME: camunda-platform
LAST DEPLOYED: Wed Jan 29 15:05:15 2025
NAMESPACE: zeebe
STATUS: deployed
REVISION: 1
NOTES:
# (camunda-platform - 11.1.1)

 ######     ###    ##     ## ##     ## ##    ## ########     ###
##    ##   ## ##   ###   ### ##     ## ###   ## ##     ##   ## ##
##        ##   ##  #### #### ##     ## ####  ## ##     ##  ##   ##
##       ##     ## ## ### ## ##     ## ## ## ## ##     ## ##     ##
##       ######### ##     ## ##     ## ##  #### ##     ## #########
##    ## ##     ## ##     ## ##     ## ##   ### ##     ## ##     ##
 ######  ##     ## ##     ##  #######  ##    ## ########  ##     ##

###################################################################

## Installed Services:

- Console:
  - Enabled: false
- Zeebe:
  - Enabled: true
  - Docker Image used for Zeebe: camunda/zeebe:8.6.7
  - Zeebe Cluster Name: "camunda-platform-zeebe"
  - Prometheus ServiceMonitor Enabled: false
- Operate:
  - Enabled: true
  - Docker Image used for Operate: camunda/operate:8.6.7
- Tasklist:
  - Enabled: true
  - Docker Image used for Tasklist: camunda/tasklist:8.6.7
- Optimize:
  - Enabled: false
- Connectors:
  - Enabled: true
  - Docker Image used for Connectors: camunda/connectors-bundle:8.6.6
- Identity:
  - Enabled: false
- Web Modeler:
  - Enabled: false
- Elasticsearch:
  - Enabled: true
  - Docker Image used for Elasticsearch: bitnami/elasticsearch:8.15.4

### Zeebe

The Cluster itself is not exposed as a service which means that you can use `kubectl port-forward` to access the Zeebe cluster from outside Kubernetes:

> kubectl port-forward svc/camunda-platform-zeebe-gateway 26500:26500 -n zeebe
> kubectl port-forward svc/camunda-platform-zeebe-gateway 8088:8080 -n zeebe

Now you can connect your workers and clients to `localhost:26500`


### Connecting to Web apps

As part of the Helm charts, an ingress definition can be deployed, but you require to have an Ingress Controller for that Ingress to be Exposed.
In order to deploy the ingress manifest, set `<service>.ingress.enabled` to `true`. Example: `operate.ingress.enabled=true`

If you don't have an ingress controller you can use `kubectl port-forward` to access the deployed web application from outside the cluster:


Operate:
> kubectl port-forward svc/camunda-platform-operate  8081:80
Tasklist:
> kubectl port-forward svc/camunda-platform-tasklist 8082:80


Connectors:
> kubectl port-forward svc/camunda-platform-connectors 8086:8080


Now you can point your browser to one of the service's login pages. Example: http://localhost:8081 for Operate.

Default user and password: "demo/demo"


## Console configuration

- name: camunda-platform
  namespace: zeebe
  version: 11.1.1
  components:
  
  - name: Operate
    id: operate
    version: 8.6.7
    url: http://localhost:8081
    readiness: http://camunda-platform-operate.zeebe:9600/actuator/health/readiness
    metrics: http://camunda-platform-operate.zeebe:9600/actuator/prometheus
  - name: Tasklist
    id: tasklist
    version: 8.6.7
    url: http://localhost:8082
    readiness: http://camunda-platform-tasklist.zeebe:9600/actuator/health/readiness
    metrics: http://camunda-platform-tasklist.zeebe:9600/actuator/prometheus
  - name: Zeebe Gateway
    id: zeebeGateway
    version: 8.6.7
    urls:
      grpc: http://localhost:26500
      http: http://localhost:8088
    readiness: http://camunda-platform-zeebe-gateway.zeebe:9600/actuator/health/readiness
    metrics: http://camunda-platform-zeebe-gateway.zeebe:9600/actuator/prometheus
  - name: Zeebe
    id: zeebe
    version: 8.6.7
    readiness: http://camunda-platform-zeebe.zeebe:9600/actuator/health/readiness
    metrics: http://camunda-platform-zeebe.zeebe:9600/actuator/prometheus

[camunda][warning]
DEPRECATION NOTICE: Starting from appVersion 8.7, the Camunda Helm chart will no longer support the global.multiregion.installationType option. This is replaced with a new procedure for managing multi-region installations documented here:
https://docs.camunda.io/docs/self-managed/operational-guides/multi-region/dual-region-operational-procedure/
Please unset this option to remove the warning.
```

Segítség a telepítéshez: https://docs.camunda.io/docs/self-managed/setup/deploy/local/local-kubernetes-cluster/?c8-connectivity=ingress

Ingress telepítése k3d-be

```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update helm install ingress-nginx ingress-nginx/ingress-nginx --create-namespace -n ingress-nginx
```

Aztán létrehozható egy ingress config a zeebe ns-ben

```
kubectl apply -f ingress.yaml -n zeebe
```
Alapvetően traffic tipusu ingress támogatott a k3d-ben. De könnyen a fenti lépésben lecserélhető nginx-re.

A curl telepítése után másik cluster alatt lévő ns-ből elérhető a zeebe proxy: 

```
~ $ curl 10.42.0.40:26500
curl: (1) Received HTTP/0.9 when not allowed
```

Most ha készen vagyunk a workflow telepítéssel, akkor a következő módon lehet kipróbálni.

```
zbctl --insecure --address localhost:26500 status
Cluster size: 1
Partitions count: 1
Replication factor: 1
Gateway version: 8.6.7
Brokers:
  Broker 0 - camunda-platform-zeebe-0.camunda-platform-zeebe:26501
    Version: 8.6.7
    Partition 1 : Leader, Healthy
```

Konkrét hívás, vagyis egy példányt elindítunk.

```
zbctl --insecure --address localhost:26500 create instance gpon_process_1

{
  "processDefinitionKey": "2251799813696671",
  "bpmnProcessId": "gpon_process_1",
  "version": 2,
  "processInstanceKey": "2251799813698731",
  "tenantId": "<default>"
}
```

Be kell importalni az image-t a cluster-be.

```
k3d image import fibonacci-service -c zeebe-test
```