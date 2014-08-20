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
	 * Returns the index of the generic vertex attribute that is bound to that attribute variable.
	 */
	public final int getAttributeLocation(String name) {
		// Get the index of the attribute variable.
		final int result = GLES20.glGetAttribLocation(this.name, name);
//		OpenGLESUtils.checkErrors("glGetAttribLocation");
		return result;
	}
	/**
	 * Returns an integer that represents the location of a specific uniform variable.
	 */
	public final int getUniformLocation(String name) {
		// Get the index of the uniform variable.
		final int result = GLES20.glGetUniformLocation(this.name, name);
//		OpenGLESUtils.checkErrors("glGetUniformLocation");
		return result;
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
			final String informationLog = GLES20.glGetShaderInfoLog(name);
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
			android.util.Log.e("OpenGL", message);
			throw new ProgramLinkException(message);
		}
		return name;
	}
	public static Program link(Shader firstShader, Shader secondShader) {
		return new Program(linkForName(firstShader, secondShader));
	}
	/**
	 * Installs the program object as part of current OpenGL rendering state.
	 */
	public final Program use() {
		// Install the program object as part of the current rendering state.
		GLES20.glUseProgram(name);
//		OpenGLESUtils.checkErrors("glUseProgram");
		return this;
	}
}