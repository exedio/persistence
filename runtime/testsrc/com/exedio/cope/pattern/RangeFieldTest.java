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

import static com.exedio.cope.AbstractRuntimeTest.i3;
import static com.exedio.cope.AbstractRuntimeTest.i4;
import static com.exedio.cope.AbstractRuntimeTest.i5;
import static com.exedio.cope.AbstractRuntimeTest.i8;
import static com.exedio.cope.AbstractRuntimeTest.i9;
import static com.exedio.cope.pattern.Range.valueOf;
import static com.exedio.cope.pattern.RangeFieldItem.TYPE;
import static com.exedio.cope.pattern.RangeFieldItem.valid;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CheckViolationException;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class RangeFieldTest extends TestWithEnvironment
{
	public RangeFieldTest()
	{
		super(RangeFieldModelTest.MODEL);
	}

	@Test void testIt()
	{
		final RangeFieldItem item = new RangeFieldItem(3, 5);

		assertEquals(valueOf(3, 5), item.getValid());
		assertEquals(i3, item.getValidFrom());
		assertEquals(i5, item.getValidTo());

		assertFails(
				() -> item.setValidFrom(8),
				CheckViolationException.class,
				"check violation on " + item + " for RangeFieldItem.valid-unison");

		assertEquals(valueOf(3, 5), item.getValid());
		assertEquals(i3, item.getValidFrom());
		assertEquals(i5, item.getValidTo());

		item.setValidTo(9);
		assertEquals(valueOf(3, 9), item.getValid());
		assertEquals(i3, item.getValidFrom());
		assertEquals(i9, item.getValidTo());

		item.setValidFrom(8);
		assertEquals(valueOf(8, 9), item.getValid());
		assertEquals(i8, item.getValidFrom());
		assertEquals(i9, item.getValidTo());
		assertEquals(false, item.doesValidContain( 7));
		assertEquals(true,  item.doesValidContain( 8));
		assertEquals(true,  item.doesValidContain( 9));
		assertEquals(false, item.doesValidContain(10));

		final RangeFieldItem item2 = new RangeFieldItem(4, 4);
		assertEquals(valueOf(4, 4), item2.getValid());
		assertEquals(i4, item2.getValidFrom());
		assertEquals(i4, item2.getValidTo());
		assertEquals(false, item2.doesValidContain(3));
		assertEquals(true,  item2.doesValidContain(4));
		assertEquals(false, item2.doesValidContain(5));

		assertContains(       TYPE.search(valid.contains(3)));
		assertContains(item2, TYPE.search(valid.contains(4)));
		assertContains(       TYPE.search(valid.contains(5)));

		assertContains(      TYPE.search(valid.contains(7)));
		assertContains(item, TYPE.search(valid.contains(8)));
		assertContains(item, TYPE.search(valid.contains(9)));
		assertContains(      TYPE.search(valid.contains(10)));
	}

	@Test void testCreateNull()
	{
		final Range<String> text = valueOf("a", "b");
		assertFails(
				() -> new RangeFieldItem(null, text),
				NullPointerException.class,
				null);
	}

	@Test void testSetNull()
	{
		final RangeFieldItem i = new RangeFieldItem(valueOf(1, 2), valueOf("a", "b"));

		assertFails(
				() -> i.setValid(null),
				NullPointerException.class,
				null);
		assertEquals(valueOf(1, 2), i.getValid());

		assertFails(
				() -> i.set(SetValue.map(valid, null)),
				NullPointerException.class,
				null);
		assertEquals(valueOf(1, 2), i.getValid());
	}

	@Test void testBorderConstraint()
	{
		final IntegerRangeViolationException e = assertFails(
				() -> new RangeFieldItem(-11, 5),
				IntegerRangeViolationException.class,
				"range violation, " +
				"-11 is too small for RangeFieldItem.valid-from, " +
				"must be at least -10");
		assertEquals(null, e.getItem());
		assertEquals(valid.getFrom(), e.getFeature());
		assertEquals(-11, e.getValue());
	}
}
