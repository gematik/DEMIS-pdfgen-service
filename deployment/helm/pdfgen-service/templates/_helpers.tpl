{{/*
Expand the name of the chart.
*/}}
{{- define "pdfgen-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "pdfgen-service.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "pdfgen-service.fullversionname" -}}
{{- if .Values.istio.enable }}
{{- $name := include "pdfgen-service.fullname" . }}
{{- $version := regexReplaceAll "\\.+" .Chart.Version "-" }}
{{- printf "%s-%s" $name $version | trunc 63 }}
{{- else }}
{{- include "pdfgen-service.fullname" . }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "pdfgen-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "pdfgen-service.labels" -}}
helm.sh/chart: {{ include "pdfgen-service.chart" . }}
{{ include "pdfgen-service.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- with .Values.customLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "pdfgen-service.selectorLabels" -}}
{{ if .Values.istio.enable -}}
app: {{ include "pdfgen-service.name" . }}
version: {{ .Chart.AppVersion | quote }}
{{ end -}}
app.kubernetes.io/name: {{ include "pdfgen-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Deployment labels
*/}}
{{- define "pdfgen-service.deploymentLabels" -}}
{{ if .Values.istio.enable -}}
istio-validate-jwt: "{{ .Values.istio.validateJwt | required ".Values.istio.validateJwt is required" }}"
{{- with .Values.deploymentLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}
{{- end }}
{{/*
Create the name of the service account to use
*/}}
{{- define "pdfgen-service.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "pdfgen-service.fullversionname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
