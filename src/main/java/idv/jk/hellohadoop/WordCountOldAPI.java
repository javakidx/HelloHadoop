package idv.jk.hellohadoop;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by bioyang on 15/7/27.
 */
public class WordCountOldAPI
{
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
    {

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
        {
            //output.collect(new Text(value.toString()), new IntWritable(1));
            String[] words = StringUtils.split(value.toString(), ' ');
            for(String word : words)
            {
                output.collect(new Text(word), new IntWritable(1));
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
    {

        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
        {
            int sum = 0;
            while(values.hasNext())
            {
                sum += values.next().get();
            }

            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception
    {
        JobConf conf = new JobConf(WordCountOldAPI.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(MyMapper.class);
        //conf.setCombinerClass(MyReducer.class);
        conf.setReducerClass(MyReducer.class);
        conf.setNumMapTasks(2);
        conf.setNumReduceTasks(2);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        conf.set("mapreduce.textoutputformat.separator", ",");
        //conf.set("mapreduce.input. keyvaluelinerecordreader.key.value. separator", ",");
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }

    /*public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
    {

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
        {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), " ");
            while(tokenizer.hasMoreTokens())
            {
                output.collect(new Text(tokenizer.nextToken()), new IntWritable(1));
            }
        }
    }*/
}
