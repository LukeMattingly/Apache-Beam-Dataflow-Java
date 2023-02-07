package Apache.Beam.Dataflow.Java.app;

import java.lang.Double;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;
import java.util.Collections;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalmartSales_v2{

    private static final String CSV_HEADER= "Store,Date,Weekly_Sales,Holiday_Flag,"
                                            + "Temperature,Fuel_Price,CPI,Unemployment";

    private static final Logger LOG = LoggerFactory.getLogger(WalmartSales_v2.class);

    public static void main(String[] args){
        StoreSalesAvgOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(StoreSalesAvgOptions.class);

        runStoreSales(options);
    }

    public interface StoreSalesAvgOptions extends PipelineOptions{
        @Description("Path of the file to read")
        @Default.String("gs://dataflow-beam-bucket/input_data/WalmartSubset.csv")
        String getInputFile();

        void setInputFile(String value);

        @Description("Path of the file to write to")
        @Default.String("gs://dataflow-beam-bucket/output_data/WalmartSalesAboveAvg")
        String getOutputFile();

        void setOutputFile(String value);
    }

    static void runStoreSales(StoreSalesAvgOptions options){
        Pipeline p = Pipeline.create(options);

        PCollection<String> walmartSales= p
          .apply("ReadLines", TextIO.read().from(options.getInputFile()))
          .apply("FilterHeader", ParDo.of(new FilterHeaderFn(CSV_HEADER)));

        final PCollectionView<Double> netAverageSales = walmartSales
         .apply("ExtractWeeklySales", FlatMapElements
            .into(TypeDescriptors.doubles())
            .via(csvRow ->Collections.singletonList(
                    Double.parseDouble(csvRow.split(",")[2]))))

         .apply("AvgSalesThroughout", Combine.globally(new Average()).asSingletonView());

        PCollection<KV<String,Double>> salesDetails = walmartSales
            .apply("ExtractAvgSalePrices", ParDo.of(new ExtractStoreDetails()));

        salesDetails.apply("ComparingAvgSales", ParDo.of(new DoFn<KV<String, Double>, String>(){

            private static final long serialVersionUID = 1L;

            @ProcessElement
            public void processElement(ProcessContext c){
                double globalAverage = c.sideInput(netAverageSales);

                if(c.element().getValue() >= globalAverage){
                    LOG.info("Date: " + c.element().getKey() + " has greater average value than global average");
                    c.output(c.element().getKey() + ", " + c.element().getValue());
                }
            }
        }).withSideInputs(netAverageSales))
            .apply("WriteResult", TextIO.write().to(options.getOutputFile())
                    .withoutSharding()
                    .withSuffix(".csv")
                    .withShardNameTemplate("-SSS")
                    .withHeader("DaleOnSalesMoreThanAvg, WeeklySales"));

         p.run().waitUntilFinish();
    }

    private static class FilterHeaderFn extends DoFn<String, String>{

        private final String header;

        public FilterHeaderFn(String header){
            this.header = header;
        }

        @ProcessElement
        public void processElement(ProcessContext c){
            String row = c.element();

            if(!row.isEmpty() && !row.equals(this.header)){
                c.output(row);
            }
        }
    }

    private static class ExtractStoreDetails extends DoFn<String, KV<String,Double>>{

        @ProcessElement
        public void processElement(@Element String element, OutputReceiver<KV<String, Double>> receiver){

            String[] data = element.split(",");

            Double weeklySales = Double.parseDouble(data[2]);
            String date = data[1];

            KV<String, Double> pair = KV.<String, Double>of(date, weeklySales);

            receiver.output(pair);
        }
    }

    private static class Average implements SerializableFunction<Iterable<Double>, Double>{

        private static final long serialVersionUID = 1L;

        @Override
        public Double apply(Iterable<Double> input){
            double sum = 0;
            int count = 0;

            for(double item: input){
                sum+=item;
                count = count+1;
            }

            return sum / count;
        }
    }

}