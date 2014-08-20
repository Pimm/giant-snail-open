package org.ilumbo.giantsnail.opengles;

public final class OpenGLESErrorException extends RuntimeException {
	private static final long serialVersionUID = 3226741585779880921L;
	public OpenGLESErrorException(int errorCode, String operation) {
		super(operation + ": " + convertErrorCodeToMessage(errorCode));
	}
	private static final String convertErrorCodeToMessage(int errorCode) {
		switch (errorCode) {
		case 0x0500:
			return "GL_INVALID_ENUM​";
		case 0x0501:
			return "GL_INVALID_VALUE";
		case 0x0502:
			return "GL_INVALID_OPERATION";
		case 0x0503:
			return "GL_STACK_OVERFLOW​";
		case 0x0504:
			return "GL_STACK_UNDERFLOW​";
		case 0x0505:
			return "GL_OUT_OF_MEMORY​";
		case 0x0506:
			return "GL_INVALID_FRAMEBUFFER_OPERATION​";
		default:
			return "(unknown)";
		}
	}
}