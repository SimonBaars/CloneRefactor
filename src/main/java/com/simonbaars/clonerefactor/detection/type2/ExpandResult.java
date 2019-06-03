package com.simonbaars.clonerefactor.detection.type2;

public enum ExpandResult {
	SUCCESS, /* If we can successfully add a row that is outside the threshold, because combined with the other rows the threshold is valid. */
	FAILED, /* If adding a row breaks the thresholds, thus resulting in an invalid clone class. */
	IMPOSSIBLE; /* If there are no more rows to expand to, which makes further expansion impossible. */
	
	/**
	 * Checks whether we can try to expand further, in order to have a chance to successfully expand in the future. If type 2R clones seem to be very slow, we might (for performance reasons) chance the implementation to FAILED or IMPOSSIBLE. However, this lessens the correctness of the result.
	 */
	public boolean cannotContinue() {
		return this == IMPOSSIBLE;
	}
}
