package org.ilumbo.giantsnail.opengles;

import android.opengl.GLES20;

public class Framebuffer {
	/**
	 * The status returned when the status is complete.
	 */
	public static final int STATUS_COMPLETE = 0;
	/**
	 * The status returned when not all framebuffer attachment points are framebuffer attachment complete. This means that at
	 * least one attachment point with a renderbuffer or texture attached has its attached object no longer in existence or has
	 * an attached image with a width or height of zero, or the color attachment point has a non-color-renderable image
	 * attached, or the depth attachment point has a non-depth-renderable image attached, or the stencil attachment point has a
	 * non-stencil-renderable image attached. 
	 */
	public static final int STATUS_INCOMPLETE_ATTACHMENT = STATUS_COMPLETE + 1;
	/**
	 * The status returned when not all attached images have the same width and height. 
	 */
	public static final int STATUS_INCOMPLETE_DIMENSIONS = STATUS_INCOMPLETE_ATTACHMENT + 1;
	/**
	 * The status returned when no images are attached to the framebuffer. 
	 */
	public static final int STATUS_INCOMPLETE_MISSING_ATTACHMENT = STATUS_INCOMPLETE_DIMENSIONS + 1;
	/**
	 * The status returned when the combination of internal formats of the attached images violates an implementation-dependent
	 * set of restrictions. 
	 */
	public static final int STATUS_UNSUPPORTED = STATUS_INCOMPLETE_ATTACHMENT + 1;
	/**
	 * Returned when the status is unknown.
	 */
	public static final int STATUS_UNKNOWN = Integer.MIN_VALUE;
	/**
	 * The name of the framebuffer in OpenGL.
	 */
	public final int name;
	public Framebuffer() {
		final int[] names = new int[1];
		GLES20.glGenFramebuffers(1, names, 0);
		name = names[0];
	}
	/**
	 * Attaches the passed, previously bound, texture as the colour buffer of the previously bound framebuffer.
	 */
	public final Framebuffer attach(Texture texture) {
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture.name, 0);
//		OpenGLESUtils.checkErrors("glFramebufferTexture2D");
		return this;
	}
	/**
	 * Attaches the passed depth renderbuffer as the depth buffer of the previously bound framebuffer. If the passed depth
	 * renderbuffer is not currently bound, the behaviour is uncertain. It seems to work.
	 */
	public final Framebuffer attach(DepthRenderbuffer depthRenderbuffer) {
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderbuffer.name);
//		OpenGLESUtils.checkErrors("glFramebufferRenderbuffer");
		return this;
	}
	/**
	 * Binds the framebuffer to {@link GLES20#GL_FRAMEBUFFER}.
	 */
	public final Framebuffer bind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, name);
//		OpenGLESUtils.checkErrors("glBindFramebuffer");
		return this;
	}
	/**
	 * Determines the status of the previously bound framebuffer. If this frame buffer is not currently bound, the behaviour is
	 * undefined.
	 */
	public final int determineStatus() {
		final int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
//		OpenGLESUtils.checkErrors("glCheckFramebufferStatus");
		switch (status) {
		case GLES20.GL_FRAMEBUFFER_COMPLETE:
			return STATUS_COMPLETE;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			return STATUS_INCOMPLETE_ATTACHMENT;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			return STATUS_INCOMPLETE_DIMENSIONS;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			return STATUS_INCOMPLETE_MISSING_ATTACHMENT;
		case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
			return STATUS_UNSUPPORTED;
		default:
			return STATUS_UNKNOWN;
		}
	}
	/**
	 * Removes the framebuffer from OpenGL.
	 */
	public final void dispose() {
		GLES20.glDeleteFramebuffers(1, new int[]{name}, 0);
//		OpenGLESUtils.checkErrors("glDeleteFramebuffers");
	}
	/**
	 * Binds the default framebuffer provided by the windowing system to {@link GLES20#GL_FRAMEBUFFER}.
	 */
	public static final void unbind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//		OpenGLESUtils.checkErrors("glBindFramebuffer");
	}
}