package org.ilumbo.giantsnail.patterns;

import org.ilumbo.giantsnail.mathematics.BitArray;

/**
 * A string of conditions.
 */
public final class ConditionString {
	/**
	 * A single condition, part of a condition string.
	 */
	public final class Condition {
		/**
		 * The identifier of this condition in the condition string.
		 */
		private final int identifier;
		/**
		 * Whether this condition has been met. Used internally by this condition only (not by the condition string).
		 */
		private boolean met;
		public Condition(int identifier) {
			this.identifier = identifier;
		}
		/**
		 * Returns whether this condition is met.
		 */
		public final boolean getIsMet() {
			return met;
		}
		/**
		 * Marks this condition as met.
		 *
		 * Returns whether the state of the condition string this condition is a part of changed from unmet to met because of
		 * this call.
		 */
		public final boolean meet() {
			// Do nothing if this condition was already met.
			if (met) {
				return false;
			}
			// Set the met bit to true for this condition. Return true if the met array is now equal to the true array, meaning
			// the state of the condition string changed from unmet to met.
			return trueArray == (metArray = BitArray.setBit(metArray, identifier, met = true));
		}
		/**
		 * Marks this condition as unmet.
		 *
		 * Returns whether the state of the condition string this condition is part of changed from met to unmet because of
		 * this call.
		 */
		public final boolean unmeet() {
			// Do nothing if this condition was not met.
			if (false == met) {
				return false;
			}
			// Returns true if the met array equals the true array (before the bit set below), meaning the state of the
			// condition string is now met (and will change to unmet by the bit set below).
			final boolean result = trueArray == metArray; 
			// Set the met bit to false for this condition.
			metArray = BitArray.setBit(metArray, identifier, met = false);
			return result;
		}
	}
	/**
	 * The number of conditions in this condition string.
	 */
	private int length;
	/**
	 * A bit array that defines whether the conditions in this condition string are met.
	 */
	/* package */ int metArray;
	/**
	 * A bit array with the same length as the met array, but with all true bits.
	 */
	/* package */ int trueArray;
	public ConditionString() {
	}
	/**
	 * Adds a new (initially unmet) condition to the condition string, and returns it.
	 *
	 * Note: adding a new condition might change the state of this condition string from met to unmet.
	 */
	public final Condition add() {
		// Update the true array.
		trueArray = BitArray.setBit(trueArray, length, true);
		// Create the condition.
		return this.new Condition(length++);
	}
}