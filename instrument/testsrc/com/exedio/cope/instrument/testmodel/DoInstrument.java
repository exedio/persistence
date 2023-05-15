/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.testmodel.DoInstrument.NotInterim.x;
import static com.exedio.cope.instrument.testmodel.DontInstrument.wouldNotCompileInInterimCode;

import com.exedio.cope.instrument.WrapInterim;


/**
 * This class has a static import for {@link DontInstrument}.
 */
@WrapInterim
@SuppressWarnings("unused") // OK: test bad API usage
class DoInstrument implements InterfaceThatUsesDontInstrument
{
	static final long useStaticImport = wouldNotCompileInInterimCode;

	static final int notInterim = x();

	static final String constantString = "a" + "b\tx";
	static final String constantStringQuotes = "\"'";
	static final byte constantByte = Byte.MAX_VALUE;
	static final byte constantByteNeg = Byte.MIN_VALUE;
	static final short constantShort = Short.MAX_VALUE;
	static final short constantShortNeg = Short.MIN_VALUE;
	static final int constantInt = Integer.MAX_VALUE;
	static final int constantIntNeg = Integer.MIN_VALUE;
	static final long constantLong = Long.MAX_VALUE;
	static final long constantLongNeg = Long.MIN_VALUE;
	static final float constantFloat = 1.0f+1.0f;
	static final float constantFloatNeg = -1.0f-1.0f;
	static final float constantFloatNaN = Float.NaN;
	static final float constantFloatInfinity = Float.POSITIVE_INFINITY;
	static final float constantFloatInfinityNeg = Float.NEGATIVE_INFINITY;
	static final double constantDouble = 2.2d+3.3d;
	static final double constantDoubleNeg = -2.2d-3.3d;
	static final double constantDoubleNaN = Double.NaN;
	static final double constantDoubleInfinity = Double.POSITIVE_INFINITY;
	static final double constantDoubleInfinityNeg = Double.NEGATIVE_INFINITY;
	@SuppressWarnings("PointlessBooleanExpression")
	static final boolean constantBoolean = !true;
	static final char constantChar = 'a'+1;
	static final char constantCharDoubleQuote = '"';
	static final char constantCharQuote = '\'';
	@SuppressWarnings("HardcodedLineSeparator")
	static final char constantCharNewline = '\n';


	enum NotInterim
	{
		a, b;

		static final int x()
		{
			return 42;
		}
	}

	@WrapInterim
	DoInstrument()
	{

	}

	@Override
	public void method(final DontInstrument param)
	{
		// empty
	}

	@WrapInterim
	void doSomething(@AnnotationNotInInterim final int param)
	{
		// empty
	}
}
