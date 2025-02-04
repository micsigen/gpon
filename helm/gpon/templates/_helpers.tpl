{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "gpon.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "gpon.labels" -}}
helm.sh/chart: {{ include "gpon.name" . }}
app.kubernetes.io/name: {{ include "gpon.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}