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

package com.exedio.cope.reflect;

import static com.exedio.cope.reflect.FeatureFieldItem.TYPE;
import static com.exedio.cope.reflect.FeatureFieldItem.forUnique;
import static com.exedio.cope.reflect.FeatureFieldItem.integer1;
import static com.exedio.cope.reflect.FeatureFieldItem.isFinal;
import static com.exedio.cope.reflect.FeatureFieldItem.optional;
import static com.exedio.cope.reflect.FeatureFieldItem.renamed;
import static com.exedio.cope.reflect.FeatureFieldItem.restricted;
import static com.exedio.cope.reflect.FeatureFieldItem.standard;
import static com.exedio.cope.reflect.FeatureFieldItem.string1;
import static com.exedio.cope.reflect.FeatureFieldItem.string2;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class FeatureFieldTest extends TestWithEnvironment
{
	public FeatureFieldTest()
	{
		super(FeatureFieldModelTest.MODEL);
	}

	@Test void testIt()
	{
		assertEquals("standard", SchemaInfo.getColumnName(standard.getIdField()));
		assertEquals("newname", SchemaInfo.getColumnName(renamed.getIdField()));

		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());

		item.setOptional(integer1);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(integer1, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals(list(), TYPE.search(optional.isInvalid()));

		item.setOptional(null);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals(list(), TYPE.search(optional.isInvalid()));

		item.setRestricted(string2);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(string2, item.getRestricted());

		item.setRestricted(null);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
	}

	@Test void testMandatoryViolation()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		assertSame(string1, item.getStandard());
		try
		{
			item.setStandard(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(standard, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(string1, item.getStandard());
	}

	@Test void testFinalViolation()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		assertSame(string2, item.getIsFinal());
		try
		{
			isFinal.set(item, integer1);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(isFinal, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(string2, item.getIsFinal());
	}

	@Test void testNotFoundNoSuchID()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		standard.getIdField().set(item, "zack");
		try
		{
			item.getStandard();
			fail();
		}
		catch(final FeatureField.NotFound e)
		{
			assertEquals(standard, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("zack", e.getID());
			assertEquals(
					"not found 'zack' on " + item + " " +
					"for FeatureFieldItem.standard, "+
					"no such id in model.",
					e.getMessage());
		}
	}

	@Test void testNotFoundWrongValueClass()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		restricted.getIdField().set(item, integer1.getID());
		try
		{
			item.getRestricted();
			fail();
		}
		catch(final FeatureField.NotFound e)
		{
			assertEquals(restricted, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(integer1.getID(), e.getID());
			assertEquals(
					"not found '" + integer1.getID() + "' on " + item + " " +
					"for FeatureFieldItem.restricted, "+
					"expected instance of com.exedio.cope.StringField, " +
					"but was com.exedio.cope.IntegerField.",
					e.getMessage());
		}
	}

	@Test void testUnique()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2);
		assertEquals(null, forUnique(string1));
		assertEquals(null, forUnique(string2));
		item.setUnique(string1);
		assertEquals(item, forUnique(string1));
		assertEquals(null, forUnique(string2));
		try
		{
			forUnique(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
