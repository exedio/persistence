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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import org.junit.Test;

public class MultiItemFieldTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MultiItemFieldItem.TYPE,
			AnCascadeItem.TYPE,
			MultiItemFieldComponentxA.TYPE,
			MultiItemFieldComponentxB.TYPE,
			MultiItemFieldComponentxC.TYPE);

	static
	{
		MODEL.enableSerialization(MultiItemFieldTest.class, "MODEL");
	}

	public MultiItemFieldTest()
	{
		super(MODEL);
	}

	@Test public void testHashCode()
	{
		final MultiItemFieldComponentxA fieldValue = new MultiItemFieldComponentxA();
		final MultiItemFieldItem expected = new MultiItemFieldItem(fieldValue);
		final MultiItemFieldValuex i1 = expected.getField();
		final MultiItemFieldValuex i2 = expected.getField();
		assertEquals(i1, i2);
		assertEquals(i1, fieldValue);
		assertSame(i1, fieldValue);
		assertFalse(i1.equals(null));
	}

	@Test public void testSerialization()
	{
		final MultiItemFieldComponentxA fieldValue = new MultiItemFieldComponentxA();
		final MultiItemFieldItem expected = new MultiItemFieldItem(fieldValue);
		final MultiItemFieldValuex i1 = expected.getField();
		final MultiItemFieldValuex i2 = expected.getOptionalField();
		final MultiItemFieldValuex i1S = reserialize(i1, 118);
		assertEquals(i1S, i1);
		assertEquals(i1S.hashCode(), i1.hashCode());
		assertNotSame(i1S, i1);
		assertFalse(i1S.equals(i2));
		assertEquals("MultiItemFieldComponentxA-0", i1S.toString());
	}

	@Test public void testEqual()
	{
		final MultiItemFieldComponentxA fieldValue = new MultiItemFieldComponentxA();
		assertEquals(
				"MultiItemFieldItem.field-MultiItemFieldComponentxA='MultiItemFieldComponentxA-0'",
				MultiItemFieldItem.field.equal(fieldValue).toString());
	}

	@Test public void testEqualConditionNull()
	{
		assertEquals(
				"(MultiItemFieldItem.field-MultiItemFieldComponentxA is null AND" +
				" MultiItemFieldItem.field-MultiItemFieldComponentxB is null)",
				MultiItemFieldItem.field.equal(null).toString());
	}

	@Test public void testEqualConditionInvalidClass()
	{
		final MultiItemFieldComponentxC invalid = new MultiItemFieldComponentxC();
		assertEquals(Condition.FALSE, MultiItemFieldItem.field.equal(invalid));
	}

	@Test public void testGet()
	{
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		assertEquals(expected, item.getField());
	}

	@Test public void testSet()
	{
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldComponentxB expected2 = new MultiItemFieldComponentxB();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		assertEquals(expected, item.getField());
		assertEquals(expected, item.getFieldA());
		assertEquals(null,     item.getFieldB());

		item.setField(expected2);
		assertEquals(expected2, item.getField());
		assertEquals(null,      item.getFieldA());
		assertEquals(expected2, item.getFieldB());
	}

	@Test public void testSetNull()
	{
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		item.setOptionalField(expected);
		assertEquals(expected, item.getOptionalField());
		assertEquals(expected, item.getOptionalFieldA());
		assertEquals(null,     item.getOptionalFieldB());

		item.setOptionalField(null);
		assertEquals(null, item.getOptionalField());
		assertEquals(null, item.getOptionalFieldA());
		assertEquals(null, item.getOptionalFieldB());
	}

	@Test public void testSetNullForMandatory()
	{
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		try
		{
			item.setField(null);
			fail("exception expected");
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(MultiItemFieldItem.field, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(expected, item.getField());
	}

	@Test public void testSetInvalidInterfaceItem()
	{
		final MultiItemFieldComponentxC notExpected = new MultiItemFieldComponentxC();
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		try
		{
			item.setField(notExpected);
			fail("exception expected");
		}
		catch(final ConstraintViolationException e)
		{
			assertEquals(MultiItemFieldItem.field, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(
					"illegal instance on MultiItemFieldItem-0, " +
					"value is com.exedio.cope.pattern.MultiItemFieldComponentxC for MultiItemFieldItem.field, " +
					"must be one of [class com.exedio.cope.pattern.MultiItemFieldComponentxA, class com.exedio.cope.pattern.MultiItemFieldComponentxB].",
					e.getMessage());
			assertEquals(
					"com.exedio.cope.pattern.MultiItemField$IllegalInstanceException",
					e.getClass().getName());
		}
		assertEquals(expected, item.getField());
	}

	@Test public void testSetClassCast()
	{
		@SuppressWarnings({"cast", "unchecked", "rawtypes"}) // OK: test bad API usage
		final MultiItemField<String> field = (MultiItemField<String>)(MultiItemField)MultiItemFieldItem.field;
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		try
		{
			field.set(item, "zack");
			fail("exception expected");
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.MultiItemFieldValuex, but was a java.lang.String",
					e.getMessage());
		}
		assertEquals(expected, item.getField());
	}

	@Test public void testConditionIsNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("MultiItemFieldItem.field-MultiItemFieldComponentxA is null");
		sb.append(" AND ");
		sb.append("MultiItemFieldItem.field-MultiItemFieldComponentxB is null");
		sb.append(")");
		assertEquals(sb.toString(), MultiItemFieldItem.field.isNull().toString());
	}

	@Test public void testConditionIsNotNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("MultiItemFieldItem.field-MultiItemFieldComponentxA is not null");
		sb.append(" OR ");
		sb.append("MultiItemFieldItem.field-MultiItemFieldComponentxB is not null");
		sb.append(")");
		assertEquals(sb.toString(), MultiItemFieldItem.field.isNotNull().toString());
	}

	@Test public void testGetComponents()
	{
		assertEqualsUnmodifiable(
				list(MultiItemFieldItem.field.of(MultiItemFieldComponentxA.class),
						MultiItemFieldItem.field.of(MultiItemFieldComponentxB.class)),
				MultiItemFieldItem.field.getComponents());
	}

	@Test public void testOfNotValidClassParameter()
	{
		try
		{
			MultiItemFieldItem.field.of(MultiItemFieldComponentxC.class);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class >"+MultiItemFieldComponentxC.class
					+"< is not supported by MultiItemFieldItem.field", e.getMessage());
		}
	}

	@Test public void testUniqueSetNull()
	{
		assertNull(MultiItemFieldItem.field.of(MultiItemFieldComponentxA.class).getImplicitUniqueConstraint());
		assertNull(MultiItemFieldItem.field.of(MultiItemFieldComponentxB.class).getImplicitUniqueConstraint());
		assertNotNull(MultiItemFieldItem.uniqueField.of(MultiItemFieldComponentxA.class).getImplicitUniqueConstraint());
		assertNotNull(MultiItemFieldItem.uniqueField.of(MultiItemFieldComponentxB.class).getImplicitUniqueConstraint());

		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				new MultiItemFieldComponentxA());
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				new MultiItemFieldComponentxA());
		item1.setUniqueField(null);
		item2.setUniqueField(null);
		assertEquals(null, item1.getUniqueField());
		assertEquals(null, item2.getUniqueField());
	}

	@Test public void testUnique()
	{
		final MultiItemFieldComponentxA value = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				value);
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				value);
		item1.setUniqueField(value);
		try
		{
			item2.setUniqueField(value);
			fail("exception expected");
		}
		catch(final UniqueViolationException e)
		{
			assertEquals("unique violation on "+item2
					+" for MultiItemFieldItem.uniqueField-MultiItemFieldComponentxAImplicitUnique",
					e.getMessage());
		}
		assertEquals(value, item1.getUniqueField());
	}

	@Test public void testgetPartOfReverse()
	{
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getPartOfs(MultiItemFieldComponentxA.TYPE));
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getDeclaredPartOfs(MultiItemFieldComponentxA.TYPE));
	}

	@Test public void testgetPartOfReverseEmpty()
	{
		assertEquals(list(), PartOf.getPartOfs(MultiItemFieldComponentxB.TYPE));
		assertEquals(list(), PartOf.getDeclaredPartOfs(MultiItemFieldComponentxB.TYPE));
	}

	@Test public void testPartOf()
	{
		final MultiItemFieldComponentxA field = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				field);
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				field);
		assertEquals(list(item1, item2), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test public void testPartOfEmpty()
	{
		final MultiItemFieldComponentxA field = new MultiItemFieldComponentxA();
		new MultiItemFieldItem(new MultiItemFieldComponentxB());
		assertEquals(list(), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test public void testPartOfInvalidClassParameter()
	{
		final MultiItemFieldComponentxB field = new MultiItemFieldComponentxB();
		try
		{
			MultiItemFieldItem.partOfClassA.getParts(field);
			fail("exception expected");
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.MultiItemFieldComponentxA, but was a com.exedio.cope.pattern.MultiItemFieldComponentxB",
					e.getMessage());
		}
	}

	@Test public void testCascade()
	{
		final MultiItemFieldComponentxA item = new MultiItemFieldComponentxA();
		final AnCascadeItem toBeCascadeDeleted = new AnCascadeItem(item);
		item.deleteCopeItem();
		assertEquals(false, toBeCascadeDeleted.existsCopeItem());
	}

	@Test public void testForbid()
	{
		final MultiItemFieldComponentxA item = new MultiItemFieldComponentxA();
		new MultiItemFieldItem(item);
		try
		{
			item.deleteCopeItem();
			fail("exception expected");
		}
		catch(final IntegrityViolationException e)
		{
			assertEquals(
					"integrity violation on deletion of MultiItemFieldComponentxA-0" +
					" because of MultiItemFieldItem.field-MultiItemFieldComponentxA referring to 1 item(s)",
					e.getMessage());
		}
	}

	@Test public void testMap()
	{
		final MultiItemFieldComponentxA a = new MultiItemFieldComponentxA();
		assertEquals(SetValue.map(MultiItemFieldItem.field, (MultiItemFieldValuex) a), MultiItemFieldItem.field.map(a));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldComponentxA-0", MultiItemFieldItem.field.map(a).toString());
	}

	@Test public void testMapNull()
	{
		assertEquals(SetValue.map(MultiItemFieldItem.optionalField, (MultiItemFieldValuex) null), MultiItemFieldItem.optionalField.map(null));
		assertEquals("MultiItemFieldItem.optionalField=null", MultiItemFieldItem.optionalField.map(null).toString());
	}

	@Test public void testMapInvalid()
	{
		final MultiItemFieldComponentxC c = new MultiItemFieldComponentxC();
		assertEquals(SetValue.map(MultiItemFieldItem.field, (MultiItemFieldValuex) c), MultiItemFieldItem.field.map(c));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldComponentxC-0", MultiItemFieldItem.field.map(c).toString());
	}

	static final class AnCascadeItem extends Item
	{
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				MultiItemFieldComponentxA.class,
				MultiItemFieldComponentxB.class).cascade();

		AnCascadeItem(final MultiItemFieldValuex value)
		{
			this(field.map(value));
		}
		

	/**
	 * Creates a new AnCascadeItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnCascadeItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnCascadeItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnCascadeItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anCascadeItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnCascadeItem> TYPE = com.exedio.cope.TypesBound.newType(AnCascadeItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnCascadeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
