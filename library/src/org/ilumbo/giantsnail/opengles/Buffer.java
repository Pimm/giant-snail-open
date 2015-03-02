package org.ilumbo.giantsnail.opengles;

import android.opengl.GLES20;

public class Buffer {
	/**
	 * The data set via {@link #setData()} will be modified once and used at most a few times.
	 */
	public static final byte ACCESS_FREQUENCY_STREAM = 0;
	/**
	 * The data set via {@link #setData()} will be modified once and used many times.
	 */
	public static final byte ACCESS_FREQUENCY_STATIC = 1;
	/**
	 * The data set via {@link #setData()} will be modified repeatedly and used many times.
	 */
	public static final byte ACCESS_FREQUENCY_DYNAMIC = 2;
	/**
	 * The name of the buffer object in OpenGL.
	 */
	public final int name;
	public Buffer() {
		final int[] names = new int[1];
		GLES20.glGenBuffers(1, names, 0);
		name = names[0];
	}
	/**
	 * Binds the buffer to {@link GLES20#GL_ARRAY_BUFFER}. The vertex array pointer parameter that is traditionally interpreted
	 * as a pointer to client-side memory will instead be interpreted as an offset within the buffer measured in basic machine
	 * units.
	 */
	public final void bind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, name);
		OpenGLESUtils.checkErrors("glBindBuffer");
	}
	/**
	 * Creates a new data store for this buffer, deleting an existing data store if any, and transfers the passed data to that
	 * data store. This method binds and unbinds in the process, so after this method is called there is no buffer bound to
	 * {@link GLES20#GL_ARRAY_BUFFER}.
	 */
	public final void setData(int sizeInBytes, java.nio.Buffer data, byte accessFrequency) {
		// Bind. This line unbinds any previously bound buffer, and this method does not restore said buffer.
		bind();
		// Determine the usage.
		final int usage;
		switch (accessFrequency) {
		case ACCESS_FREQUENCY_STREAM:
			usage = GLES20.GL_STREAM_DRAW;
			break;
		case ACCESS_FREQUENCY_STATIC:
			usage = GLES20.GL_STATIC_DRAW;
			break;
		case ACCESS_FREQUENCY_DYNAMIC:
			usage = GLES20.GL_DYNAMIC_DRAW;
			break;
		default:
			throw new IllegalArgumentException("Unsupported access frequency was passed");
		}
		// Set the data.
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sizeInBytes, data, usage);
		OpenGLESUtils.checkErrors("glBufferData");
		// Unbind.
		Buffer.unbind();
	}
	/**
	 * Unbinds the currently bound buffer (regardless of which buffer that is) from {@link GLES20#GL_ARRAY_BUFFER}.
	 */
	public static final void unbind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		OpenGLESUtils.checkErrors("glBindBuffer");
	}
}