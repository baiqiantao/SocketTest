package com.heima.socket.server;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {
	//最大容量为5的数组堵塞队列
	private static ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(5, true);
	//private static LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(5);

	private static CountDownLatch producerLatch; //生产者倒计时计数器
	private static CountDownLatch consumerLatch;//消费者倒计时计数器

	public static void main(String[] args) {
		producerLatch = new CountDownLatch(10); //state值为10
		consumerLatch = new CountDownLatch(10); //state值为10

		new Thread(new ProducerTask()).start();
		new Thread(new ConsumerTask()).start();

		try {
			System.out.println("producer waiting...");
			producerLatch.await(); //进入等待状态，直到state值为0，再继续往下执行
			System.out.println("producer end");

			System.out.println("consumer waiting...");
			consumerLatch.await(); //进入等待状态，直到state值为0，再继续往下执行
			System.out.println("consumer end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("end");
	}

//******************************************************************************************
	//生产者
	private static class ProducerTask implements Runnable {
		private Random rnd = new Random();

		@Override
		public void run() {
			try {
				while (true) {
					queue.put(rnd.nextInt(100)); //如果queue容量已满，则当前线程会堵塞，直到有空间再继续

					//offer方法为非堵塞的
					//queue.offer(rnd.nextInt(100), 1, TimeUnit.SECONDS); //等待1秒后还不能加入队列则返回失败，放弃加入
					//queue.offer(rnd.nextInt(100));

					producerLatch.countDown(); //state值减1
					//TimeUnit.SECONDS.sleep(2); //线程休眠2秒
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//消费者
	private static class ConsumerTask implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					Integer value = queue.take(); //如果queue为空，则当前线程会堵塞，直到有新数据加入

					//poll方法为非堵塞的
					//Integer value = queue.poll(1, TimeUnit.SECONDS); //等待1秒后还没有数据可取则返回失败，放弃获取
					//Integer value = queue.poll();

					System.out.println("value = " + value);

					consumerLatch.countDown(); //state值减1
					TimeUnit.SECONDS.sleep(2); //线程休眠2秒
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}