package com.distributed.lock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import redis.clients.jedis.Jedis;

public abstract class RedisLock implements Lock {

	protected Jedis jedis;
	protected String lockKey;
	protected String lockValue;
	protected volatile boolean isOpenExpirationRenewal = true;

	// 开启定时更新
	protected void scheduleExpirationRenewal() {
		Thread renewalThread  = new Thread(new ExpirationRenewal());

		renewalThread.start();
	}

	private class ExpirationRenewal implements Runnable {

		public void run() {
			while(isOpenExpirationRenewal){
				System.out.println("执行延迟失效时间中...");
				
				String checkAndExpireScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('expire',KEYS[1],ARGV[2]) " +
                        "else " +
                        "return 0 end";
                jedis.eval(checkAndExpireScript, 1, lockKey, lockValue, "10");
                
                sleepBySecond(10);
			}

		}

	}

	// 设置lockValue来标记这个锁
	public RedisLock(Jedis jedis, String lockKey) {
		this(jedis, lockKey, UUID.randomUUID().toString() + Thread.currentThread().getId());

	}

	public RedisLock(Jedis jedis, String lockKey, String lockValue) {
		this.jedis = jedis;
		this.lockKey = lockKey;
		this.lockValue = lockValue;
	}
	
	
	public void sleepBySecond(int second){
        try {
            Thread.sleep(second*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	public void lockInterruptibly() throws InterruptedException {

	}

	public boolean tryLock() {
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}

	public Condition newCondition() {
		return null;
	}

}
