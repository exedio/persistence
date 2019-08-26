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

package com.exedio.cope.pattern;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

final class MoneyAmountUtil
{
	static final class Cy implements Money.Currency, Serializable
	{
		@Override
		public int hashCode()
		{
			return System.identityHashCode(this); // OK: singleton
		}
		@Override
		public boolean equals(final Object other)
		{
			assertSame(CY, this);
			if(other instanceof Cy)
				assertSame(CY, other);
			return other instanceof Cy;
		}
		@Override
		public String toString()
		{
			assertSame(CY, this);
			return "[C]";
		}
		private static final long serialVersionUID = 1l;
		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return CY;
		}
	}

	static final Cy CY = new Cy();

	static final Money<Cy> ZERO = Money.storeOf(0, CY);

	static Money<Cy> storeOf(final Integer amountStore)
	{
		return Money.storeOf(amountStore, CY);
	}

	static Money<Cy> storeOf(final long amountStore)
	{
		return Money.storeOf(amountStore, CY);
	}

	static Money<Cy> storeOf(final Long amountStore)
	{
		return Money.storeOf(amountStore, CY);
	}

	static Money<Cy> nullToZero(final Money<Cy> value)
	{
		return Money.nullToZero(value, CY);
	}

	static Money<Cy> valueOf(final double amount)
	{
		return Money.valueOf(amount, CY);
	}

	static Money<Cy> valueOf(final double amount, final RoundingMode roundingMode)
	{
		return Money.valueOf(amount, roundingMode, CY);
	}

	static Money<Cy> valueOf(final BigDecimal amount)
	{
		return Money.valueOf(amount, CY);
	}

	static Money<Cy> parse(final String source, final DecimalFormat format) throws ParseException
	{
		return Money.parseAmount(source, format, CY);
	}

	private MoneyAmountUtil()
	{
		// prevent instantiation
	}
}
