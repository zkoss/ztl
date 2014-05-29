package org.zkoss.ztl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.zkoss.ztl.util.ConfigHelper;

public class ConnectionManager {
		
	private static ConnectionManager instance;
	private static ConfigHelper configHelper;
	private static long uuid = 0L;
	public void printUUID(long uuid) {
		this.uuid = uuid;
		
	}
	private static int reducePeriod;
	
	// milliseconds
	private ThreadLocal<Integer> waitingPeriod = new ThreadLocal<Integer>();
	private Map<String, LockEntity> openedFileMap = new HashMap<String, LockEntity>();
	
	class LockEntity {
		private RandomAccessFile openedFile;
		private FileLock lock;
		private String remote;
		
		public LockEntity(RandomAccessFile openedFile, FileLock lock, String remote) {
			this.openedFile = openedFile;
			this.lock = lock;
			this.remote = remote;
		}
		
		public void release() {
			try {
				lock.release();
				openedFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static{
		configHelper = ConfigHelper.getInstance();
		reducePeriod = configHelper.getConnectionReducePeriod();
		instance = new ConnectionManager();
	}
	
	private ConnectionManager(){}

	/*
	 * Public method
	 */
	public static ConnectionManager getInstance() {
		return instance;
	}
	
	public String getAvailableRemote(String browserKey, Map<String, List<String>> remoteMap) {
		this.waitingPeriod.set(configHelper.getConnectionWaitPeriod());
		
		List<String> remotes = remoteMap.get(browserKey);		
		do {
			String remote = tryLock(remotes, browserKey);
			
			if(remote == null)
				waitForConnection();
			else
				return remote;
			
		} while(true);
	}
	
	public void releaseRemote(String remote) {
		System.out.println("log release remote...");
		
		waitingPeriod.remove();
		
		if(openedFileMap.containsKey(remote)) {
			openedFileMap.get(remote).release();
			openedFileMap.remove(remote);
			// System.out.println(random + ":release remote, " + remote);
		}
	}
	
	public String getOpenedRemote(String browserKey) {
		if(openedFileMap.containsKey(browserKey))
			return openedFileMap.get(browserKey).remote;
		
		return null;
	}
	
	/*
	 * Private method
	 */
	private String tryLock(List<String> remotes, String browserKey) {
		
		for (String remote : remotes) {
			File lockFile = null;
			RandomAccessFile file = null;
			FileLock lock = null;
			
			try {
				lockFile = getFile(remote);
				file = new RandomAccessFile(lockFile, "rw");
				lock = file.getChannel().tryLock();
				
				if(lock != null) {
					openedFileMap.put(browserKey, new LockEntity(file, lock, remote));
					// System.out.println(random + ":get connection... with " + remote);
					return remote;
				}
			} catch (Exception e) {
				System.out.println(uuid + ":can't create or open a file to write: " + e.getMessage());
			} finally {
				if(lock == null)
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		
		return null;
	}

	private File getFile(String remote) {
		try {
			remote = URLEncoder.encode(remote, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new File(configHelper.getMutexDir(), remote + ".lock");
	}

	private void waitForConnection() {
		try {
			Thread.sleep(configHelper.getConnectionWaitPeriod());
		} catch (InterruptedException e) {}
		// like priority queue
		int wp = waitingPeriod.get();
		if(wp > reducePeriod) wp -= wp / reducePeriod;
		waitingPeriod.set(wp);
	}
	
//	@Test
//	public void testConnection() throws InterruptedException {
//		Map<String, List<String>> map = new HashMap<String, List<String>>();
//		map.put("ie8", Arrays.asList("10.1.3.1", "10.1.3.2"));
//		map.put("ie9", Arrays.asList("10.1.3.7", "10.1.3.8"));
//		
//		ExecutorService executor = Executors.newCachedThreadPool();
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie9", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
////		executor.execute(new TestThread("ie8", map));
//		
//		executor.shutdown();
//		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//	}
//	
//	class TestThread implements Runnable {
//
//		private String name;
//		private Map<String, List<String>> remoteMap;
//		
//		public TestThread(String name, Map<String, List<String>> remoteMap) {
//			this.name = name;
//			this.remoteMap = remoteMap;
//		}
//		
//		@Override
//		public void run() {
//			ConnectionManager manager = ConnectionManager.getInstance();
//			String remote = manager.getAvailableRemote(name, remoteMap);
//			
//			try {
//				Thread.sleep(10000);
//				manager.releaseRemote(remote);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			
//		}
//		
//	}
}
