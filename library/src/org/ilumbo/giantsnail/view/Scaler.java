package org.ilumbo.giantsnail.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * An object that scales a bitmap, and then draws it onto a canvas. Both the behaviour and implementation are similar to
 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean)}.
 *
 * Note that the scaler has internal state, which mutates. Behaviour is undefined when a single scaler is used by multpile
 * threads simultaneously.
 */
public final class Scaler {
	/**
	 * The matrix used to scale bitmaps.
	 */
	private final Matrix scaleMatrix;
	/**
	 * Array used to set the values of the matrix above.
	 */
	private final float[] scaleMatrixValues;
	/**
	 * The paint used to scale bitmaps.
	 */
	private final Paint scalePaint;
	public Scaler(boolean filter) {
		scaleMatrix = new Matrix();
		(scaleMatrixValues = new float[9])
				[8] = 1;
		(scalePaint = new Paint())
				.setFilterBitmap(filter);
	}
	/**
	 * Scales the passed bitmap by the passed scale factor, and then draws it to the passed canvas at the passed coordinates.
	 * The passed bitmap is unaltered.
	 */
	public final void scaleAndDraw(Bitmap bitmap, float scaleFactor,
			float left, float top, Canvas canvas) {
		scaleAndDraw(bitmap, scaleFactor, scaleFactor, left, top, canvas);
	}
	/**
	 * Scales the passed bitmap by the passed scale factors, and then draws it to the passed canvas at the passed coordinates.
	 * The passed bitmap is unaltered.
	 */
	public final void scaleAndDraw(Bitmap bitmap, float scaleFactorWidth, float scaleFactorHeight,
			float left, float top, Canvas canvas) {
		if (1 != scaleFactorWidth || 1 != scaleFactorHeight) {
			// Update the matrix, for scaling.
			scaleMatrixValues[0] = scaleFactorWidth;
			scaleMatrixValues[2] = left;
			scaleMatrixValues[4] = scaleFactorHeight;
			scaleMatrixValues[5] = top;
			scaleMatrix.setValues(scaleMatrixValues);
			// Draw.
			canvas.drawBitmap(bitmap, scaleMatrix, scalePaint);
		// Don't apply any matrix stuff if the width and height scale factors are 1.
		} else /* if (1 == scaleFactorWidth && 1 == scaleFactorHeight) */ {
			canvas.drawBitmap(bitmap, left, top, null);
		}
	}
	/**
	 * Scales the passed bitmap by the passed scale factor, and then draws it to the passed canvas. The passed bitmap is
	 * unaltered.
	 */
	public final void scaleAndDraw(Bitmap bitmap, float scaleFactor, Canvas canvas) {
		scaleAndDraw(bitmap, scaleFactor, scaleFactor, canvas);
	}
	/**
	 * Scales the passed bitmap by the passed scale factors, and then draws it to the passed canvas. The passed bitmap is
	 * unaltered.
	 */
	public final void scaleAndDraw(Bitmap bitmap, float scaleFactorWidth, float scaleFactorHeight, Canvas canvas) {
		if (1 != scaleFactorWidth || 1 != scaleFactorHeight) {
			// Update the matrix, for scaling.
			scaleMatrixValues[0] = scaleFactorWidth;
			scaleMatrixValues[4] = scaleFactorHeight;
			scaleMatrixValues[2] =
					scaleMatrixValues[5] = 0;
			scaleMatrix.setValues(scaleMatrixValues);
			// Draw.
			canvas.drawBitmap(bitmap, scaleMatrix, scalePaint);
		// Don't apply any matrix stuff if the width and height scale factors are 1.
		} else /* if (1 == scaleFactorWidth && 1 == scaleFactorHeight) */ {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
	}
}