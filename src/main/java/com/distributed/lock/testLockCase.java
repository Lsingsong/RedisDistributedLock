package com.distributed.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class testLockCase {
	public static void main(String[] args) {

		// 定义线程池
		ThreadPoolExecutor pool = new ThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		// 添加10个线程获取锁
		for (int i = 1; i < 10; i++) {
			MyTask myTask = new MyTask(i);
			pool.execute(myTask);
			 System.out.println("线程池中线程数目："+pool.getPoolSize()+"，队列中等待执行的任务数目："+
			 pool.getQueue().size()+"，已执行玩别的任务数目："+pool.getCompletedTaskCount());
		}

		pool.shutdown();
	}

}
