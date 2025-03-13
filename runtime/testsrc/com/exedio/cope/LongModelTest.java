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

import static com.exedio.cope.LongItem.TYPE;
import static com.exedio.cope.LongItem.any;
import static com.exedio.cope.LongItem.mandatory;
import static com.exedio.cope.LongItem.max4;
import static com.exedio.cope.LongItem.min4;
import static com.exedio.cope.LongItem.min4Max8;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class LongModelTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(TYPE);

	@Test void testIt()
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
		assertContains(LongRangeViolationException.class, min4.getInitialExceptions());

		assertEquals(false, max4.isMandatory());
		assertEquals(MIN_VALUE, max4.getMinimum());
		assertEquals(4, max4.getMaximum());
		assertContains(LongRangeViolationException.class, max4.getInitialExceptions());

		assertEquals(false, min4Max8.isMandatory());
		assertEquals(4, min4Max8.getMinimum());
		assertEquals(8, min4Max8.getMaximum());
		assertContains(LongRangeViolationException.class, min4Max8.getInitialExceptions());
	}

	@Test void testCheck()
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
			min4.check(3l);
			fail();
		}
		catch(final LongRangeViolationException e)
		{
			assertEquals(min4, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation, " +
					"3 is too small for " + min4 + ", " +
					"must be at least 4",
					e.getMessage());
		}
		min4.check(4l);
	}

	@Test void testConditions()
	{
		assertEqualsAndHash(any.is(1l), any.is(1l));
		assertEqualsAndHash(any.is(mandatory), any.is(mandatory));
		assertNotEqualsAndHash(
				any.is(1l),
				any.is(2l),
				any.is((Long)null),
				any.greater(1l),
				any.is(mandatory),
				any.is(any));
	}
}
