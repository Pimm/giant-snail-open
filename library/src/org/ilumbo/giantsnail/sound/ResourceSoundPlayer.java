package org.ilumbo.giantsnail.sound;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * Plays sound resources. This class actually decodes the sound resource and even creates a new {@link MediaPlayer} instance
 * every time it is used.
 *
 * Use this class if a sound has to be played on an incidental basis only. If the sound has to be played more often, consider
 * creating a {@link MediaPlayer} that is re-used or creating a {@link SoundPool}.
 */
public final class ResourceSoundPlayer {
	/**
	 * Releases the media player when the sound has been played completely.
	 */
	private static final class ReleasingOnCompletionListener implements OnCompletionListener {
		@Override
		public final void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.release();
		}
	}
	/**
	 * Plays the sound resource with the passed resource identifier once. Returns true if the sound is now playing, and false
	 * if the sound could not be played. Reasons for a sound being unplayable include no resource with the passed resource
	 * identifier existing and the resource not being decodable into a sound (unsupported format).
	 *
	 * Use this method if the sound has to be played on an incidental basis only.
	 */
	public static final boolean playResourceSound(Resources resources, int resourceIdentifier, int audioStreamType) {
		// Open an (asset) file descriptor for the sound resource.
		final AssetFileDescriptor soundAssetFileDescriptior;
		try {
			soundAssetFileDescriptior = resources.openRawResourceFd(resourceIdentifier);
		} catch (NotFoundException exception) {
			// Return prematurely if the resource could not be found.
			return false;
		}
		// According to the documetation, openRawResourceFd might return null. Return prematurely if so.
		if (null == soundAssetFileDescriptior) {
			return false;
		}
		// Create the media player, and set the data source.
		final MediaPlayer mediaPlayer = new MediaPlayer();
		boolean dataSourceSuccesfullySet = true;
		try {
			mediaPlayer.setDataSource(soundAssetFileDescriptior.getFileDescriptor(),
					soundAssetFileDescriptior.getStartOffset(), soundAssetFileDescriptior.getLength());
		} catch (IOException exception) {
			dataSourceSuccesfullySet = false;
		}
		// Close the (asset) file descriptor for the sound resource.
		try {
			soundAssetFileDescriptior.close();
		} catch (IOException exception) {
			// In the unlikely event that the file descriptor cannot be closed, proceed as usual. It sucks, but it is not
			// fatal.
		}
		// If the data source could not be set, release the media player and return prematurely.
		if (false == dataSourceSuccesfullySet) {
			mediaPlayer.release();
			return false;
		}
		// Set the audio stream type.
		mediaPlayer.setAudioStreamType(audioStreamType);
		// Prepare the media player (synchronously).
		try {
			mediaPlayer.prepare();
		} catch (IOException exception) {
			// If the media player could not be prepared, release it and return prematurely.
			mediaPlayer.release();
			return false;
		}
		// Attach a releasing on completion listener.
		mediaPlayer.setOnCompletionListener(new ReleasingOnCompletionListener());
		// Start playing the sound.
		mediaPlayer.start();
		return true;
	}
}