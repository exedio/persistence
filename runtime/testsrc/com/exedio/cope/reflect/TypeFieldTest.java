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

import static com.exedio.cope.reflect.TypeFieldItem.TYPE;
import static com.exedio.cope.reflect.TypeFieldItem.forUnique;
import static com.exedio.cope.reflect.TypeFieldItem.isFinal;
import static com.exedio.cope.reflect.TypeFieldItem.optional;
import static com.exedio.cope.reflect.TypeFieldItem.renamed;
import static com.exedio.cope.reflect.TypeFieldItem.restricted;
import static com.exedio.cope.reflect.TypeFieldItem.standard;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

public class TypeFieldTest extends TestWithEnvironment
{
	public TypeFieldTest()
	{
		super(TypeFieldModelTest.MODEL);
	}

	@Test public void testIt()
	{
		assertEquals("standard", SchemaInfo.getColumnName(standard.getIdField()));
		assertEquals("newname", SchemaInfo.getColumnName(renamed.getIdField()));

		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());

		item.setOptional(TYPE);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(TYPE, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals(list(), TYPE.search(optional.isInvalid()));

		item.setOptional(null);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals(list(), TYPE.search(optional.isInvalid()));

		item.setRestricted(TypeFieldSubItem.TYPE);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(TypeFieldSubItem.TYPE, item.getRestricted());

		item.setRestricted(null);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testMandatoryViolation()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		assertSame(TYPE, item.getStandard());
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
		assertSame(TYPE, item.getStandard());
	}

	@Test public void testFinalViolation()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		try
		{
			isFinal.set(item, TYPE);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(isFinal, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
	}

	@Test public void testNotFoundNoSuchID()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		standard.getIdField().set(item, "zack");
		try
		{
			item.getStandard();
			fail();
		}
		catch(final TypeField.NotFound e)
		{
			assertEquals(standard, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("zack", e.getID());
			assertEquals(
					"not found 'zack' on " + item + " " +
					"for TypeFieldItem.standard, "+
					"no such id in model.",
					e.getMessage());
		}
	}

	@Test public void testNotFoundWrongValueClass()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		restricted.getIdField().set(item, TYPE.getID());
		try
		{
			item.getRestricted();
			fail();
		}
		catch(final TypeField.NotFound e)
		{
			assertEquals(restricted, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(TYPE.getID(), e.getID());
			assertEquals(
					"not found '" + TYPE.getID() + "' on " + item + " " +
					"for TypeFieldItem.restricted, "+
					"expected instance of com.exedio.cope.reflect.TypeFieldSubItem, " +
					"but was com.exedio.cope.reflect.TypeFieldItem.",
					e.getMessage());
		}
	}

	@Test public void testUnique()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		assertEquals(null, forUnique(TYPE));
		assertEquals(null, forUnique(TypeFieldSubItem.TYPE));
		item.setUnique(TYPE);
		assertEquals(item, forUnique(TYPE));
		assertEquals(null, forUnique(TypeFieldSubItem.TYPE));
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

	@Test public void testRestrictionViolatedExecute()
	{
		try
		{
			TypeFieldItem.createRestrictedRaw(TYPE);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a Type<? extends com.exedio.cope.reflect.TypeFieldSubItem>, " +
					"but was a Type<com.exedio.cope.reflect.TypeFieldItem> for TypeFieldItem.restricted.",
					e.getMessage());
		}
	}

	@Test public void testRestrictionViolatedSetter()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE);
		assertEquals(null, item.getRestricted());

		try
		{
			item.setRestrictedRaw(TYPE);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a Type<? extends com.exedio.cope.reflect.TypeFieldSubItem>, " +
					"but was a Type<com.exedio.cope.reflect.TypeFieldItem> for TypeFieldItem.restricted.",
					e.getMessage());
		}
		assertEquals(null, item.getRestricted());
	}
}
