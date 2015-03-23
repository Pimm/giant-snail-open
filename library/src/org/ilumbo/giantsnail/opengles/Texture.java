package org.ilumbo.giantsnail.opengles;

import org.ilumbo.giantsnail.mathematics.POTMath;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	/**
	 * The bit that indicates that the magnification filter or wrap mode requires a power of two texture.
	 */
	protected static final int POWER_OF_TWO_REQUIRED_BIT = 0x20;
	/**
	 * Mask texture parameters by this to get {@link #WRAP_MODE_CLAMP_TO_EDGE}, {@link #WRAP_MODE_MIRRORED_REPEAT} or
	 * {@link #WRAP_MODE_REPEAT}.
	 */
	protected static final int WRAP_MODE_MASK = 0x3;
	protected static final int WRAP_MODE_SHIFT = 0;
	/**
	 * A wrap mode that causes coordinates to be clamped to the range 1/2n…1-1/2n, where n is the size of the texture in the
	 * direction of clamping.
	 */
	public static final int WRAP_MODE_CLAMP_TO_EDGE = 1 << WRAP_MODE_SHIFT;
	/**
	 * A wrap mode that causes the integer part of coordinates to be ignored; the GL uses only the fractional part, thereby
	 * creating a repeating pattern.
	 */
	public static final int WRAP_MODE_REPEAT = (((WRAP_MODE_CLAMP_TO_EDGE & WRAP_MODE_MASK) >> WRAP_MODE_SHIFT) + 1) << WRAP_MODE_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A wrap mode that behaves like {@link #WRAP_MODE_REPEAT} if the integer part of a coordinate is even, and uses the
	 * inverted fractional part if the integer part of a coordinate is odd.
	 */
	public static final int WRAP_MODE_MIRRORED_REPEAT = (((WRAP_MODE_REPEAT & WRAP_MODE_MASK) >> WRAP_MODE_SHIFT) + 1) << WRAP_MODE_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * Mask texture parameters by this to get the magnification filter.
	 */
	protected static final int MAGNIFICATION_FILTER_MASK = 0x1C;
	protected static final int MAGNIFICATION_FILTER_SHIFT = 2;
	/**
	 * A magnification function that returns the value of the texture element that is nearest (in Manhattan distance) to the
	 * centre of the pixel being textured.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST = 1 << MAGNIFICATION_FILTER_SHIFT;
	/**
	 * A magnification returns the weighted average of the four texture elements that are closest to the centre of the pixel
	 * being textured.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR = (((MAGNIFICATION_FILTER_NEAREST & MAGNIFICATION_FILTER_MASK) >> MAGNIFICATION_FILTER_SHIFT) + 1) << MAGNIFICATION_FILTER_SHIFT;
	/**
	 * A magnification function that chooses the mipmap that most closely matches the size of the pixel being textured and uses
	 * the {@link #MAGNIFICATION_FILTER_NEAREST} criterion to produce a texture value.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST_MIPMAP_NEAREST = (((MAGNIFICATION_FILTER_LINEAR & MAGNIFICATION_FILTER_MASK) >> MAGNIFICATION_FILTER_SHIFT) + 1) << MAGNIFICATION_FILTER_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the mipmap that most closely matches the size of the pixel being textured and uses
	 * the {@link #MAGNIFICATION_FILTER_LINEAR} criterion to produce a texture value.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR_MIPMAP_NEAREST = (((MAGNIFICATION_FILTER_NEAREST_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK) >> MAGNIFICATION_FILTER_SHIFT) + 1) << MAGNIFICATION_FILTER_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the two mipmaps that most closely match the size of the pixel being textured and
	 * uses the {@link #MAGNIFICATION_FILTER_NEAREST} criterion to produce a texture value from each mipmap. The function
	 * finally returns a weighted average of those two values.
	 */
	public static final int MAGNIFICATION_FILTER_NEAREST_MIPMAP_LINEAR = (((MAGNIFICATION_FILTER_LINEAR_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK) >> MAGNIFICATION_FILTER_SHIFT) + 1) << MAGNIFICATION_FILTER_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * A magnification function that chooses the two mipmaps that most closely match the size of the pixel being textured and
	 * uses the {@link #MAGNIFICATION_FILTER_LINEAR} criterion to produce a texture value from each mipmap. The function
	 * finally returns a weighted average of those two values.
	 */
	public static final int MAGNIFICATION_FILTER_LINEAR_MIPMAP_LINEAR = (((MAGNIFICATION_FILTER_NEAREST_MIPMAP_LINEAR & MAGNIFICATION_FILTER_MASK) >> MAGNIFICATION_FILTER_SHIFT) + 1) << MAGNIFICATION_FILTER_SHIFT | POWER_OF_TWO_REQUIRED_BIT;
	/**
	 * Mask texture parameters by this to get the format.
	 */
	protected static final int FORMAT_MASK = 0x1C0;
	protected static final int FORMAT_SHIFT = 6;
	/**
	 * A format in which each element is a single alpha component. The GL converts it to floating point and assembles it into
	 * an RGBA element by attaching 0 for red, green, and blue. Each component is then clamped to the range [0, 1].
	 */
	public static final int FORMAT_ALPHA = 1 << FORMAT_SHIFT;
	/**
	 * A format in which each element is an RGB triple. The GL converts it to floating point and assembles it into an RGBA
	 * element by attaching 1 for alpha. Each component is then clamped to the range [0, 1]. 
	 */
	public static final int FORMAT_COLOR = ((FORMAT_ALPHA >> FORMAT_SHIFT) + 1) << FORMAT_SHIFT;
	/**
	 * A format in which each element contains all four components. The GL converts it to floating point, then each component
	 * is clamped to the range [0, 1]. 
	 */
	public static final int FORMAT_COLOR_AND_ALPHA = ((FORMAT_COLOR >> FORMAT_SHIFT) + 1) << FORMAT_SHIFT;
	/**
	 * A format in which each element is a single luminance value. The GL converts it to floating point, then assembles it into
	 * an RGBA element by replicating the luminance value three times for red, green, and blue and attaching 1 for alpha. Each
	 * component is then clamped to the range [0, 1]. 
	 */
	public static final int FORMAT_LUMINANCE = ((FORMAT_COLOR_AND_ALPHA >> FORMAT_SHIFT) + 1) << FORMAT_SHIFT;
	/**
	 * A format in which each element is a luminance/alpha pair. The GL converts it to floating point, then assembles it into
	 * an RGBA element by replicating the luminance value three times for red, green, and blue. Each component is then clamped
	 * to the range [0, 1]. 
	 */
	public static final int FORMAT_LUMINANCE_AND_ALPHA = ((FORMAT_LUMINANCE >> FORMAT_SHIFT) + 1) << FORMAT_SHIFT;
	/**
	 * Mask texture parameters by this to get the type of the data.
	 */
	protected static final int TYPE_MASK = 0xE00;
	protected static final int TYPE_SHIFT = 9;
	/**
	 * A type in which each of the four components is stored in 8 bits. This type seems to work with every format.
	 */
	public static final int TYPE_32_BIT = 1 << TYPE_SHIFT;
	/**
	 * A type in which the RGB triples are stored together in 16 bits. The green component is stored in 6 bits and the other
	 * two components are stored in 5 bits each. This type works with {@link #FORMAT_COLOR}.
	 */
	public static final int TYPE_16_BIT_COLOR = ((TYPE_32_BIT >> TYPE_SHIFT) + 1) << TYPE_SHIFT;
	/**
	 * A type in which all four components are stored together in 16 bits, 4 bits each. This type works with
	 * {@link #FORMAT_COLOR_AND_ALPHA}.
	 */
	public static final int TYPE_16_BIT_COLOR_AND_ALPHA = ((TYPE_16_BIT_COLOR >> TYPE_SHIFT) + 1) << TYPE_SHIFT;
	/**
	 * A type in which all four components are stored together in 16 bits. The alpha component is stored in a single bit and
	 * the other three components are stored in 5 bits each. This type works with {@link #FORMAT_COLOR_AND_ALPHA}.
	 */
	public static final int TYPE_16_BIT_COLOR_AND_BINARY_ALPHA = ((TYPE_16_BIT_COLOR_AND_ALPHA >> TYPE_SHIFT) + 1) << TYPE_SHIFT;
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
	/**
	 * Returns whether the format and the type in the passed texture parameters are compatible.
	 */
	protected static final boolean checkFormatTypeCompatibility(int textureParameters) {
		switch (textureParameters & TYPE_MASK) {
		case TYPE_16_BIT_COLOR:
			switch (textureParameters & FORMAT_MASK) {
			case 0:
			case FORMAT_COLOR:
				return true;
			default:
				return false;
			}
		case TYPE_16_BIT_COLOR_AND_ALPHA:
		case TYPE_16_BIT_COLOR_AND_BINARY_ALPHA:
			switch (textureParameters & FORMAT_MASK) {
			case 0:
			case FORMAT_COLOR_AND_ALPHA:
				return true;
			default:
				return false;
			}
		default:
			return true;
		}
	}
	protected static final int createAndBindForName(Bitmap image, int textureParameters) {
		// Generate a name for the texture.
		final int[] names = new int[1];
		GLES20.glGenTextures(1, names, 0);
//		OpenGLESUtils.checkErrors("glGenTextures");
		// Bind the texture (for the operations below to make sense).
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, names[0]);
//		OpenGLESUtils.checkErrors("glBindTexture");
		// Get the format.
		final int format = extractFormat(textureParameters);
		// Get the type.
		final int type = extractType(textureParameters);
		{
			// Copy over the wrap mode.
			final int wrapMode = extractWrapMode(textureParameters);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		{
			// Copy over the magnification function.
			final int magnificationFunction = extractMagnificationFilter(textureParameters);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		// Fill the texture with the image. The format and type variables might be -1, which is a magic number in the
		// texImage2D method.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, format, image, type, 0);
//		OpenGLESUtils.checkErrors("texImage2D");
		return names[0];
	}
	protected static final int createAndBindForName(int width, int height, int textureParameters) {
		// Generate a name for the texture.
		final int[] names = new int[1];
		GLES20.glGenTextures(1, names, 0);
//		OpenGLESUtils.checkErrors("glGenTextures");
		// Bind the texture (for the operations below to make sense).
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, names[0]);
//		OpenGLESUtils.checkErrors("glBindTexture");
		// Get the format.
		final int format = extractFormat(textureParameters);
		if (-1 == format) {
			throw new IllegalArgumentException("Texture parameters do not contain a supported format");
		}
		// Get the type.
		final int type = extractType(textureParameters);
		if (-1 == type) {
			throw new IllegalArgumentException("Texture parameters do not contain a supported type");
		}
		{
			// Copy over the wrap mode.
			final int wrapMode = extractWrapMode(textureParameters);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapMode);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		{
			// Copy over the magnification function.
			final int magnificationFunction = extractMagnificationFilter(textureParameters);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, magnificationFunction);
//			OpenGLESUtils.checkErrors("glTexParameteri");
		}
		// Allocate texture memory.
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format, type, null);
//		OpenGLESUtils.checkErrors("glTexImage2D");
		return names[0];
	}
	/**
	 * Creates a texture in OpenGL from the passed image, and binds it to GL_TEXTURE_2D.
	 */
	public static Texture createAndBind(Bitmap image, int textureParameters) {
		if (false == checkFormatTypeCompatibility(textureParameters)) {
			throw new IllegalArgumentException("The type and format in the passed texture parameters are incompatible");
		}
		final Texture result = new Texture(createAndBindForName(image, textureParameters), image.getWidth(), image.getHeight());
		if (0 != (textureParameters & POWER_OF_TWO_REQUIRED_BIT) && false == result.determineIsPot()) {
			throw new IllegalArgumentException("The passed texture parameters are not valid for non-power of two textures, unless an extension is present that removes this limitation");
		}
		return result;
	}
	/**
	 * Creates an empty texture in OpenGL, and binds it to GL_TEXTURE_2D.
	 */
	public static Texture createAndBind(int width, int height, int textureParameters) {
		if (false == checkFormatTypeCompatibility(textureParameters)) {
			throw new IllegalArgumentException("The type and format in the passed texture parameters are incompatible");
		}
		final Texture result = new Texture(createAndBindForName(width, height, textureParameters), width, height);
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
	/**
	 * Returns the OpenGL constant that matches the format in the passed texture parameters. Returns -1 if the passed texture
	 * parameters do not contain a supported format.
	 */
	protected static final int extractFormat(int textureParameters) {
		switch (textureParameters & FORMAT_MASK) {
		case FORMAT_ALPHA:
			return GLES20.GL_ALPHA;
		case FORMAT_COLOR:
			return GLES20.GL_RGB;
		case FORMAT_COLOR_AND_ALPHA:
			return GLES20.GL_RGBA;
		case FORMAT_LUMINANCE:
			return GLES20.GL_LUMINANCE;
		case FORMAT_LUMINANCE_AND_ALPHA:
			return GLES20.GL_LUMINANCE_ALPHA;
		default:
			return -1;
		}
	}
	/**
	 * Returns the OpenGL constant that matches the magnification filter in the passed texture parameters.
	 */
	protected static final int extractMagnificationFilter(int textureParameters) {
		switch (textureParameters & MAGNIFICATION_FILTER_MASK) {
		case MAGNIFICATION_FILTER_NEAREST & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_NEAREST;
		case MAGNIFICATION_FILTER_LINEAR & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_LINEAR;
		case MAGNIFICATION_FILTER_NEAREST_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_NEAREST_MIPMAP_NEAREST;
		case MAGNIFICATION_FILTER_LINEAR_MIPMAP_NEAREST & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_LINEAR_MIPMAP_NEAREST;
		case MAGNIFICATION_FILTER_NEAREST_MIPMAP_LINEAR & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_NEAREST_MIPMAP_LINEAR;
		case MAGNIFICATION_FILTER_LINEAR_MIPMAP_LINEAR & MAGNIFICATION_FILTER_MASK:
			return GLES20.GL_LINEAR_MIPMAP_LINEAR;
		default:
			throw new IllegalArgumentException("Texture parameters do not contain a supported magnification filter");
		}
	}
	/**
	 * Returns the OpenGL constant that matches the type in the passed texture parameters. Returns -1 if the passed texture
	 * parameters do not contain a supported type.
	 */
	protected static final int extractType(int textureParameters) {
		switch (textureParameters & TYPE_MASK) {
		case TYPE_32_BIT:
			return GLES20.GL_UNSIGNED_BYTE;
		case TYPE_16_BIT_COLOR:
			return GLES20.GL_UNSIGNED_SHORT_5_6_5;
		case TYPE_16_BIT_COLOR_AND_ALPHA:
			return GLES20.GL_UNSIGNED_SHORT_4_4_4_4;
		case TYPE_16_BIT_COLOR_AND_BINARY_ALPHA:
			return GLES20.GL_UNSIGNED_SHORT_5_5_5_1;
		default:
			return -1;
		}
	}
	/**
	 * Returns the OpenGL constant that matches the wrap mode in the passed texture parameters.
	 */
	protected static final int extractWrapMode(int textureParameters) {
		switch (textureParameters & WRAP_MODE_MASK) {
		case WRAP_MODE_CLAMP_TO_EDGE & WRAP_MODE_MASK:
			return GLES20.GL_CLAMP_TO_EDGE;
		case WRAP_MODE_REPEAT & WRAP_MODE_MASK:
			return GLES20.GL_REPEAT;
		case WRAP_MODE_MIRRORED_REPEAT & WRAP_MODE_MASK:
			return GLES20.GL_MIRRORED_REPEAT;
		default:
			throw new IllegalArgumentException("Texture parameters do not contain a supported wrap mode");
		}
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