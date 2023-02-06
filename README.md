gradle build \n
gcloud auth application-default login \n

Local Runner: \m
gradle run

Args Reference: https://cloud.google.com/dataflow/docs/guides/setting-pipeline-options#setting_required_options

GCP Runner: \n
gradle run --args="--project=apache-beam-dataflow-376221 --runner=DataflowRunner --region=us-central1 --stagingLocation=gs://dataflow-beam-bucket/staging/ --gcpTempLocation=gs://dataflow-beam-bucket/temp/ --experiments=use_runner_v2"
