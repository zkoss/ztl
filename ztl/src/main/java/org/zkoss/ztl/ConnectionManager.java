package org.zkoss.ztl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.zkoss.ztl.util.ConfigHelper;

public class ConnectionManager {
		
	private static ConnectionManager instance;
	private static ConfigHelper configHelper;
	
	// milliseconds
	private int waitingPeriod = 1000;
	private int waitTimes = 0;
	private Map<String, LockEntity> openedFileMap = new HashMap<String, LockEntity>();
	
	class LockEntity {
		File file;
		private RandomAccessFile openedFile;
		private FileLock lock;
		
		public LockEntity(File file, RandomAccessFile openedFile, FileLock lock) {
			this.file = file;
			this.openedFile = openedFile;
			this.lock = lock;
		}
		
		public void release() {
			try {
				lock.release();
				openedFile.close();
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	static{
		instance = new ConnectionManager();
		configHelper = ConfigHelper.getInstance();
	}
	
	public ConnectionManager(){}
	
	/*
	 * Public method
	 */
	
	public static ConnectionManager getInstance() {
		return instance;
	}
	
	public String getAvailableRemote(String browserKey, Map<String, List<String>> remoteMap) {
		
		List<String> remotes = remoteMap.get(browserKey);
		
		do {
			String remote = tryLock(remotes);
			
			if(remote == null)
				waitForConnection();
			else
				return remote;
		} while(waitTimes <= configHelper.getConnectionWaitTimes());
		
		return null;
	}
	
	public void releaseRemote(String remote) throws IOException {
		if(openedFileMap.containsKey(remote)) {
			openedFileMap.get(remote).release();
			openedFileMap.remove(remote);
			System.out.println("release remote, " + remote);
		}
	}
	
	/*
	 * Private method
	 */
	private String tryLock(List<String> remotes) {
		
		for (String remote : remotes) {
			File lockFile = getFile(remote);
			
			try {
				RandomAccessFile file = new RandomAccessFile(lockFile, "rw");
				FileLock lock = file.getChannel().lock();
				openedFileMap.put(remote, new LockEntity(lockFile, file, lock));
				System.out.println("get connection... with " + remote);
				return remote;
			} catch (Exception e) {
				System.out.println("can't create or open a file to write");
			}
		}
		
		return null;
	}

	private File getFile(String remote) {
		return new File(configHelper.getMutexDir(), remote + ".lock");
	}

	private void waitForConnection() {
		try {
			System.out.println("wait for connection...");
			Thread.sleep(configHelper.getConnectionWaitPeriod());
		} catch (InterruptedException e) {}
		// like priority queue
		waitingPeriod -= waitingPeriod/100;
	}
	
	@Test
	public void testConnection() throws InterruptedException {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("ie8", Arrays.asList("10.1.3.1", "10.1.3.2"));
		map.put("ie9", Arrays.asList("10.1.3.7", "10.1.3.8"));
		
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new TestThread("ie8", map));
		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie9", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
//		executor.execute(new TestThread("ie8", map));
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}
	
	class TestThread implements Runnable {

		private String name;
		private Map<String, List<String>> remoteMap;
		
		public TestThread(String name, Map<String, List<String>> remoteMap) {
			this.name = name;
			this.remoteMap = remoteMap;
		}
		
		@Override
		public void run() {
			ConnectionManager manager = ConnectionManager.getInstance();
			String remote = manager.getAvailableRemote(name, remoteMap);
			
			try {
				Thread.sleep(10000);
				manager.releaseRemote(remote);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
}
