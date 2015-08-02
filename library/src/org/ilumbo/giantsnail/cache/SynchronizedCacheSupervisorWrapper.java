package org.ilumbo.giantsnail.cache;

public class SynchronizedCacheSupervisorWrapper implements CacheSupervisor {
	/**
	 * The wrappee.
	 */
	protected final CacheSupervisor wrappee;
	/**
	 * Hold this lock while using {@link #wrappee}.
	 */
	protected final Object wrappeeLock;
	public SynchronizedCacheSupervisorWrapper(CacheSupervisor wrappee) {
		this.wrappee = wrappee;
		wrappeeLock = new Object();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int determineObtainOperation(int identifier) {
		synchronized (wrappeeLock) {
			return wrappee.determineObtainOperation(identifier);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish(int identifier) {
		synchronized (wrappeeLock) {
			wrappee.finish(identifier);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int peekObtainOperation(int identifier) {
		synchronized (wrappeeLock) {
			return wrappee.peekObtainOperation(identifier);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int refreshAndDetermineObtainOperation(int identifier) {
		synchronized (wrappeeLock) {
			return wrappee.refreshAndDetermineObtainOperation(identifier);
		}
	}
}