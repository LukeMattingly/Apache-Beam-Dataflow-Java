gradle build
gcloud auth application-default login

Local Runner:
gradle run //for local runner

Args Reference: https://cloud.google.com/dataflow/docs/guides/setting-pipeline-options#setting_required_options

GCP Runner:
gradle run --args="--project=apache-beam-dataflow-376221 --runner=DataflowRunner --region=us-central1 --stagingLocation=gs://dataflow-beam-bucket/staging/ --gcpTempLocation-gs://dataflow-beam-bucket/temp/"
