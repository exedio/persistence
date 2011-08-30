/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.IntegerItem.TYPE;
import static com.exedio.cope.IntegerItem.any;
import static com.exedio.cope.IntegerItem.mandatory;
import static com.exedio.cope.IntegerItem.max4;
import static com.exedio.cope.IntegerItem.min4;
import static com.exedio.cope.IntegerItem.min4Max8;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import com.exedio.cope.junit.CopeAssert;

public class IntegerModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(TYPE);

	public void testIt()
	{
		assertEquals(TYPE, any.getType());
		assertEquals("any", any.getName());
		assertEquals(false, any.isMandatory());
		assertEquals(null, any.getPattern());
		assertEquals(MIN_VALUE, any.getMinimum());
		assertEquals(MAX_VALUE, any.getMaximum());
		assertContains(any.getInitialExceptions());

		assertEquals(TYPE, mandatory.getType());
		assertEquals("mandatory", mandatory.getName());
		assertEquals(true, mandatory.isMandatory());
		assertEquals(MIN_VALUE, mandatory.getMinimum());
		assertEquals(MAX_VALUE, mandatory.getMaximum());
		assertContains(MandatoryViolationException.class, mandatory.getInitialExceptions());

		assertEquals(false, min4.isMandatory());
		assertEquals(4, min4.getMinimum());
		assertEquals(MAX_VALUE, min4.getMaximum());
		assertContains(IntegerRangeViolationException.class, min4.getInitialExceptions());

		assertEquals(false, max4.isMandatory());
		assertEquals(MIN_VALUE, max4.getMinimum());
		assertEquals(4, max4.getMaximum());
		assertContains(IntegerRangeViolationException.class, max4.getInitialExceptions());

		assertEquals(false, min4Max8.isMandatory());
		assertEquals(4, min4Max8.getMinimum());
		assertEquals(8, min4Max8.getMaximum());
		assertContains(IntegerRangeViolationException.class, min4Max8.getInitialExceptions());
	}

	public void testCheck()
	{
		try
		{
			mandatory.check(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		try
		{
			min4.check(3);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(min4, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation, " +
					"3 is too small for " + min4 + ", " +
					"must be at least 4.",
					e.getMessage());
		}
		min4.check(4);
	}

	public void testConditions()
	{
		assertEqualsStrict(any.equal(1), any.equal(1));
		assertNotEqualsStrict(any.equal(1), any.equal(2));
		assertNotEqualsStrict(any.equal(1), any.equal((Integer)null));
		assertNotEqualsStrict(any.equal(1), any.greater(1));
		assertEqualsStrict(any.equal(mandatory), any.equal(mandatory));
		assertNotEqualsStrict(any.equal(mandatory), any.equal(any));
	}
}
