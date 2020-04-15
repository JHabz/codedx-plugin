package com.secdec.codedx.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Code Dx version number, disregarding tail matter like "RC" and "SNAPSHOT".
 * The constructor is private; use the static <code>fromString</code> method instead.
 * Instances are available for reference to point out min/max versions supporting certain
 * features that might necessitate the Jenkins plugin to behave differently.
 */
public final class CodeDxVersion implements Comparable<CodeDxVersion> {

	/**
	 * Version when the "New" triage status was removed in Code Dx (2.4.0).
	 * At this time, filters intending to use the "New" status would instead use
	 * the "First Seen" filter, using some threshold date to categorize "new" findings.
	 */
	public final static CodeDxVersion NEW_STATUS_REMOVED = fromString("2.4.0");

	/**
	 * Version when the "New" triage status was returned to Code Dx (2.4.2).
	 * Several clients were adversely affected by the removal, and requested its return.
	 * Versions <em>before</em> (exclusive) this version, and <em>starting from</em> (inclusive)
	 * the <code>NEW_STATUS_REMOVED</code> version must use the "First Seen" filter
	 * in order to emulate the behavior of the "triage status = New" filter.
	 */
	public final static CodeDxVersion NEW_STATUS_RETURNED = fromString("2.4.2");

	/**
	 * Version when the "Reopened" status was introduced.
	 */
	public final static CodeDxVersion REOPENED_STATUS_ADDED = fromString("4.1.0");

	/** First version that supports the "analysis names" feature. */
	public final static CodeDxVersion MIN_FOR_ANALYSIS_NAMES = fromString("2.4.0");

	public static CodeDxVersion fromString(String version){
		// format is expected to be "x(.y)*-abc", and we want the x.y.z part
		Pattern versionRegex = Pattern.compile("^(\\d+(?:\\.\\d+)*).*");
		Matcher versionMatcher = versionRegex.matcher(version);
		if(versionMatcher.matches()){
			String matchedNumbers = versionMatcher.group(1);
			String[] rawNumbers = matchedNumbers.split("\\.");
			int[] numbers = new int[rawNumbers.length];
			for(int i=0; i<rawNumbers.length; ++i){
				numbers[i] = Integer.valueOf(rawNumbers[i]);
			}
			return new CodeDxVersion(numbers);
		} else {
			throw new IllegalArgumentException("for input string \"" + version + "\"");
		}
	}

	private final int[] numbers;

	private CodeDxVersion(int[] numbers){
		this.numbers = numbers;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<numbers.length; ++i){
			if(i > 0) sb.append('.');
			sb.append(numbers[i]);
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object thatObj) {
		if(thatObj instanceof CodeDxVersion){
			CodeDxVersion that = (CodeDxVersion) thatObj;
			return compareTo(that) == 0;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public int compareTo(CodeDxVersion that) {
		int index = 0;
		int limit = Math.max(this.numbers.length, that.numbers.length);
		do {
			int nThis = index < this.numbers.length ? this.numbers[index] : 0;
			int nThat = index < that.numbers.length ? that.numbers[index] : 0;
			if(nThis != nThat) return nThis - nThat;
			++index;
		} while(index < limit);
		return 0;
	}

	/**
	 * Convenience method that determines if this version of Code Dx supports the "New" triage status.
	 * Logically, this method checks that <code>this &lt; NEW_STATUS_REMOVED || this &gt;= NEW_STATUS_RETURNED</code>
	 * @return
	 */
	public boolean supportsTriageNew(){
		// (version < NEW_STATUS_REMOVED) || (version >= NEW_STATUS_RETURNED)
		return (this.compareTo(NEW_STATUS_REMOVED) < 0) || (this.compareTo(NEW_STATUS_RETURNED) >= 0);
	}

	public boolean supportsTriageReopened() {
		return this.compareTo(REOPENED_STATUS_ADDED) >= 0;
	}
}
