package org.ilumbo.giantsnail.opengles;

import org.ilumbo.giantsnail.mathematics.POTMath;

import android.opengl.GLES20;

public final class OpenGLESUtils {
	/**
	 * Checks whether OpenGL errors have occurred. If so, logs and thrown an exception.
	 */
	public static final void checkErrors(String operation) {
		int errorCode;
		while (GLES20.GL_NO_ERROR != (errorCode = GLES20.glGetError())) {
			final OpenGLESErrorException exception = new OpenGLESErrorException(errorCode, operation);
			android.util.Log.e("OpenGL", exception.getMessage());
			throw exception;
		}
	}
	/**
	 * Returns the maximal texture size (width and height) for OpenGL. Always a POT (power of two).
	 */
	public static final int getMaximalTextureSize() {
		final int[] result = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, result, 0);
//		checkErrors("glGetIntegerv");
		// This line ensures the result is a power of two, equal to or less than the value returned above.
		return POTMath.floor(result[0]);
	}
}