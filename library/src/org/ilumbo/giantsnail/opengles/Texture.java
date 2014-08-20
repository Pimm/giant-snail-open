package org.ilumbo.giantsnail.opengles;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	/**
	 * The height of the texture, in pixels.
	 */
	public final int height;
	/**
	 * The name of the texture in OpenGL.
	 */
	public final int name;
	/**
	 * The width of the texture, in pixels.
	 */
	public final int width;
	protected Texture(int name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}
	/**
	 * Binds the texture to GL_TEXTURE_2D.
	 */
	public final void bind() {
		// Bind the texture to GL_TEXTURE_2D.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, name);
//		OpenGLESUtils.checkErrors("glBindTexture");
	}
	protected static final int createAndBindForName(Bitmap image) {
		// Generate a name for the texture.
		final int[] names = new int[1];
		GLES20.glGenTextures(1, names, 0);
//		OpenGLESUtils.checkErrors("glGenTextures");
		// Bind the texture (for the operations below to make sense).
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, names[0]);
//		OpenGLESUtils.checkErrors("glBindTexture");
		// Define the magnification function used, if needed. Nearest FTW.
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//		OpenGLESUtils.checkErrors("glTexParameteri");
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//		OpenGLESUtils.checkErrors("glTexParameteri");
		// Define the texture image.
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
//		OpenGLESUtils.checkErrors("texImage2D");
		return names[0];
	}
	/**
	 * Creates a texture in OpenGL from the passed image, and binds it to GL_TEXTURE_2D.
	 */
	public static Texture createAndBind(Bitmap image) {
		return new Texture(createAndBindForName(image), image.getWidth(), image.getHeight());
	}
	@Override
	public String toString() {
		return new StringBuilder(64)
			.append(getClass().getSimpleName())
			.append("[name=")
			.append(name)
			.append(", width=")
			.append(width)
			.append(", height=")
			.append(height)
			.append("]")
			.toString();
	}
}