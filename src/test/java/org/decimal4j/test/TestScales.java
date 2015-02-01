package org.decimal4j.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

public enum TestScales {
	/** Run unit test with all scales */
	ALL(Scales.VALUES),
	/** Run unit test with standard scales: 0, 6, 9, 17, 18 */
	STANDARD(Arrays.asList(Scales.valueOf(0), Scales.valueOf(6), Scales.valueOf(9), Scales.valueOf(17), Scales.valueOf(18))),
	/** Run unit test with small set of scales: 0, 6, 9, 18 */
	SMALL(Arrays.asList(Scales.valueOf(0), Scales.valueOf(6), Scales.valueOf(9), Scales.valueOf(17), Scales.valueOf(18))),
	/** Run unit test with tiny set of scales: 0, 9, 18 */
	TINY(Arrays.asList(Scales.valueOf(0), Scales.valueOf(6), Scales.valueOf(9), Scales.valueOf(17), Scales.valueOf(18)));
	private final List<ScaleMetrics> scales;

	private TestScales(List<ScaleMetrics> scales) {
		this.scales = Collections.unmodifiableList(scales);
	}

	public List<ScaleMetrics> getScales() {
		return scales;
	}
}