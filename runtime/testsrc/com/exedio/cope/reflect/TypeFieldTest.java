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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.reflect.TypeField.NotFound;
import org.junit.jupiter.api.Test;

public class TypeFieldTest extends TestWithEnvironment
{
	public TypeFieldTest()
	{
		super(TypeFieldModelTest.MODEL);
	}

	@Test void testIt()
	{
		assertEquals("standard", SchemaInfo.getColumnName(standard.getIdField()));
		assertEquals("newname", SchemaInfo.getColumnName(renamed.getIdField()));

		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		assertSame(TYPE, item.getStandard());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals("TypeFieldItem", standard.getId(item));
		assertEquals("TypeFieldSubItem", isFinal.getId(item));
		assertEquals(null, optional.getId(item));
		assertEquals(null, restricted.getId(item));

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

	@Test void testMandatoryViolation()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		assertSame(TYPE, item.getStandard());
		final MandatoryViolationException e = assertFails(
				() -> item.setStandard(null),
				MandatoryViolationException.class,
				"mandatory violation on " + item + " for " + standard);
		assertEquals(standard, e.getFeature());
		assertEquals(item, e.getItem());
		assertSame(TYPE, item.getStandard());
	}

	@Test void testFinalViolation()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
		final FinalViolationException e = assertFails(
				() -> isFinal.set(item, TYPE),
				FinalViolationException.class,
				"final violation on " + item + " for " + isFinal);
		assertEquals(isFinal, e.getFeature());
		assertEquals(item, e.getItem());
		assertSame(TypeFieldSubItem.TYPE, item.getIsFinal());
	}

	@Test void testNotFoundNoSuchID()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		standard.getIdField().set(item, "zack");
		assertEquals("zack", standard.getId(item));
		final NotFound e = assertFails(
				item::getStandard,
				NotFound.class,
				"not found 'zack' on " + item + " " +
				"for TypeFieldItem.standard, " +
				"no such id in model.");
		assertEquals(standard, e.getFeature());
		assertEquals(item, e.getItem());
		assertEquals("zack", e.getID());
		assertEquals("zack", standard.getId(item));
	}

	@Test void testNotFoundWrongValueClass()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		restricted.getIdField().set(item, TYPE.getID());
		final NotFound e = assertFails(
				item::getRestricted,
				NotFound.class,
				"not found '" + TYPE.getID() + "' on " + item + " " +
				"for TypeFieldItem.restricted, "+
				"expected instance of com.exedio.cope.reflect.TypeFieldSubItem, " +
				"but was com.exedio.cope.reflect.TypeFieldItem.");
		assertEquals(restricted, e.getFeature());
		assertEquals(item, e.getItem());
		assertEquals(TYPE.getID(), e.getID());
	}

	@Test void testUnique()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		assertEquals(null, forUnique(TYPE));
		assertEquals(null, forUnique(TypeFieldSubItem.TYPE));
		item.setUnique(TYPE);
		assertEquals(item, forUnique(TYPE));
		assertEquals(null, forUnique(TypeFieldSubItem.TYPE));
		assertFails(
				() -> forUnique(null),
				NullPointerException.class,
				null);
	}

	@Test void testRestrictionViolatedExecute()
	{
		assertFails(
				() -> TypeFieldItem.createRestrictedRaw(TYPE),
				ClassCastException.class,
				"expected a Type<? extends com.exedio.cope.reflect.TypeFieldSubItem>, " +
				"but was " + TYPE + " which is a Type<com.exedio.cope.reflect.TypeFieldItem> for TypeFieldItem.restricted.");
	}

	@Test void testRestrictionViolatedSetter()
	{
		final TypeFieldItem item = new TypeFieldItem(TYPE, TypeFieldSubItem.TYPE, null);
		assertEquals(null, item.getRestricted());

		assertFails(
				() -> item.setRestrictedRaw(TYPE),
				ClassCastException.class,
				"expected a Type<? extends com.exedio.cope.reflect.TypeFieldSubItem>, " +
				"but was " + TYPE + " which is a Type<com.exedio.cope.reflect.TypeFieldItem> for TypeFieldItem.restricted.");
		assertEquals(null, item.getRestricted());
	}
}
