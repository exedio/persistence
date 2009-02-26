/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.Wrapper;

public class RangeFieldTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(RangeFieldItem.TYPE);
	
	public RangeFieldTest()
	{
		super(MODEL);
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
			}), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.valid,
				item.valid.getFrom(),
				item.valid.getTo(),
			}), item.TYPE.getDeclaredFeatures());

		assertEquals(item.TYPE, item.valid.getFrom().getType());
		assertEquals(item.TYPE, item.valid.getTo().getType());
		assertEquals(item.TYPE, item.valid.getType());
		assertEquals("validFrom", item.valid.getFrom().getName());
		assertEquals("validTo",   item.valid.getTo().getName());
		assertEquals("valid",     item.valid.getName());
		assertEquals(item.valid, item.valid.getFrom().getPattern());
		
		assertEquals(true, item.valid.isInitial());
		assertEquals(false, item.valid.isFinal());
		assertEquals(Wrapper.generic(Range.class, Integer.class), item.valid.getInitialType());
		assertContains(MandatoryViolationException.class, item.valid.getInitialExceptions());
		
		// test persistence
		item = deleteOnTearDown(new RangeFieldItem(new Range<Integer>(3, 5)));
		
		assertEquals(new Range<Integer>(3, 5), item.getValid());
		assertEquals(i3, item.getValidFrom());
		assertEquals(i5, item.getValidTo());
		
		item.setValidFrom(8);
		assertEquals(new Range<Integer>(8, 5), item.getValid());
		assertEquals(i8, item.getValidFrom());
		assertEquals(i5, item.getValidTo());
		
		item.setValidTo(9);
		assertEquals(new Range<Integer>(8, 9), item.getValid());
		assertEquals(i8, item.getValidFrom());
		assertEquals(i9, item.getValidTo());
		
		final RangeFieldItem item2 = deleteOnTearDown(new RangeFieldItem(new Range<Integer>(4, 4)));
		assertEquals(new Range<Integer>(4, 4), item2.getValid());
		assertEquals(i4, item2.getValidFrom());
		assertEquals(i4, item2.getValidTo());
		
		assertContains(       item.TYPE.search(item.valid.contains(3)));
		assertContains(item2, item.TYPE.search(item.valid.contains(4)));
		assertContains(       item.TYPE.search(item.valid.contains(5)));
		
		assertContains(      item.TYPE.search(item.valid.contains(7)));
		assertContains(item, item.TYPE.search(item.valid.contains(8)));
		assertContains(item, item.TYPE.search(item.valid.contains(9)));
		assertContains(      item.TYPE.search(item.valid.contains(10)));
		
		try
		{
			RangeField.newRange(new IntegerField().unique());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("unique borderTemplate is not supported", e.getMessage());
		}
	}
}
