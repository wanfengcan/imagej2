/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2013 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.data.types;


import net.imglib2.type.numeric.NumericType;

/**
 * GeneralCast supports data conversion logic between variables of differing
 * {@link DataType}s.
 * 
 * @author Barry DeZonia
 */
public class GeneralCast {

	/**
	 * Fills an output with a cast from an input given information about their
	 * DataTypes. This version of cast() can throw IllegalArgumentException if it
	 * can't find a safe cast. Use the alternate version of cast() that takes a
	 * temporary working variable for fully safe casting.
	 * 
	 * @param inputType The DataType of the input.
	 * @param input The input variable to cast from.
	 * @param outputType The DataType of the output
	 * @param output The output variable to cast into.
	 */
	public static <U extends NumericType<U>, V extends NumericType<V>> void cast(
		DataType<U> inputType, U input, DataType<V> outputType, V output)
	{
		cast(inputType, input, outputType, output, null);
	}

	/**
	 * Fills an output with a cast from an input given information about their
	 * DataTypes. This version always succeeds. It requires a temporary working
	 * variable of type BigComplex to be passed in.
	 * 
	 * @param inputType The DataType of the input.
	 * @param input The input variable to cast from.
	 * @param outputType The DataType of the output
	 * @param output The output variable to cast into.
	 * @param tmp The working variable the method may use internally.
	 */
	public static <U extends NumericType<U>, V extends NumericType<V>> void cast(
		DataType<U> inputType, U input, DataType<V> outputType, V output, BigComplex tmp)
	{
		// Only do general casts when data types are unbounded or are outside
		// Double or Long precisions. Otherwise use primitives to avoid tons of
		// Object overhead.

		if (inputType.hasLongRepresentation() && outputType.hasLongRepresentation())
		{
			long val = inputType.asLong(input);
			outputType.setLong(output, val);
		}
		else if (inputType.hasDoubleRepresentation() &&
			outputType.hasDoubleRepresentation())
		{
			double val = inputType.asDouble(input);
			outputType.setDouble(output, val);
		}
		else if (inputType.hasLongRepresentation() &&
			outputType.hasDoubleRepresentation())
		{
			long val = inputType.asLong(input);
			outputType.setDouble(output, val);
		}
		else if (inputType.hasDoubleRepresentation() &&
			outputType.hasLongRepresentation())
		{
			double val = inputType.asDouble(input);
			outputType.setLong(output, (long) val);
		}

		if (tmp == null) {
			throw new IllegalArgumentException("Could not find a suitable cast. "
				+ "Pass a temporary to the alternate version of cast().");
		}

		// fall thru to simplest slowest approach: usually for complex numbers

		inputType.cast(input, tmp);
		outputType.cast(tmp, output);
	}
}