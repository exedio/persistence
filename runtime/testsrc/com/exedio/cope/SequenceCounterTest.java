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

import static com.exedio.cope.SequenceInfoAssert.assertInfoAny;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SequenceCounterTest
{
	@Test void testNormal()
	{
		final SequenceCounter c =
				newSequenceCounter(MyItem.featureNormal, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		c.next(15);
		assertIt(c, 15, 10, 20, 1, 15, 15);
		c.next(16);
		assertIt(c, 15, 10, 20, 2, 15, 16);
		c.next(17);
		assertIt(c, 15, 10, 20, 3, 15, 17);
	}

	@Test void testLimit()
	{
		final SequenceCounter c =
				newSequenceCounter(MyItem.featureLimit, 10, 10, 12);
		assertIt(c, 10, 10, 12);
		c.next(10);
		assertIt(c, 10, 10, 12, 1, 10, 10);
		c.next(11);
		assertIt(c, 10, 10, 12, 2, 10, 11);
		c.next(12);
		assertIt(c, 10, 10, 12, 3, 10, 12);
		try
		{
			c.next(13);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 13 in " + feature + " limited to 10,12", e.getMessage());
		}
		assertIt(c, 10, 10, 12, 3, 10, 12);
	}

	@Test void testHole()
	{
		final SequenceCounter c =
				newSequenceCounter(MyItem.featureHole, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		c.next(17);
		assertIt(c, 15, 10, 20, 1, 17, 17);
		c.next(19);
		assertIt(c, 15, 10, 20, 2, 17, 19);
	}

	@Test void testMin()
	{
		final SequenceCounter c =
				newSequenceCounter(MyItem.featureMin, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		try
		{
			c.next(9);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 9 in " + feature + " limited to 10,20", e.getMessage());
		}
		assertIt(c, 15, 10, 20);
	}

	@Test void testMax()
	{
		final SequenceCounter c =
				newSequenceCounter(MyItem.featureMax, 15, 10, 20);
		assertIt(c, 15, 10, 20);
		try
		{
			c.next(21);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 21 in " + feature + " limited to 10,20", e.getMessage());
		}
		assertIt(c, 15, 10, 20);
	}

	private void assertIt(
			final SequenceCounter counter,
			final int start, final int minimum, final int maximum,
			final int count, final int first, final int last)
	{
		assertNotNull(feature);
		assertInfoAny(feature, start, minimum, maximum, count, first, last, counter.getInfo());
	}

	private void assertIt(
			final SequenceCounter counter,
			final int start, final int minimum, final int maximum)
	{
		assertNotNull(feature);
		assertInfoAny(feature, start, minimum, maximum, counter.getInfo());
	}

	private SequenceCounter newSequenceCounter(
			final StringField feature, final long start, final long minimum, final long maximum)
	{
		assertNull(this.feature);
		assertNotNull(feature);
		this.feature = feature;
		final SequenceCounter result = new SequenceCounter(feature, start, minimum, maximum);
		result.onModelNameSet(new ModelMetrics(MODEL, MODEL.toString()).name(SequenceCounterTest.class/* deliberate nonsense */));
		return result;
	}

	StringField feature = null;

	@BeforeEach @AfterEach
	void resetFeature()
	{
		feature = null;
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		@WrapperIgnore static final StringField featureNormal = new StringField();
		@WrapperIgnore static final StringField featureLimit  = new StringField();
		@WrapperIgnore static final StringField featureHole   = new StringField();
		@WrapperIgnore static final StringField featureMin    = new StringField();
		@WrapperIgnore static final StringField featureMax    = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}
