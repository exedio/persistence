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

package com.exedio.cope.pattern;

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.AbstractRuntimeTest.getInitialType;

import java.util.Arrays;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.junit.CopeAssert;

public class RangeFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(RangeFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(RangeFieldModelTest.class, "MODEL");
	}

	RangeFieldItem item;

	public void testIt()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.valid,
				item.valid.getFrom(),
				item.valid.getTo(),
				item.valid.getUnison(),
				item.text,
				item.text.getFrom(),
				item.text.getTo(),
				item.text.getUnison(),
			}), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.valid,
				item.valid.getFrom(),
				item.valid.getTo(),
				item.valid.getUnison(),
				item.text,
				item.text.getFrom(),
				item.text.getTo(),
				item.text.getUnison(),
			}), item.TYPE.getDeclaredFeatures());

		assertEquals(item.TYPE, item.valid.getFrom().getType());
		assertEquals(item.TYPE, item.valid.getTo().getType());
		assertEquals(item.TYPE, item.valid.getType());
		assertEquals("valid-from", item.valid.getFrom().getName());
		assertEquals("valid-to",   item.valid.getTo().getName());
		assertEquals("valid",     item.valid.getName());
		assertEquals(item.valid, item.valid.getFrom().getPattern());

		assertEquals(true, item.valid.isInitial());
		assertEquals(false, item.valid.isFinal());
		assertEquals(true,  item.valid.isMandatory());
		assertEquals(false, item.valid.getFrom().isFinal());
		assertEquals(false, item.valid.getTo().isFinal());
		assertEquals(Wrapper.generic(Range.class, Integer.class), getInitialType(item.valid));
		assertContains(MandatoryViolationException.class, item.valid.getInitialExceptions());
		assertSerializedSame(item.valid, 388);

		assertEquals(true, item.text.isInitial());
		assertEquals(true, item.text.isFinal());
		assertEquals(true, item.text.isMandatory());
		assertEquals(true, item.text.getFrom().isFinal());
		assertEquals(true, item.text.getTo().isFinal());
		assertEquals(Wrapper.generic(Range.class, String.class), getInitialType(item.text));
		assertContains(FinalViolationException.class, MandatoryViolationException.class, StringLengthViolationException.class, item.text.getInitialExceptions());
		assertSerializedSame(item.text, 387);


		try
		{
			RangeField.newRange(new IntegerField().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("optional borderTemplate not yet implemented", e.getMessage());
		}
		try
		{
			RangeField.newRange(new IntegerField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unique borderTemplate is not supported", e.getMessage());
		}

		try
		{
			item.valid.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("right", e.getMessage());
		}
	}
}
