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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.reflect.FeatureField.NotFound;
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

		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		assertSame(string1, item.getStandard());
		assertSame(string2, item.getIsFinal());
		assertSame(null, item.getOptional());
		assertSame(null, item.getRestricted());
		assertEquals("FeatureFieldItem.string1", standard.getId(item));
		assertEquals("FeatureFieldItem.string2", isFinal.getId(item));
		assertEquals(null, optional.getId(item));
		assertEquals(null, restricted.getId(item));

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
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		assertSame(string1, item.getStandard());
		final MandatoryViolationException e = assertFails(
				() -> item.setStandard(null),
				MandatoryViolationException.class,
				"mandatory violation on " + item + " for " + standard);
		assertEquals(standard, e.getFeature());
		assertEquals(item, e.getItem());
		assertSame(string1, item.getStandard());
	}

	@Test void testFinalViolation()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		assertSame(string2, item.getIsFinal());
		final FinalViolationException e = assertFails(
				() -> isFinal.set(item, integer1),
				FinalViolationException.class,
				"final violation on " + item + " for " + isFinal);
		assertEquals(isFinal, e.getFeature());
		assertEquals(item, e.getItem());
		assertSame(string2, item.getIsFinal());
	}

	@Test void testNotFoundNoSuchID()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		standard.getIdField().set(item, "zack");
		assertEquals("zack", standard.getId(item));
		final NotFound e = assertFails(
				item::getStandard,
				NotFound.class,
				"not found 'zack' on " + item + " " +
				"for FeatureFieldItem.standard, "+
				"no such id in model.");
		assertEquals(standard, e.getFeature());
		assertEquals(item, e.getItem());
		assertEquals("zack", e.getID());
		assertEquals("zack", standard.getId(item));
	}

	@Test void testNotFoundWrongValueClass()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		restricted.getIdField().set(item, integer1.getID());
		final NotFound e = assertFails(
				item::getRestricted,
				NotFound.class,
				"not found '" + integer1.getID() + "' on " + item + " " +
				"for FeatureFieldItem.restricted, "+
				"expected instance of com.exedio.cope.StringField, " +
				"but was com.exedio.cope.IntegerField.");
		assertEquals(restricted, e.getFeature());
		assertEquals(item, e.getItem());
		assertEquals(integer1.getID(), e.getID());
	}

	@Test void testUnique()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		assertEquals(null, forUnique(string1));
		assertEquals(null, forUnique(string2));
		item.setUnique(string1);
		assertEquals(item, forUnique(string1));
		assertEquals(null, forUnique(string2));
		assertFails(
				() -> forUnique(null),
				NullPointerException.class,
				null);
	}

	@Test void testRestrictionViolatedExecute()
	{
		assertFails(
				() -> FeatureFieldItem.createRestrictedRaw(integer1),
				ClassCastException.class,
				"expected a com.exedio.cope.StringField, " +
				"but was " + integer1 + " which is a com.exedio.cope.IntegerField for FeatureFieldItem.restricted.");
	}

	@Test void testRestrictionViolatedSetter()
	{
		final FeatureFieldItem item = new FeatureFieldItem(string1, string2, null);
		assertEquals(null, item.getRestricted());

		assertFails(
				() -> item.setRestrictedRaw(integer1),
				ClassCastException.class,
				"expected a com.exedio.cope.StringField, " +
				"but was " + integer1 + " which is a com.exedio.cope.IntegerField for FeatureFieldItem.restricted.");
		assertEquals(null, item.getRestricted());
	}
}
