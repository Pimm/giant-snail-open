package org.ilumbo.giantsnail.cache;

/**
 * Knows about the elements that exist in the cache, and can be used to coordinate access to those elements.
 */
public interface CacheSupervisor {
	/**
	 * You must create the element and write it to the cache. Call {@link #finish()} after writing it.
	 */
	public static final int OBTAIN_OPERATION_CREATE_AND_WRITE = 0;
	/**
	 * You should read the element directly from cache.
	 */
	public static final int OBTAIN_OPERATION_READ = 2;
	/**
	 * The element is being created and written to cache. You can wait and re-call {@link #determineObtainOperation(int)},
	 * which will return {@link #OBTAIN_OPERATION_READ} at some point. You can also simply create the element and bypass the
	 * cache.
	 */
	public static final int OBTAIN_OPERATION_WAIT_OR_CREATE = 1;
	/**
	 * Determines and returns how an element with the passed identifier must be obtained. Once you call this method, you must
	 * obey whatever is returned. If this is too much of a commitment, call {@link #peekObtainOperation(int)}.
	 */
	public abstract int determineObtainOperation(int identifier);
	/**
	 * Informs the supervisor that the element with the passed identifier is now available in the cache. This method must be
	 * called after an element is created and written to the cache.
	 */
	public abstract void finish(int identifier);
	/**
	 * Determines and returns how an element with the passed identifier should be obtained if it was to be obtained. If this
	 * method returns {@link #OBTAIN_OPERATION_READ}, {@link #determineObtainOperation(int)} will also return
	 * {@link #OBTAIN_OPERATION_READ} for that identifier.
	 */
	public abstract int peekObtainOperation(int identifier);
	/**
	 * Informs the supervisor that the element with the passed identifier is broken, meaning
	 * {@link #determineObtainOperation(int)} or {@link #peekObtainOperation(int)} returned {@link #OBTAIN_OPERATION_READ} but
	 * the element was not available in the cache. This method returns how the element should be obtained. Once you call this
	 * method, you must obey whatever is returned
	 */
	public abstract int refreshAndDetermineObtainOperation(int identifier);
}