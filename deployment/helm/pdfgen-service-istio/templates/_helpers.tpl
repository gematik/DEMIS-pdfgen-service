{{/*
Expand the name of the chart.
*/}}
{{- define "pdfgen-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "pdfgen-service.fullversionname" -}}
{{- $name := .Values.fullName }}
{{- $version := regexReplaceAll "\\.+" .Values.version "-" }}
{{- printf "%s-%s" $name $version | trunc 63 }}
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
app: {{ .Values.fullName }}
{{- end }}


