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
	 * The name of the shader object in OpenGL.
	 */
	public final int name;
	protected Shader(int name) {
		this.name = name;
	}
	/**
	 * Removes the shader object from OpenGL.
	 */
	public final void dispose() {
		// Remove the shader object from OpenGL.
		GLES20.glDeleteShader(name);
//		OpenGLESUtils.checkErrors("glDeleteShader");
	}
	protected static final int compileForName(int type, String source) {
		// Create an (empty) shader object in OpenGL.
		final int name = GLES20.glCreateShader(type);
//		OpenGLESUtils.checkErrors("glCreateShader");
		// Add the source to the newly created shader object.
		GLES20.glShaderSource(name, source);
//		OpenGLESUtils.checkErrors("glShaderSource");
		// Compile the shader object.
		GLES20.glCompileShader(name);
//		OpenGLESUtils.checkErrors("glCompileShader");
		// Get the compile status.
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(name, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if (GLES20.GL_FALSE == compileStatus[0]) {
			// Grab the information log.
			final String informationLog = GLES20.glGetShaderInfoLog(name);
			// Remove the shader object from OpenGL.
			GLES20.glDeleteShader(name);
//			OpenGLESUtils.checkErrors("glDeleteShader");
			// Log and throw an exception.
			final StringBuilder messageBuilder = new StringBuilder(256)
					.append("GL_COMPILE_STATUS is GL_FALSE (type is ");
			switch (type) {
			case GLES20.GL_VERTEX_SHADER:
				messageBuilder.append("GL_VERTEX_SHADER");
				break;
			case GLES20.GL_FRAGMENT_SHADER:
				messageBuilder.append("GL_FRAGMENT_SHADER");
				break;
			default:
				messageBuilder.append("unknown");
				break;
			}
			messageBuilder.append(").");
			if (0 != informationLog.length()) {
				messageBuilder.append('\n')
						.append(informationLog);
			}
			final String message = messageBuilder.toString();
			android.util.Log.e(Shader.class.getSimpleName(), message);
			throw new ShaderCompileException(message);
		}
		return name;
	}
	/**
	 * Compiles and loads a shader object into OpenGL. The type argument must be GLES20.GL_VERTEX_SHADER or
	 * GLES20.GL_FRAGMENT_SHADER.
	 */
	public static Shader compile(int type, String source) {
		return new Shader(compileForName(type, source));
	}
}