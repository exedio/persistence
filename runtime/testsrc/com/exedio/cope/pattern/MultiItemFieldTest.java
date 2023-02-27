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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MultiItemFieldTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MultiItemFieldItem.TYPE,
			AnCascadeItem.TYPE,
			AnNullifyItem.TYPE,
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

	@Test void testEqualCondition()
	{
		final MultiItemFieldComponentxA fieldValue = new MultiItemFieldComponentxA();
		assertEquals(
				"MultiItemFieldItem.field-MultiItemFieldComponentxA='MultiItemFieldComponentxA-0'",
				MultiItemFieldItem.field.equal(fieldValue).toString());
	}

	@Test void testEqualConditionNull()
	{
		assertEquals(
				"(MultiItemFieldItem.field-MultiItemFieldComponentxA is null and" +
				" MultiItemFieldItem.field-MultiItemFieldComponentxB is null)",
				MultiItemFieldItem.field.equal(null).toString());
	}

	@Test void testEqualConditionInvalidClass()
	{
		final MultiItemFieldComponentxC invalid = new MultiItemFieldComponentxC();
		assertEquals(Condition.FALSE, MultiItemFieldItem.field.equal(invalid));
	}

	@Test void testGet()
	{
		final MultiItemFieldComponentxA expected = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		assertEquals(expected, item.getField());
	}

	@Test void testSet()
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

	@Test void testSetNull()
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

	@Test void testCreateWithNullForMandatory()
	{
		try
		{
			new MultiItemFieldItem(null);
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(MultiItemFieldItem.field, e.getFeature());
			assertEquals(null, e.getItem());
		}
	}

	@Test void testSetNullForMandatory()
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

	@Test void testSetInvalidInterfaceItem()
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
					"must be one of [class com.exedio.cope.pattern.MultiItemFieldComponentxA, class com.exedio.cope.pattern.MultiItemFieldComponentxB]",
					e.getMessage());
			assertEquals(
					"com.exedio.cope.pattern.MultiItemField$IllegalInstanceException",
					e.getClass().getName());
		}
		assertEquals(expected, item.getField());
	}

	@Test void testSetClassCast()
	{
		@SuppressWarnings({"cast", "unchecked"}) // OK: test bad API usage
		final MultiItemField<String> field = (MultiItemField<String>)(MultiItemField<?>)MultiItemFieldItem.field;
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
					"Cannot cast java.lang.String to com.exedio.cope.pattern.MultiItemFieldValuex",
					e.getMessage());
		}
		assertEquals(expected, item.getField());
	}

	@Test void testConditionIsNull()
	{
		assertEquals(
				"(" +
				"MultiItemFieldItem.field-MultiItemFieldComponentxA is null" +
				" and " +
				"MultiItemFieldItem.field-MultiItemFieldComponentxB is null" +
				")",
				MultiItemFieldItem.field.isNull().toString());
	}

	@Test void testConditionIsNotNull()
	{
		assertEquals(
				"(" +
				"MultiItemFieldItem.field-MultiItemFieldComponentxA is not null" +
				" or " +
				"MultiItemFieldItem.field-MultiItemFieldComponentxB is not null" +
				")",
				MultiItemFieldItem.field.isNotNull().toString());
	}

	@Test void testGetComponents()
	{
		assertEqualsUnmodifiable(
				list(MultiItemFieldItem.field.of(MultiItemFieldComponentxA.class),
						MultiItemFieldItem.field.of(MultiItemFieldComponentxB.class)),
				MultiItemFieldItem.field.getComponents());
	}

	@Test void testOfNotValidClassParameter()
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

	@Test void testUniqueSetNull()
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

	@Test void testUnique()
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

	@Test void testSearch()
	{
		final MultiItemFieldValuex value1 = new MultiItemFieldComponentxA();
		final MultiItemFieldValuex value2 = new MultiItemFieldComponentxA();
		final MultiItemFieldValuex value3 = new MultiItemFieldComponentxA();
		final MultiItemFieldValuex valueNotStoreable = new NotStoreableMultiItemFieldValuex();
		final MultiItemFieldItem item1 = new MultiItemFieldItem(value1);
		item1.setUniqueField(value1);
		final MultiItemFieldItem item2 = new MultiItemFieldItem(value1);
		item2.setUniqueField(value2);

		assertEquals(item1, MultiItemFieldItem.forUniqueField(value1));
		assertEquals(item2, MultiItemFieldItem.forUniqueField(value2));
		assertEquals(null, MultiItemFieldItem.forUniqueField(value3));
		assertEquals(null, MultiItemFieldItem.forUniqueField(valueNotStoreable));
		try
		{
			MultiItemFieldItem.forUniqueField(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on MultiItemFieldItem.uniqueField", e.getMessage());
		}

		assertEquals(item1, MultiItemFieldItem.forUniqueFieldStrict(value1));
		assertEquals(item2, MultiItemFieldItem.forUniqueFieldStrict(value2));
		try
		{
			MultiItemFieldItem.forUniqueFieldStrict(value3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"expected result of size one, but was empty for query: select this from MultiItemFieldItem where uniqueField-MultiItemFieldComponentxA='MultiItemFieldComponentxA-2'",
				e.getMessage()
			);
		}
		try
		{
			MultiItemFieldItem.forUniqueFieldStrict(valueNotStoreable);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"expected result of size one, but was empty for query: select this from MultiItemFieldItem where FALSE",
				e.getMessage()
			);
		}
		try
		{
			MultiItemFieldItem.forUniqueFieldStrict(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on MultiItemFieldItem.uniqueField", e.getMessage());
		}
	}

	private static class NotStoreableMultiItemFieldValuex implements MultiItemFieldValuex
	{
		private static final long serialVersionUID = 1L;
	}

	@Test void testgetPartOfReverse()
	{
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getPartOfs(MultiItemFieldComponentxA.TYPE));
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getDeclaredPartOfs(MultiItemFieldComponentxA.TYPE));
	}

	@Test void testgetPartOfReverseEmpty()
	{
		assertEquals(list(), PartOf.getPartOfs(MultiItemFieldComponentxB.TYPE));
		assertEquals(list(), PartOf.getDeclaredPartOfs(MultiItemFieldComponentxB.TYPE));
	}

	@Test void testPartOf()
	{
		final MultiItemFieldComponentxA field = new MultiItemFieldComponentxA();
		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				field);
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				field);
		assertEquals(list(item1, item2), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test void testPartOfEmpty()
	{
		final MultiItemFieldComponentxA field = new MultiItemFieldComponentxA();
		new MultiItemFieldItem(new MultiItemFieldComponentxB());
		assertEquals(list(), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test void testPartOfInvalidClassParameter()
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
					"Cannot cast com.exedio.cope.pattern.MultiItemFieldComponentxB to com.exedio.cope.pattern.MultiItemFieldComponentxA",
					e.getMessage());
		}
	}

	@Test void testCascade()
	{
		final MultiItemFieldComponentxA item = new MultiItemFieldComponentxA();
		final AnCascadeItem toBeCascadeDeleted = new AnCascadeItem(item);
		item.deleteCopeItem();
		assertEquals(false, toBeCascadeDeleted.existsCopeItem());
	}

	@Test void testForbid()
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

	@Test void testNullify()
	{
		final MultiItemFieldComponentxA a = new MultiItemFieldComponentxA();
		final AnNullifyItem nullify = new AnNullifyItem();
		nullify.setField(a);
		a.deleteCopeItem();
		assertEquals(true, nullify.existsCopeItem());
		assertEquals(null, nullify.getField());
	}

	@Test void testMap()
	{
		final MultiItemFieldComponentxA a = new MultiItemFieldComponentxA();
		assertEquals(SetValue.map(MultiItemFieldItem.field, a), MultiItemFieldItem.field.map(a));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldComponentxA-0", MultiItemFieldItem.field.map(a).toString());
	}

	@Test void testMapNull()
	{
		assertEquals(SetValue.map(MultiItemFieldItem.optionalField, null), MultiItemFieldItem.optionalField.map(null));
		assertEquals("MultiItemFieldItem.optionalField=null", MultiItemFieldItem.optionalField.map(null).toString());
	}

	@Test void testMapInvalid()
	{
		final MultiItemFieldComponentxC c = new MultiItemFieldComponentxC();
		assertEquals(SetValue.map(MultiItemFieldItem.field, c), MultiItemFieldItem.field.map(c));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldComponentxC-0", MultiItemFieldItem.field.map(c).toString());
	}

	@WrapperType(indent=2)
	private static final class AnCascadeItem extends Item
	{
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
				canBe(MultiItemFieldComponentxA.class).
				canBe(MultiItemFieldComponentxB.class).
				cascade();

		/**
		 * Creates a new AnCascadeItem with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.MandatoryViolationException if field is null.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnCascadeItem(
					@javax.annotation.Nonnull final MultiItemFieldValuex field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnCascadeItem.field,field),
			});
		}

		/**
		 * Creates a new AnCascadeItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private AnCascadeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValuex getField()
		{
			return AnCascadeItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final MultiItemFieldValuex field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			AnCascadeItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anCascadeItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<AnCascadeItem> TYPE = com.exedio.cope.TypesBound.newType(AnCascadeItem.class,AnCascadeItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private AnCascadeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class AnNullifyItem extends Item
	{
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
				canBe(MultiItemFieldComponentxA.class).
				canBe(MultiItemFieldComponentxB.class).
				nullify();

		/**
		 * Creates a new AnNullifyItem with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnNullifyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		/**
		 * Creates a new AnNullifyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private AnNullifyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		MultiItemFieldValuex getField()
		{
			return AnNullifyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final MultiItemFieldValuex field)
		{
			AnNullifyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anNullifyItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<AnNullifyItem> TYPE = com.exedio.cope.TypesBound.newType(AnNullifyItem.class,AnNullifyItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private AnNullifyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
