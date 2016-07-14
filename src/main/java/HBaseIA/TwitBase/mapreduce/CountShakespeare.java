package HBaseIA.TwitBase.mapreduce;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import HBaseIA.TwitBase.hbase.TwitsDAO;
import utils.HBaseUtils;

public class CountShakespeare {

	public static class Map extends TableMapper<Text, LongWritable> {

		public static enum Counters {
			ROWS, SHAKESPEAREAN
		};

		private Random rand;

		/**
		 * Determines if the message pertains to Shakespeare.
		 */
		private boolean containsShakespear(String msg) {
			return rand.nextBoolean();
		}

		@Override
		protected void setup(Context context) {
			rand = new Random(System.currentTimeMillis());
		}

		@Override
		protected void map(ImmutableBytesWritable rowkey, Result result, Context context) {
			byte[] b = result.getColumnLatest(TwitsDAO.TWITS_FAM, TwitsDAO.TWIT_COL).getValue();
			if (b == null)
				return;

			String msg = Bytes.toString(b);
			if (msg.isEmpty())
				return;

			context.getCounter(Counters.ROWS).increment(1);;
			if (containsShakespear(msg)) {
				context.getCounter(Counters.SHAKESPEAREAN).increment(1);;
			}

		}

		@Override
		protected void cleanup(Mapper<ImmutableBytesWritable, Result, Text, LongWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			super.cleanup(context);
			System.out.println(context.getCounter(Counters.ROWS).getValue());
			System.out.println(context.getCounter(Counters.SHAKESPEAREAN).getValue());

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseUtils.getConfiguration();
		Job job = new Job(conf, "TwitBase Shakespeare counter");
		job.setJarByClass(CountShakespeare.class);

		Scan scan = new Scan();
		scan.addColumn(TwitsDAO.TWITS_FAM, TwitsDAO.TWIT_COL);
		TableMapReduceUtil.initTableMapperJob(Bytes.toString(TwitsDAO.TABLE_NAME), scan, Map.class,
				ImmutableBytesWritable.class, Result.class, job);

		job.setOutputFormatClass(NullOutputFormat.class);
		job.setNumReduceTasks(0);

		System.out.println(job.waitForCompletion(true) ? 0 : 1);
	}
}
