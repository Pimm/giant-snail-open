package org.ilumbo.giantsnail.opengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

public class Buffer {
	/**
	 * The data set via {@link #setData(int, java.nio.Buffer, byte)} will be modified once and used at most a few times.
	 */
	public static final byte ACCESS_FREQUENCY_STREAM = 0;
	/**
	 * The data set via {@link #setData(int, java.nio.Buffer, byte)} will be modified once and used many times.
	 */
	public static final byte ACCESS_FREQUENCY_STATIC = 1;
	/**
	 * The data set via {@link #setData(int, java.nio.Buffer, byte)} will be modified repeatedly and used many times.
	 */
	public static final byte ACCESS_FREQUENCY_DYNAMIC = 2;
	/**
	 * The number of bytes the data store for this buffer is.
	 */
	protected int capacity;
	/**
	 * The name of the buffer object in OpenGL.
	 */
	public final int name;
	/**
	 * A re-usable native buffer, used in {@link #setData(float[], byte)}.
	 */
	protected ByteBuffer nativeBuffer;
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
	public final Buffer bind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, name);
//		OpenGLESUtils.checkErrors("glBindBuffer");
		return this;
	}
	/**
	 * Removes the buffer from OpenGL.
	 */
	public final void dispose() {
		GLES20.glDeleteBuffers(1, new int[]{name}, 0);
//		OpenGLESUtils.checkErrors("glDeleteBuffers");
	}
	/**
	 * Injects the passed data into the existing data store of the previously bound buffer.
	 */
	public final void insertData(java.nio.Buffer data, int startInBytes, int endInBytes, int offsetInBytes) {
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, offsetInBytes, endInBytes - startInBytes, data.position(startInBytes));
//		OpenGLESUtils.checkErrors("glBufferSubData");
	}
	/**
	 * Creates a new data store for the previously bound buffer, deleting an existing data store if any. If this buffer is not
	 * currently bound, the behaviour is undefined and will possibly damage the state.
	 */
	public final void prepareForData(int sizeInBytes, byte accessFrequency) {
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
		// Overwrite the data store.
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, capacity = sizeInBytes, null, usage);
//		OpenGLESUtils.checkErrors("glBufferData");
		// Obtain the size of the newly created data store.
//		final int[] size = new int[1];
//		GLES20.glGetBufferParameteriv(GLES20.GL_ARRAY_BUFFER, GLES20.GL_BUFFER_SIZE, size, 0);
//		OpenGLESUtils.checkErrors("glGetBufferParameteriv");
	}
	/**
	 * Creates a new data store for the previously bound buffer, deleting an existing data store if any, and transfers the
	 * passed data to that data store. If this buffer is not currently bound, the behaviour is undefined and will possibly
	 * damage the state.
	 */
	public final void setData(java.nio.Buffer data, int startInBytes, int endInBytes, byte accessFrequency) {
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
		// Set the data (overwritig the data store).
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, capacity = (endInBytes - startInBytes), data.position(startInBytes), usage);
//		OpenGLESUtils.checkErrors("glBufferData");
	}
	/**
	 * Like {@link #setData(int, java.nio.Buffer, byte)}, but accepts an array of floats.
	 */
	public final void setData(float[] data, byte accessFrequency) {
		// Grab or create a native buffer.
		final ByteBuffer nativeBuffer;
		if (null != this.nativeBuffer && this.nativeBuffer.capacity() >= data.length * (Float.SIZE >> 3)) {
			nativeBuffer = this.nativeBuffer;
		} else /* if (null == this.nativeBuffer || this.nativeBuffer.capacity() < data.length * (Float.SIZE >> 3)) */ {
			nativeBuffer = ByteBuffer.allocateDirect(data.length * (Float.SIZE >> 3))
					.order(ByteOrder.nativeOrder());
		}
		// Fill the native buffer with the data.
		nativeBuffer.position(0);
		nativeBuffer.asFloatBuffer()
				.put(data);
		// Set the data.
		setData(nativeBuffer, 0, data.length * (Float.SIZE >> 3), accessFrequency);
		// If the access frequency suggests that this buffer (the OpenGL buffer) will be modified repeatedly, save the native
		// buffer.
		if (ACCESS_FREQUENCY_DYNAMIC == accessFrequency) {
			this.nativeBuffer = nativeBuffer;
		}
	}
	/**
	 * Unbinds the currently bound buffer (regardless of which buffer that is) from {@link GLES20#GL_ARRAY_BUFFER}.
	 */
	public static final void unbind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//		OpenGLESUtils.checkErrors("glBindBuffer");
	}
}