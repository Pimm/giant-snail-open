package org.ilumbo.giantsnail.opengles;

import org.ilumbo.giantsnail.mathematics.POTMath;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	/**
	 * The bit that indicates that the magnification filter or wrap mode requires a power of two texture.
	 */
	private static final int POWER_OF_TWO_REQUIRED_BIT = 0x10000;
	/**
	 * A magnification function that returns the value of the texture element that is nearest (in Manhattan distance) to the
	 * centre of the pixel being textured.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST = 0x01;
	/**
	 * A magnification returns the weighted average of the four texture elements that are closest to the centre of the pixel
	 * being textured.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR = 0x02;
	/**
	 * A magnification function that chooses the mipmap that most closely matches the size of the pixel being textured and uses
	 * the {@link #MAGNIFICATION_FILTER_NEAREST} criterion to produce a texture value.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST_MIPMAP_NEAREST = 0x03 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the mipmap that most closely matches the size of the pixel being textured and uses
	 * the {@link #MAGNIFICATION_FILTER_LINEAR} criterion to produce a texture value.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR_MIPMAP_NEAREST = 0x04 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the two mipmaps that most closely match the size of the pixel being textured and
	 * uses the {@link #MAGNIFICATION_FILTER_NEAREST} criterion to produce a texture value from each mipmap. The function
	 * finally returns a weighted average of those two values.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST_MIPMAP_LINEAR = 0x05 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the two mipmaps that most closely match the size of the pixel being textured and
	 * uses the {@link #MAGNIFICATION_FILTER_LINEAR} criterion to produce a texture value from each mipmap. The function
	 * finally returns a weighted average of those two values.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR_MIPMAP_LINEAR = 0x06 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * Mask texture parameters by this to get the magnification filter.
	 */
	private static final int MAGNIFICATION_FILTER_MASK = 0xFF;
	/**
	 * A wrap mode that causes coordinates to be clamped to the range 1/2n…1-1/2n, where n is the size of the texture in the
	 * direction of clamping.
	 */
	public static final int WRAP_MODE_CLAMP_TO_EDGE = 0x0100;
	/**
	 * A wrap mode that causes the integer part of coordinates to be ignored; the GL uses only the fractional part, thereby
	 * creating a repeating pattern.
	 */
	public static final int WRAP_MODE_REPEAT = 0x0200 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A wrap mode that behaves like {@link #WRAP_MODE_REPEAT} if the integer part of a coordinate is even, and uses the
	 * inverted fractional part if the integer part of a coordinate is odd.
	 */
	public static final int WRAP_MODE_MIRRORED_REPEAT = 0x0300 | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * Mask texture parameters by this to get {@link #WRAP_MODE_CLAMP_TO_EDGE}, {@link #WRAP_MODE_MIRRORED_REPEAT} or
	 * {@link #WRAP_MODE_REPEAT}.
	 */
	private static final int WRAP_MODE_MASK = 0xFF00;
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
	protected static final int createAndBindForName(Bitmap image, int textureParameters) {
		// Generate a name for the texture.
		final int[] names = new int[1];
		GLES20.glGenTextures(1, names, 0);
//		OpenGLESUtils.checkErrors("glGenTextures");
		// Bind the texture (for the operations below to make sense).
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, names[0]);
//		OpenGLESUtils.checkErrors("glBindTexture");
		{
			// Copy over the magnification function.
			final int magnificationFunction;
			switch (textureParameters & MAGNIFICATION_FILTER_MASK) {
			case MAGNIFICATION_FILTER_NEAREST & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_NEAREST;
				break;
			case MAGNIFICATION_FILTER_LINEAR & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_LINEAR;
				break;
			case MAGNIFICATION_FILTER_NEAREST_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_NEAREST_MIPMAP_NEAREST;
				break;
			case MAGNIFICATION_FILTER_LINEAR_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_LINEAR_MIPMAP_NEAREST;
				break;
			case MAGNIFICATION_FILTER_NEAREST_MIPMAP_LINEAR & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_NEAREST_MIPMAP_LINEAR;
				break;
			case MAGNIFICATION_FILTER_LINEAR_MIPMAP_LINEAR & MAGNIFICATION_FILTER_MASK:
				magnificationFunction = GLES20.GL_LINEAR_MIPMAP_LINEAR;
				break;
			default:
				throw new IllegalArgumentException("Texture parameters do not contain a supported magnification filter");
			}
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		{
			// Copy over the wrap mode.
			final int wrapMode;
			switch (textureParameters & WRAP_MODE_MASK) {
			case WRAP_MODE_CLAMP_TO_EDGE & WRAP_MODE_MASK:
				wrapMode = GLES20.GL_CLAMP_TO_EDGE;
				break;
			case WRAP_MODE_REPEAT & WRAP_MODE_MASK:
				wrapMode = GLES20.GL_REPEAT;
				break;
			case WRAP_MODE_MIRRORED_REPEAT & WRAP_MODE_MASK:
				wrapMode = GLES20.GL_MIRRORED_REPEAT;
				break;
			default:
				throw new IllegalArgumentException("Texture parameters do not contain a supported wrap mode");
			}
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		// Define the texture image.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
//		OpenGLESUtils.checkErrors("texImage2D");
		return names[0];
	}
	/**
	 * Creates a texture in OpenGL from the passed image, and binds it to GL_TEXTURE_2D.
	 */
	public static Texture createAndBind(Bitmap image, int textureParameters) {
		final Texture result = new Texture(createAndBindForName(image, textureParameters), image.getWidth(), image.getHeight());
		if (0 != (textureParameters & POWER_OF_TWO_REQUIRED_BIT) && false == result.determineIsPot()) {
			throw new IllegalArgumentException("The passed texture parameters are not valid for non-power of two textures, unless an extension is present that removes this limitation");
		}
		return result;
	}
	/**
	 * Returns whether this texture is a power of two texture.
	 */
	protected final boolean determineIsPot() {
		return POTMath.determineIsPot(width) && POTMath.determineIsPot(height);
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
				.append(']')
				.toString();
	}
}