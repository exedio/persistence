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

package com.exedio.cope;

import static com.exedio.cope.IntegerFieldDefaultToNextItem.TYPE;
import static com.exedio.cope.IntegerFieldDefaultToNextItem.integerNext;
import static com.exedio.cope.IntegerFieldDefaultToNextItem.integerNone;
import static com.exedio.cope.SequenceInfoAssert.assertInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class IntegerFieldDefaultToNextTest extends TestWithEnvironment
{
	public IntegerFieldDefaultToNextTest()
	{
		super(IntegerFieldDefaultToNextModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testNext()
	{
		assertDefaultToNextSequenceName("DefaulToItem_inteNext_Seq", integerNext);

		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			clock.assertEmpty();
			final IntegerFieldDefaultToNextItem item = new IntegerFieldDefaultToNextItem(
			);
			clock.assertEmpty();

			assertEquals(integer(10001), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 1, 10001, 10001, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
		{
			clock.assertEmpty();
			final IntegerFieldDefaultToNextItem item = new IntegerFieldDefaultToNextItem(
			);
			clock.assertEmpty();

			assertEquals(integer(10002), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, 2, 10001, 10002, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
	}
	@Test void testSet()
	{
		{
			clock.assertEmpty();
			final IntegerFieldDefaultToNextItem item = new IntegerFieldDefaultToNextItem(
					integerNext.map(7001)
			);
			clock.assertEmpty();

			assertEquals(integer(7001), item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
	}
	@Test void testSetNull()
	{
		{
			clock.assertEmpty();
			final IntegerFieldDefaultToNextItem item = new IntegerFieldDefaultToNextItem(
					integerNext.map(null)
			);
			clock.assertEmpty();

			assertEquals(null, item.getIntegerNext());
			assertEquals(null, item.getIntegerNone());
		}
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), integerNext);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(integerNext, integerNext.getDefaultToNextInfo());
		assertNull(integerNone.getDefaultToNextInfo());
	}

	private static Integer integer(final int i)
	{
		return Integer.valueOf(i);
	}
}
