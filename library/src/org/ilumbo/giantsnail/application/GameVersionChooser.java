package org.ilumbo.giantsnail.application;

import java.util.Random;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

/**
 * A chooser of game versions, if your game has multiple versions (like Nintendogs, and Pokémon).
 *
 * When the user creates a (save)game, this class can choose the version of that game. This class uses
 * {@link Secure#ANDROID_ID}, causing two games created by the same user to have the same version. This follows the rules of
 * Pokémon: no matter how many times you choose "New Game" on the same Pokémon cartridge, the version you are playing will
 * always be "Red", or always be "Blue", or always be "Sapphire".
 *
 * If the device has multiple users, different users could create (save)games with different versions.
 *
 * If your game is able to import (save)games from the cloud or elsewhere, the version should be bound to those games. It would
 * be odd if a user creates a new game and happily plays "Red", then continues playing on another device and is suddenly
 * playing "Blue".
 */
public final class GameVersionChooser {
	/**
	 * Blue version of the game. Perhaps you should make Sandshrew exclusive to this version.
	 */
	public static final int BLUE = 3;
	/**
	 * Green version of the game. Players can catch Bellsprout on this one.
	 */
	public static final int GREEN = 1;
	/**
	 * Red version of the game. Perhaps you should make Oddish exclusive to this version.
	 */
	public static final int RED = 0;
	/**
	 * Yellow version of the game.
	 */
	public static final int YELLOW = 2;
	/**
	 * Returns the version of (save)games that are created. The passed version count can be 2, 3 or 4. Returns
	 * {@link GameVersionChooser#RED} or {@link GameVersionChooser#GREEN} if the version count is 2. Returns one of those two
	 * or {@link GameVersionChooser#YELLOW} if the version count is 3. Returns one of those three or
	 * {@link GameVersionChooser#BLUE} if the version count is 4. Throws an exception if the version count is any other value.
	 */
	public final static int chooseGameVersion(ContextWrapper contextWrapper, int versionCount) throws IllegalArgumentException {
		int result;
		// Retrieve the Android identifier, randomly generated on the device's first boot.
		final String androidIdentifier = Secure.getString(contextWrapper.getContentResolver(), Secure.ANDROID_ID);
		// If the Android identifier is ready to go, use it. If not, use use the backup method using preferences. The
		// preference-based method is less cool than the normal one, because preferences can be easily removed by the user.
		if (null != androidIdentifier) {
			try {
				result = chooseGameVersionByAndroidIdentifier(androidIdentifier, versionCount);
			} catch (NumberFormatException exception) {
				result = chooseGameVersionByPreferences(contextWrapper, versionCount);
			}
		} else {
			result = chooseGameVersionByPreferences(contextWrapper, versionCount);
		}
		return result;
	}
	/**
	 * Returns the version of (save)games that are created based on the passed Android identifier.
	 */
	private final static int chooseGameVersionByAndroidIdentifier(String androidIdentifier, int versionCount) throws NumberFormatException, IllegalArgumentException {
		// Parse the last part of the Android identifier.
		final int parsedAndroidIdentifier = Integer.parseInt(androidIdentifier.substring(androidIdentifier.length() - 4), 0x10);
		// Choose a version based on the parsing result.
		switch (versionCount) {
		case 2:
			return parsedAndroidIdentifier & 1;
		case 3:
			return parsedAndroidIdentifier % 3;
		case 4:
			return parsedAndroidIdentifier & 3;
		default:
			throw createUnexpectedVersionCountException(versionCount);
		}
	}
	/**
	 * Returns the version of (save)games that are created based on preferences. Returns the version saved in the preferences,
	 * or returns a randomly selected version after storing that into the preferences.
	 */
	private final static int chooseGameVersionByPreferences(Context context, int versionCount) throws IllegalArgumentException {
		// Throw an exception if the version count is unexpected.
		if (versionCount < 2 || versionCount > 4) {
			throw createUnexpectedVersionCountException(versionCount);
		}
		// Grab the preferences.
		final SharedPreferences preferences = context.getSharedPreferences("version", Context.MODE_PRIVATE);
		// Check whether a version has been chosen for the passed version count earlier.
		int result = preferences.getInt(Integer.toString(versionCount), Integer.MIN_VALUE);
		// If no version has been chosen yet for the passed version count, choose one now. Store the chosen version in the
		// preferences.
		if (Integer.MIN_VALUE == result) {
			preferences.edit()
					.putInt(Integer.toString(versionCount), result = new Random().nextInt(versionCount))
					.commit();
		}
		return result;
	}
	private final static IllegalArgumentException createUnexpectedVersionCountException(int unexpectedValue) {
		return new IllegalArgumentException("Unexpected version count " + Integer.toString(unexpectedValue));
	}
}