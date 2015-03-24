package org.ilumbo.giantsnail.opengles;

import android.opengl.GLES20;

public class DepthRenderbuffer {
	/**
	 * The name of the depth renderbuffer in OpenGL.
	 */
	public final int name;
	public DepthRenderbuffer() {
		final int[] names = new int[1];
		GLES20.glGenRenderbuffers(1, names, 0);
		name = names[0];
	}
	/**
	 * Binds the depth renderbuffer to {@link GLES20#GL_FRAMEBUFFER}.
	 */
	public final DepthRenderbuffer bind() {
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, name);
//		OpenGLESUtils.checkErrors("glBindRenderbuffer");
		return this;
	}
	/**
	 * Removes the depth renderbuffer from OpenGL.
	 */
	public final void dispose() {
		GLES20.glDeleteRenderbuffers(1, new int[]{name}, 0);
//		OpenGLESUtils.checkErrors("glDeleteRenderbuffers");
	}
	/**
	 * Creates a new data store for the previously bound depth renderbuffer, deleting an existing data store if any. If this
	 * depth renderbuffer is not currently bound, the behaviour is undefined. 
	 */
	public final void prepare(int width, int height) {
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
//		OpenGLESUtils.checkErrors("glRenderbufferStorage");
	}
	/**
	 * Unbinds the previously bound depth renderbuffer from {@link GLES20#GL_RENDERBUFFER}.
	 */
	public static final void unbind() {
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
//		OpenGLESUtils.checkErrors("glBindRenderbuffer");
	}
}