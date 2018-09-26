package com.victor.mapreduce.inputformat;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WholeRecordReader extends RecordReader<NullWritable, BytesWritable>{
	BytesWritable value = new BytesWritable();
	FileSplit split;
	Configuration configuration;
	private boolean isProcessed = false;
	
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		// ��ʼ��
		this.split = (FileSplit) split;
		configuration = context.getConfiguration();
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		
		boolean isResult;
		
		// ҵ���߼�����
		if (!isProcessed) {
			byte[] buf = new byte[(int)split.getLength()];
			
			// ��ȡ�ļ�ϵͳ
			FileSystem fs = FileSystem.get(configuration);
			Path path = split.getPath();
			
			FSDataInputStream fis = null;
			
			try {
				// ���ļ���
				fis = fs.open(path);
				
				// ���Ŀ���
				IOUtils.readFully(fis, buf, 0, buf.length);
				
				// �������ֵ
				value.set(buf, 0, buf.length);
			} finally {
				IOUtils.closeStream(fis);
			}
			
			isProcessed = true;
			
			isResult = true;
//			return true;
		}else {
			isResult = false;
		}
		
		return isResult;
	}

	@Override
	public NullWritable getCurrentKey() throws IOException, InterruptedException {
		return NullWritable.get();
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// ��ȡ����
		
		return isProcessed?1:0;
	}

	@Override
	public void close() throws IOException {
		// �ر���Դ
		
	}

}