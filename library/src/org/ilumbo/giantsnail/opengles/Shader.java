package org.ilumbo.giantsnail.opengles;

import android.opengl.GLES20;

public class Shader {
	protected static final class ShaderCompileException extends RuntimeException {
		private static final long serialVersionUID = 0x43F0002351C9DF2Fl;
		public ShaderCompileException(String message) {
			super(message);
		}
	}
	/**
	 * The type of a shader that is intended to run on the programmable vertex processor.
	 */
	public static final boolean TYPE_VERTEX = false;
	/**
	 * The type of a shader that is intended to run on the programmable fragment processor.
	 */
	public static final boolean TYPE_FRAGMENT = true;
	/**
	 * The name of the shader object in OpenGL.
	 */
	public final int name;
	protected Shader(int name) {
		this.name = name;
	}
	/**
	 * Removes the shader from OpenGL.
	 */
	public final void dispose() {
		GLES20.glDeleteShader(name);
//		OpenGLESUtils.checkErrors("glDeleteShader");
	}
	protected static int compileForName(String source, boolean type) {
		// Create an (empty) shader in OpenGL.
		final int name = GLES20.glCreateShader(TYPE_VERTEX == type ? GLES20.GL_VERTEX_SHADER : GLES20.GL_FRAGMENT_SHADER);
//		OpenGLESUtils.checkErrors("glCreateShader");
		// Add the source to the newly created shader.
		GLES20.glShaderSource(name, source);
//		OpenGLESUtils.checkErrors("glShaderSource");
		// Compile the shader.
		GLES20.glCompileShader(name);
//		OpenGLESUtils.checkErrors("glCompileShader");
		// Get the compile status.
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(name, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if (GLES20.GL_FALSE == compileStatus[0]) {
			// Grab the information log.
			final String informationLog = GLES20.glGetShaderInfoLog(name);
			// Remove the shader from OpenGL.
			GLES20.glDeleteShader(name);
//			OpenGLESUtils.checkErrors("glDeleteShader");
			// Log and throw an exception.
			final StringBuilder messageBuilder = new StringBuilder(256)
					.append("GL_COMPILE_STATUS is GL_FALSE (type is ");
			if (TYPE_VERTEX == type) {
				messageBuilder.append("TYPE_VERTEX");
			} else /* if (TYPE_FRAGMENT == type) */ {
				messageBuilder.append("TYPE_FRAGMENT");
			}
			messageBuilder.append(')');
			if (0 != informationLog.length()) {
				messageBuilder.append('\n')
						.append(informationLog);
			}
			throw new ShaderCompileException(messageBuilder.toString());
		}
		return name;
	}
	/**
	 * Compiles and loads a shader into OpenGL.
	 */
	public static Shader compile(String source, boolean type) {
		return new Shader(compileForName(source, type));
	}
}