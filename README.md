Commands to get started and to run

```
gradle build
gcloud auth application-default login
```

Local Runner:

```
gradle run
```

GCP Runner:

```
gradle run --args="--project=apache-beam-dataflow-376221 --runner=DataflowRunner --region=us-central1 --stagingLocation=gs://dataflow-beam-bucket/staging/ --gcpTempLocation=gs://dataflow-beam-bucket/temp/"
```

Args Reference: https://cloud.google.com/dataflow/docs/guides/setting-pipeline-options#setting_required_options

Optional Args:

--experiments=use_runner_v2 \
--maxNumWorkers=20 \
--numWorkers=5
