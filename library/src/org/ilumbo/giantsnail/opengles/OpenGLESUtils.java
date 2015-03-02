package org.ilumbo.giantsnail.opengles;

import org.ilumbo.giantsnail.mathematics.POTMath;

import android.opengl.GLES20;

public final class OpenGLESUtils {
	/**
	 * Checks whether OpenGL errors have occurred. If so, logs and thrown an exception.
	 */
	public static final void checkErrors(String operation) {
		final int errorCode;
		while (GLES20.GL_NO_ERROR != (errorCode = GLES20.glGetError())) {
			final OpenGLESErrorException exception = new OpenGLESErrorException(errorCode, operation);
			android.util.Log.e(OpenGLESUtils.class.getSimpleName(), exception.getMessage());
			throw exception;
		}
	}
	/**
	 * Checks and returns whether the extension with the passed name is available.
	 */
	public static final boolean checkExtension(String name) {
		final int nameLength = name.length();
		// Grab the extensions.
		final String extensionsString = GLES20.glGetString(GLES20.GL_EXTENSIONS);
//		checkErrors("glGetString");
		// Iterate over the extensions.
		int currentExtensionStringStart = 0;
		int currentExtensionStringEnd;
		do {
			if (-1 == (currentExtensionStringEnd = extensionsString.indexOf(' ', currentExtensionStringStart))) {
				currentExtensionStringEnd = extensionsString.length();
			}
			// Check whether this extension is the passed one.
			if (nameLength == currentExtensionStringEnd - currentExtensionStringStart &&
					extensionsString.startsWith(name, currentExtensionStringStart)) {
				return true;
			}
			currentExtensionStringStart = currentExtensionStringEnd + 1;
		} while (extensionsString.length() != currentExtensionStringEnd);
		return false;
	}
	/**
	 * Returns the maximal number of supported texture image units that can be used to access texture maps from the vertex
	 * shader and the fragment processor combined. If both the vertex shader and the fragment processing stage access the same
	 * texture image unit, then that counts as using two texture image units against this limit. This value is at least 8.
	 */
	public static final int getMaximalTextureImageUnitCount() {
		final int[] result = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, result, 0);
//		checkErrors("glGetIntegerv");
		return result[0];
	}
	/**
	 * Returns a rough estimate of the largest texture that the GL can handle. The value is at least 64, and always a POT.
	 */
	public static final int getMaximalTextureSize() {
		final int[] result = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, result, 0);
//		checkErrors("glGetIntegerv");
		// This line ensures the result is a power of two, equal to or less than the value returned above.
		return POTMath.floor(result[0]);
	}
}