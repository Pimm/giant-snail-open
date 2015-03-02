package org.ilumbo.giantsnail.opengles;

import android.opengl.GLES20;

public class Program {
	protected static final class ProgramLinkException extends RuntimeException {
		private static final long serialVersionUID = 0x76F8D291422AC3E5l;
		public ProgramLinkException(String message) {
			super(message);
		}
	}
	/**
	 * The name of the program object in OpenGL.
	 */
	public final int name;
	protected Program(int name) {
		this.name = name;
	}
	/**
	 * Removes the program object from OpenGL.
	 */
	public final void dispose() {
		// Remove the program object from OpenGL.
		GLES20.glDeleteProgram(name);
//		OpenGLESUtils.checkErrors("glDeleteProgram");
	}
	/**
	 * Returns the index of the generic vertex attribute that is bound to that attribute variable. Returns
	 * {@link Integer#MIN_VALUE} if the named attribute variable is not an active attribute.
	 */
	public final int getAttributeLocation(String name) {
		// Get the index of the attribute variable.
		final int result = GLES20.glGetAttribLocation(this.name, name);
//		OpenGLESUtils.checkErrors("glGetAttribLocation");
		return -1 == result ? Integer.MIN_VALUE : result;
	}
	/**
	 * Returns the index of the generic vertex attribute that is bound to that attribute variable. Returns
	 * {@link Integer#MIN_VALUE} if the named attribute variable is not an active attribute.
	 */
	public final int getAttributeLocation(char name) {
		// Get the index of the attribute variable.
		final int result = GLES20.glGetAttribLocation(this.name, Character.toString(name));
//		OpenGLESUtils.checkErrors("glGetAttribLocation");
		return -1 == result ? Integer.MIN_VALUE : result;
	}
	/**
	 * Returns an integer that represents the location of a specific uniform variable. Returns {@link Integer#MIN_VALUE} if the
	 * passed name does not correspond to an active uniform variable.
	 */
	public final int getUniformLocation(String name) {
		// Get the index of the uniform variable.
		final int result = GLES20.glGetUniformLocation(this.name, name);
//		OpenGLESUtils.checkErrors("glGetUniformLocation");
		return -1 == result ? Integer.MIN_VALUE : result;
	}
	/**
	 * Returns an integer that represents the location of a specific uniform variable. Returns {@link Integer#MIN_VALUE} if the
	 * passed name does not correspond to an active uniform variable.
	 */
	public final int getUniformLocation(char name) {
		// Get the index of the uniform variable.
		final int result = GLES20.glGetUniformLocation(this.name, Character.toString(name));
//		OpenGLESUtils.checkErrors("glGetUniformLocation");
		return -1 == result ? Integer.MIN_VALUE : result;
	}
	protected static final int linkForName(Shader firstShader, Shader secondShader) {
		// Create an (empty) program object in OpenGL.
		final int name = GLES20.glCreateProgram();
//		OpenGLESUtils.checkErrors("glCreateProgram");
		// Attach the passed shader objects to the newly created program object.
		GLES20.glAttachShader(name, firstShader.name);
//		OpenGLESUtils.checkErrors("glAttachShader");
		GLES20.glAttachShader(name, secondShader.name);
//		OpenGLESUtils.checkErrors("glAttachShader");
		// Link the program object.
		GLES20.glLinkProgram(name);
//		OpenGLESUtils.checkErrors("glLinkProgram");
		// Get the link status.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(name, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (GLES20.GL_FALSE == linkStatus[0]) {
			// Grab the information log.
			final String informationLog = GLES20.glGetProgramInfoLog(name);
			// Remove the program object from OpenGL.
			GLES20.glDeleteProgram(name);
//			OpenGLESUtils.checkErrors("glDeleteProgram");
			// Log and throw an exception.
			final String message;
			if (0 == informationLog.length()) {
				message = "GL_LINK_STATUS is GL_FALSE";
			} else /* if (0 != informationLog.length()) */ {
				message = new StringBuilder(128)
						.append("GL_LINK_STATUS is GL_FALSE")
						.append('\n')
						.append(informationLog)
						.toString();
			}
			android.util.Log.e(Program.class.getSimpleName(), message);
			throw new ProgramLinkException(message);
		}
		return name;
	}
	public static Program link(Shader firstShader, Shader secondShader) {
		return new Program(linkForName(firstShader, secondShader));
	}
	/**
	 * Uninstalls the current program (regardless of which program that is). The behaviour of methods such as
	 * {@link GLES20#glDrawArrays(int, int, int)} is undefined after calling this method.
	 */
	public static final void unuse() {
		GLES20.glUseProgram(0);
		OpenGLESUtils.checkErrors("glUseProgram");
	}
	/**
	 * Installs the program object as part of current OpenGL rendering state.
	 */
	public final Program use() {
		GLES20.glUseProgram(name);
		OpenGLESUtils.checkErrors("glUseProgram");
		return this;
	}
}