/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.jmh.state;

import java.math.MathContext;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.TruncationPolicy;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
abstract public class AbstractBenchmarkState {
	//		@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" })
	//		@Param({ "0", "6", "9", "17", "18" })
	@Param({ "0", "6", "17" })
	public int scale;

	public RoundingMode roundingMode;
	public DecimalArithmetic arithmetic;
	public DecimalFactory<?> factory;
	public MathContext mcLong64;
	public MathContext mcLong128;

	//for checked benchmarks only
	public TruncationPolicy checkedTruncationPolicy;
	public DecimalArithmetic checkedArithmetic;

	protected void init(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		this.arithmetic = Scales.getScaleMetrics(scale).getArithmetic(roundingMode);
		this.factory = Factories.getDecimalFactory(scale);
		this.checkedTruncationPolicy = CheckedRounding.valueOf(roundingMode);
		this.checkedArithmetic = Scales.getScaleMetrics(scale).getArithmetic(checkedTruncationPolicy);
		this.mcLong64 = new MathContext(19, roundingMode);
		this.mcLong128 = new MathContext(39, roundingMode);
	}
}