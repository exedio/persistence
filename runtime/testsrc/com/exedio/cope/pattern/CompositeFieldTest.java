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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.CompositeFinalItem.first;
import static com.exedio.cope.pattern.CompositeFinalItem.second;
import static com.exedio.cope.pattern.CompositeItem.eins;
import static com.exedio.cope.pattern.CompositeItem.zwei;
import static com.exedio.cope.pattern.CompositeOptionalItem.duo;
import static com.exedio.cope.pattern.CompositeOptionalItem.uno;
import static com.exedio.cope.pattern.CompositeValue.aString;
import static com.exedio.cope.pattern.CompositeValue.anEnum;
import static com.exedio.cope.pattern.CompositeValue.anInt;
import static com.exedio.cope.pattern.CompositeValue.anItem;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.CompositeValue.AnEnum;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class CompositeFieldTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(CompositeItem.TYPE, CompositeOptionalItem.TYPE, CompositeFinalItem.TYPE, CompositeItemHolder.TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldTest.class, "MODEL");
	}

	public CompositeFieldTest()
	{
		super(MODEL);
	}

	CompositeOptionalItem target1;
	CompositeOptionalItem target2;
	CompositeItem item;
	CompositeOptionalItem oItem;
	CompositeFinalItem fItem;

	@Before public final void setUp()
	{
		target1 = new CompositeOptionalItem("target1");
		target2 = new CompositeOptionalItem("target2");
	}

	@Test public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeItem.TYPE.getThis(),
				CompositeItem.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
			}), CompositeItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeItem.TYPE.getThis(),
				CompositeItem.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
			}), CompositeItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeOptionalItem.TYPE.getThis(),
				CompositeOptionalItem.code,
				uno,
				uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem),
				uno.getUnison(),
				duo,
				duo.of(aString), duo.of(anInt), duo.of(anEnum), duo.of(anItem),
				duo.getUnison(),
			}), CompositeOptionalItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeOptionalItem.TYPE.getThis(),
				CompositeOptionalItem.code,
				uno,
				uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem),
				uno.getUnison(),
				duo,
				duo.of(aString), duo.of(anInt), duo.of(anEnum), duo.of(anItem),
				duo.getUnison(),
			}), CompositeOptionalItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeFinalItem.TYPE.getThis(),
				CompositeFinalItem.code,
				first,
				first.of(aString),  first.of(anInt),  first.of(anEnum),  first.of(anItem),
				second,
				second.of(aString), second.of(anInt), second.of(anEnum), second.of(anItem),
			}), CompositeFinalItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				CompositeFinalItem.TYPE.getThis(),
				CompositeFinalItem.code,
				first,
				first.of(aString),  first.of(anInt),  first.of(anEnum),  first.of(anItem),
				second,
				second.of(aString), second.of(anInt), second.of(anEnum),second.of(anItem),
			}), CompositeFinalItem.TYPE.getDeclaredFeatures());

		assertEquals(CompositeOptionalItem.TYPE, uno.of(aString).getType());
		assertEquals(CompositeOptionalItem.TYPE, uno.getType());
		assertEquals("uno-aString", uno.of(aString).getName());
		assertEquals("uno", uno.getName());
		assertEquals(uno, uno.of(aString).getPattern());
		assertEqualsUnmodifiable(list(uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem), uno.getUnison()), uno.getSourceFeatures());

		assertEquals(true,  eins.isInitial());
		assertEquals(false, eins.isFinal());
		assertEquals(true,  eins.isMandatory());
		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());
		assertEquals(false, uno.isInitial());
		assertEquals(false, uno.isFinal());
		assertEquals(false, uno.isMandatory());
		assertEquals(false, uno.of(aString).isInitial());
		assertEquals(false, uno.of(aString).isFinal());
		assertEquals(false, uno.of(aString).isMandatory());
		assertEquals(true, first.isInitial());
		assertEquals(true, first.isFinal());
		assertEquals(true, first.isMandatory());
		assertEquals(true, first.of(aString).isInitial());
		assertEquals(true, first.of(aString).isFinal());
		assertEquals(true, first.of(aString).isMandatory());

		assertEquals(CompositeValue.class, eins .getValueClass());
		assertEquals(CompositeValue.class, uno  .getValueClass());
		assertEquals(CompositeValue.class, first.getValueClass());

		assertSame(aString, eins.getTemplate(eins.of(aString)));
		assertSame(anInt,   eins.getTemplate(eins.of(anInt)));
		assertSame(anInt,   uno .getTemplate(uno .of(anInt)));

		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem), eins  .getTemplates());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem), uno   .getTemplates());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem), second.getTemplates());

		assertEqualsUnmodifiable(list(eins.  of(aString), eins  .of(anInt), eins  .of(anEnum), eins  .of(anItem)), eins  .getComponents());
		assertEqualsUnmodifiable(list(uno   .of(aString), uno   .of(anInt), uno   .of(anEnum), uno   .of(anItem)), uno   .getComponents());
		assertEqualsUnmodifiable(list(second.of(aString), second.of(anInt), second.of(anEnum), second.of(anItem)), second.getComponents());

		assertNull(eins.getUnison());
		assertNull(zwei.getUnison());
		assertNull(first.getUnison());
		assertNull(second.getUnison());
		{
			final CheckConstraint unison = uno.getUnison();
			assertSame(CompositeOptionalItem.TYPE, unison.getType());
			assertEquals("uno-unison", unison.getName());
			assertEquals(uno, unison.getPattern());
			assertEquals(Cope.or(
					Cope.and(
							uno.of(aString).isNull(),
							uno.of(anInt  ).isNull(),
							uno.of(anEnum ).isNull(),
							uno.of(anItem ).isNull()),
					Cope.and(
							uno.of(aString).isNotNull(),
							uno.of(anInt  ).isNotNull(),
							uno.of(anEnum ).isNotNull(),
							uno.of(anItem ).isNotNull())),
					unison.getCondition());
		}
		{
			final CheckConstraint unison = duo.getUnison();
			assertSame(CompositeOptionalItem.TYPE, unison.getType());
			assertEquals("duo-unison", unison.getName());
			assertEquals(duo, unison.getPattern());
			assertEquals(Cope.or(
					Cope.and(
							duo.of(aString).isNull(),
							duo.of(anInt  ).isNull(),
							duo.of(anEnum ).isNull(),
							duo.of(anItem ).isNull()),
					Cope.and(
							duo.of(aString).isNotNull(),
							duo.of(anInt  ).isNotNull(),
							duo.of(anEnum ).isNotNull(),
							duo.of(anItem ).isNotNull())),
					unison.getCondition());
		}

		assertSerializedSame(eins, 385);
		assertSerializedSame(zwei, 385);
		assertSerializedSame(uno,  392);
		assertSerializedSame(duo,  392);

		// test type safety of template-component relation
		second.of(aString).startsWith("zack");
		second.of(anInt).plus(1);
		second.getTemplate(second.of(aString)).startsWith("zack");
		second.getTemplate(second.of(anInt)).plus(1);

		try
		{
			uno.of(CompositeOptionalItem.code);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("CompositeOptionalItem.code is not a template of CompositeOptionalItem.uno", e.getMessage());
		}
		try
		{
			uno.getTemplate(CompositeOptionalItem.code);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("CompositeOptionalItem.code is not a component of CompositeOptionalItem.uno", e.getMessage());
		}
		try
		{
			uno.getTemplate(duo.of(aString));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("CompositeOptionalItem.duo-aString is not a component of CompositeOptionalItem.uno", e.getMessage());
		}

		{
			final CompositeValue v = second.newValue(
					CompositeValue.aString.map("firstString1"),
					CompositeValue.anInt.map(1),
					CompositeValue.anEnum.map(AnEnum.facet1),
					CompositeValue.anItem.map(target1));
			assertEquals("firstString1", v.getAString());
			assertEquals(1,              v.getAnInt());
			assertEquals(AnEnum.facet1,  v.getAnEnum());
			assertEquals(target1,        v.getAnItem());

			try
			{
				second.newValue();
				fail();
			}
			catch(final MandatoryViolationException e)
			{
				assertEquals("mandatory violation for " + CompositeValue.aString.toString(), e.getMessage());
			}
			try
			{
				second.newValue(CompositeValue.aString.map(null));
				fail();
			}
			catch(final MandatoryViolationException e)
			{
				assertEquals("mandatory violation for " + CompositeValue.aString.toString(), e.getMessage());
			}
			try
			{
				second.newValue(CompositeItem.code.map("firstString1"));
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("not a member", e.getMessage());
			}
		}

		assertFalse(aString.isAnnotationPresent(Computed.class));

		assertFalse(eins.of(aString).isAnnotationPresent(Computed.class));
		assertFalse(eins.of(anInt  ).isAnnotationPresent(Computed.class));
		assertFalse(eins.of(anEnum ).isAnnotationPresent(Computed.class));
		assertFalse(eins.of(anItem ).isAnnotationPresent(Computed.class));
		assertFalse(zwei.of(aString).isAnnotationPresent(Computed.class));
		assertFalse(zwei.of(anInt  ).isAnnotationPresent(Computed.class));
		assertFalse(zwei.of(anEnum ).isAnnotationPresent(Computed.class));
		assertFalse(zwei.of(anItem ).isAnnotationPresent(Computed.class));


		// test persistence
		oItem = new CompositeOptionalItem("optional1");
		assertEquals("optional1", oItem.getCode());
		assertEquals(null, oItem.getUno());
		assertEquals(null, oItem.getDuo());

		fItem = new CompositeFinalItem("final1",
						new CompositeValue("firstString1",  1, AnEnum.facet1, target1),
						new CompositeValue("secondString1", 2, AnEnum.facet2, target2));
		assertEquals("final1", fItem.getCode());
		assertEquals("firstString1", fItem.getFirst().getAString());
		assertEquals(1, fItem.getFirst().getAnInt());
		assertEquals(AnEnum.facet1, fItem.getFirst().getAnEnum());
		assertEquals(target1, fItem.getFirst().getAnItem());
		assertEquals("secondString1", fItem.getSecond().getAString());
		assertEquals(2, fItem.getSecond().getAnInt());
		assertEquals(AnEnum.facet2, fItem.getSecond().getAnEnum());
		assertEquals(target2, fItem.getSecond().getAnItem());

		oItem.setDuo(fItem.getFirst());
		assertEquals(null, oItem.getUno());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals(1, oItem.getDuo().getAnInt());
		assertEquals(AnEnum.facet1, oItem.getDuo().getAnEnum());
		assertEquals(target1, oItem.getDuo().getAnItem());

		oItem.setDuo(null);
		assertEquals(null, oItem.getUno());
		assertEquals(null, oItem.getDuo());
		assertEquals(null, duo.of(aString).get(oItem));
		assertEquals(null, duo.of(anInt  ).get(oItem));
		assertEquals(null, duo.of(anEnum ).get(oItem));
		assertEquals(null, duo.of(anItem ).get(oItem));

		item = new CompositeItem("default",
						new CompositeValue("einsString1", 1, AnEnum.facet1, target1),
						new CompositeValue("zweiString1", 2, AnEnum.facet2, target2));
		try
		{
			item.setEins(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for CompositeItem.eins-aString", e.getMessage()); // TODO feature should be CompositeItem.eins
		}
		try
		{
			new CompositeItem("defaultFailure",
					new CompositeValue("einsString1", 1, AnEnum.facet1, target1),
					null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for CompositeItem.zwei-aString", e.getMessage()); // TODO feature should be CompositeItem.zwei
		}
		try
		{
			first.set(fItem, null);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals("final violation on " + fItem + " for CompositeFinalItem.first-aString", e.getMessage()); // TODO feature should be CompositeFinalItem.first
		}
		try
		{
			first.set(fItem, new CompositeValue("finalViolation", 1, AnEnum.facet1, target1));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals("final violation on " + fItem + " for CompositeFinalItem.first-aString", e.getMessage()); // TODO feature should be CompositeFinalItem.first
		}

		// test value independence
		oItem.setDuo(fItem.getFirst());
		final CompositeValue value = oItem.getDuo();
		assertEquals("firstString1", value.getAString());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());

		value.setAString("firstString1X");
		assertEquals("firstString1X", value.getAString());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());

		oItem.setDuo(value);
		assertEquals("firstString1X", value.getAString());
		assertEquals("firstString1X", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());

		// test hashCode
		assertEquals(value, value);
		assertEquals(fItem.getFirst(), fItem.getFirst());
		assertNotSame(fItem.getFirst(), fItem.getFirst());
		assertFalse(fItem.getFirst().equals(oItem.getDuo()));
		assertFalse(fItem.getFirst().equals(null));
		assertFalse(fItem.getFirst().equals("hallo"));
		// test hashCode
		assertEquals(fItem.getFirst().hashCode(), fItem.getFirst().hashCode());
		assertFalse(fItem.getFirst().hashCode()==oItem.getDuo().hashCode());

		// test serialization
		final CompositeValue serializedValue = reserialize(value, 481);
		assertEquals(value, serializedValue);
		assertNotSame(value, serializedValue);
		assertEquals("firstString1X", value.getAString());
		assertEquals("firstString1X", serializedValue.getAString());
	}

	@Test public void testConditions()
	{
		final CompositeValue v = new CompositeValue("einsString1", 1, AnEnum.facet1, target1);
		final CompositeItem i1 = new CompositeItem("i1", v, v);
		final CompositeItem i2 = new CompositeItem("i1", v, v);
		final CompositeOptionalItem o1 = new CompositeOptionalItem("o1");
		final CompositeOptionalItem o2 = new CompositeOptionalItem("o2");
		o1.setUno(v);
		o2.setDuo(v);

		assertEquals(list(), CompositeItem.TYPE.search(CompositeItem.eins.isNull(), CompositeItem.TYPE.getThis(), true));
		assertEquals(list(), CompositeItem.TYPE.search(CompositeItem.zwei.isNull(), CompositeItem.TYPE.getThis(), true));

		assertEquals(list(i1, i2), CompositeItem.TYPE.search(CompositeItem.eins.isNotNull(), CompositeItem.TYPE.getThis(), true));
		assertEquals(list(i1, i2), CompositeItem.TYPE.search(CompositeItem.zwei.isNotNull(), CompositeItem.TYPE.getThis(), true));

		assertEquals(list(o1), CompositeOptionalItem.TYPE.search(CompositeOptionalItem.uno.isNotNull(), CompositeOptionalItem.TYPE.getThis(), true));
		assertEquals(list(o2), CompositeOptionalItem.TYPE.search(CompositeOptionalItem.duo.isNotNull(), CompositeOptionalItem.TYPE.getThis(), true));

		assertEquals(list(target1, target2, o2), CompositeOptionalItem.TYPE.search(CompositeOptionalItem.uno.isNull(), CompositeOptionalItem.TYPE.getThis(), true));
		assertEquals(list(target1, target2, o1), CompositeOptionalItem.TYPE.search(CompositeOptionalItem.duo.isNull(), CompositeOptionalItem.TYPE.getThis(), true));
	}

	@Test public void testBindingInConditions()
	{
		final CompositeItemHolder h1 = new CompositeItemHolder(target1);
		new CompositeItemHolder(target2);

		final CompositeValue uno1 = new CompositeValue("uno1", 1, AnEnum.facet1, target1);
		target1.setUno( uno1 );

		{
			final Query<CompositeItemHolder> query = CompositeItemHolder.TYPE.newQuery();
			final Join join1 = query.join(CompositeOptionalItem.TYPE);
			join1.setCondition(CompositeItemHolder.anItem.equalTarget(join1) );
			query.narrow( CompositeOptionalItem.uno.of(CompositeValue.aString).bind(join1).startsWith( "uno1" ) );

			final Join join2 = query.join(CompositeOptionalItem.TYPE);
			join2.setCondition(CompositeItemHolder.anItem.equalTarget(join2) );
			query.narrow( CompositeOptionalItem.duo.isNull(join2) );

			assertEquals( list(h1), query.search() );
		}

		{
			final Query<CompositeItemHolder> query = CompositeItemHolder.TYPE.newQuery();
			final Join join1 = query.join(CompositeOptionalItem.TYPE);
			join1.setCondition(CompositeItemHolder.anItem.equalTarget(join1) );
			query.narrow( CompositeOptionalItem.uno.of(CompositeValue.aString).bind(join1).startsWith( "uno1" ) );

			final Join join2 = query.join(CompositeOptionalItem.TYPE);
			join2.setCondition(CompositeItemHolder.anItem.equalTarget(join2) );
			query.narrow( CompositeOptionalItem.duo.isNotNull(join2) );

			assertTrue( query.search().isEmpty() );
		}

		target1.setUno( null );
	}
}

