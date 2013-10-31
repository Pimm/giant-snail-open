package org.ilumbo.giantsnail.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.Display;
import android.view.Surface;

public abstract class ScreenOrientationUnlocker {
	/**
	 * The landscape base orientation. The base orientation for non-reverse landscape as well as reverse landscape.
	 */
	public static final boolean BASE_ORIENTATION_LANDSCAPE = true;
	/**
	 * The portrait base orientation. The base orientation for non-reverse portrait as well as reverse portrait.
	 */
	public static final boolean BASE_ORIENTATION_PORTRAIT = false;
	/**
	 * While unlocked, the orientation will be influenced by sensor data.
	 * {@link ScreenOrientationUnlocker#UNLOCKED_BEHAVIOR_USER} is probably a better choice.
	 */
	public static final boolean UNLOCKED_BEHAVIOR_SENSOR = false;
	/**
	 * While unlocked, the orientation will be influenced by sensor data only if the user has enabled sensor-based rotation.
	 */
	public static final boolean UNLOCKED_BEHAVIOR_USER = true;
	public abstract void lock();
	/**
	 * Sets the requested orientation of the passed activity to {@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT} or
	 * {@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} depending on the passed base orientation. The screen orientation is
	 * then locked. Creates and returns a new screen orientation locker which can be used to lock and unlock the screen
	 * orientation.
	 */
	public static final ScreenOrientationUnlocker obtain(final Activity targetActivity, final boolean baseOrientation) {
		return obtain(targetActivity, baseOrientation, UNLOCKED_BEHAVIOR_USER);
	}
	/**
	 * Sets the requested orientation of the passed activity to {@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT} or
	 * {@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} depending on the passed base orientation. The screen orientation is
	 * then locked. Creates and returns a new screen orientation locker which can be used to lock and unlock the screen
	 * orientation.
	 */
	public static final ScreenOrientationUnlocker obtain(final Activity targetActivity, final boolean baseOrientation, final boolean unlockedBehavior) {
		// Set the initial requested orientation.
		final int currentRequestedOrientation = BASE_ORIENTATION_LANDSCAPE == baseOrientation ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		targetActivity.setRequestedOrientation(currentRequestedOrientation);
		// Return the jelly bean implementation if available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			return new JellyBeanScreenOrientationLocker(targetActivity, baseOrientation, unlockedBehavior);
		// Return the gingerbread implementation if available. This implementation is a lot less sexy than the one above, and
		// always behaves as if the unlocked behaviour was set to UNLOCKED_BEHAVIOR_SENSOR.
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			try {
				return new GingerbreadScreenOrientationLocker(targetActivity, baseOrientation);
			} catch (IllegalRotationException exception) {
				// Should the construction fail, log this and "silently" switch to the null object implementation below.
				android.util.Log.w(ScreenOrientationUnlocker.class.getSimpleName(), exception.getMessage());
			}
		}
		// If both implementations above are not available, return a null object implementation which doesn't do anything. This
		// will effectively cause the screen orientation to be permanently locked.
		return new NullScreenOrientationUnlocker();
	}
	public abstract void unlock();
	/**
	 * Simple implementation for Android 4.3 and up (which supports {@link ActivityInfo#SCREEN_ORIENTATION_LOCKED}).
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static final class JellyBeanScreenOrientationLocker extends ScreenOrientationUnlocker {
		/**
		 * The requested orientation of this activity will be set in the lock and unlock methods.
		 */
		private final Activity targetActivity;
		/**
		 * The requested orientation when unlocked. ActivityInfo.SCREEN_ORIENTATION_(SENSOR|USER)_(LANDSCAPE|PORTRAIT).
		 */
		private final int unlockedRequestedOrientation;
		public JellyBeanScreenOrientationLocker(final Activity targetActivity, final boolean baseOrientation, final boolean unlockedBehavior) {
			this.targetActivity = targetActivity;
			// Determine the requested orientation when the activity is unlocked, based on the base orientation and the
			// unlocked behaviour.
			if (BASE_ORIENTATION_LANDSCAPE == baseOrientation) {
				if (UNLOCKED_BEHAVIOR_SENSOR == unlockedBehavior) {
					unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
				} else /* if (UNLOCKED_BEHAVIOR_USER == unlockedBehavior) */ {
					unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
				}
			} else /* if (BASE_ORIENTATION_PORTRAIT == baseOrientation) */ {
				if (UNLOCKED_BEHAVIOR_SENSOR == unlockedBehavior) {
					unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
				} else /* if (UNLOCKED_BEHAVIOR_USER == unlockedBehavior) */ {
					unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
				}
			}
		}
		@Override
		public final void lock() {
			targetActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		}
		@Override
		public final void unlock() {
			targetActivity.setRequestedOrientation(unlockedRequestedOrientation);
		}
	}
	private static final class IllegalRotationException extends Exception {
		private static final long serialVersionUID = 6259458757914445838l;
		public IllegalRotationException(final int value) {
			super(new StringBuilder(24)
					.append("Unexpected rotation ")
					.append(value)
					.append('.').toString());
		}
	}
	/**
	 * Less sexy implementation for Android 2.3 and up. This implementation might malfunction if there are multiple display
	 * areas with different rotations.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static final class GingerbreadScreenOrientationLocker extends ScreenOrientationUnlocker {
		/**
		 * The requested orientation when locked in non-reverse.
		 */
		private final int lockedNonReverseRequestedOrientation;
		/**
		 * The requested orientation when locked in reverse.
		 */
		private final int lockedReverseRequestedOrientation;
		/**
		 * The rotation of the display when the screen is in its non-reverse landscape or portrait orientation.
		 */
		private final int nonReverseRotation;
		/**
		 * The rotation of the display when the screen is in its reverse (upside down) landscape or portrait orientation.
		 */
		private final int reverseRotation;
		/**
		 * The requested orientation of this activity will be set in the lock and unlock methods.
		 */
		private final Activity targetActivity;
		/**
		 * The rotation of this display will be checked when locking the requested orientation.
		 */
		private final Display targetDisplay;
		/**
		 * The requested orientation when unlocked. ActivityInfo.SCREEN_ORIENTATION_SENSOR_(LANDSCAPE|PORTRAIT).
		 */
		private final int unlockedRequestedOrientation;
		public GingerbreadScreenOrientationLocker(final Activity targetActivity, final boolean baseOrientation) throws IllegalRotationException {
			targetDisplay = (this.targetActivity = targetActivity)
					.getWindowManager().getDefaultDisplay();
			// Determine and store the two rotations the screen can have. The two rotations are the non-reverse and the reverse
			// one. Because the current requested orientation is portrait or landscape (and not reverse-portrait or reverse-
			// landscape), the current rotation is the rotation of the display when the screen is in its non-reversed landscape
			// or portrait orientation.
			switch (nonReverseRotation = targetDisplay.getRotation()) {
			case Surface.ROTATION_0:
				reverseRotation = Surface.ROTATION_180;
				break;
			case Surface.ROTATION_90:
				reverseRotation = Surface.ROTATION_270;
				break;
			case Surface.ROTATION_180:
				reverseRotation = Surface.ROTATION_0;
				break;
			case Surface.ROTATION_270:
				reverseRotation = Surface.ROTATION_90;
				break;
			default:
				throw new IllegalRotationException(targetDisplay.getRotation());
			}
			// Figure out the orientations.
			if (BASE_ORIENTATION_LANDSCAPE == baseOrientation) {
				lockedNonReverseRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				lockedReverseRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
			} else /* if (BASE_ORIENTATION_PORTRAIT == baseOrientation) */ {
				lockedNonReverseRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				lockedReverseRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				unlockedRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
			}
		}
		@Override
		public final void lock() {
			// Determine the orientation of the screen based on the rotation of the display. Lock by explicitly requesting that
			// orientation.
			if (targetDisplay.getRotation() == nonReverseRotation) {
				targetActivity.setRequestedOrientation(lockedNonReverseRequestedOrientation);
			} else if (targetDisplay.getRotation() == reverseRotation) {
				targetActivity.setRequestedOrientation(lockedReverseRequestedOrientation);
			}
		}
		@Override
		public final void unlock() {
			targetActivity.setRequestedOrientation(unlockedRequestedOrientation);
		}
	}
	/**
	 * A null object implementation of the screen orientation unlocker. Does nothing.
	 */
	private static final class NullScreenOrientationUnlocker extends ScreenOrientationUnlocker {
		public NullScreenOrientationUnlocker() {
		}
		@Override
		public final void lock() {	
		}
		@Override
		public final void unlock() {
		}		
	}
}