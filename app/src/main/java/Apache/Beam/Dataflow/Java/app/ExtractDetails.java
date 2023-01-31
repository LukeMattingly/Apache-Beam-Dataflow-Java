package Apache.Beam.Dataflow.Java.app;

import java.lang.Double; 

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

public class ExtractDetails{
    private static final String CSV_HEADER="Order_ID,Product,Quantity_Ordered,price_EAch,Order_Date,Purcahse_Address";

    public static void main(String[] args){
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).create();

        runProductDetails(options);
    }

    static void runProductDetails(PipelineOptions options){

     Pipeline p = Pipeline.create(options);

     p.apply("ReadLines", TextIO.read().from("gs://dataflow-beam-bucket/input_data/Sales_April_2019.csv"))
        .apply(ParDo.of(new FilterHeaderFn(CSV_HEADER)))
        .apply("ExtractSalesDetails", ParDo.of(new ExtractSalesDetailsFn(CSV_HEADER)))
        .apply("WriteSalesDetails", TextIO.write().to("gs://dataflow-beam-bucket/output_data/get_sales_details").withHeader("Product,Total_Price,Order_Date"));

    p.run().waitUntilFinish();
   
    }

    
}