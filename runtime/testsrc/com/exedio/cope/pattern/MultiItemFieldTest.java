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

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Condition;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.UniqueViolationException;
import org.junit.Test;

public class MultiItemFieldTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(MultiItemFieldItem.TYPE,
			AnCascadeItem.TYPE,
			MultiItemFieldItemInterfaceImplementationA.TYPE,
			MultiItemFieldItemInterfaceImplementationB.TYPE,
			MultiItemFieldItemInterfaceImplementationC.TYPE);

	static
	{
		MODEL.enableSerialization(MultiItemFieldTest.class, "MODEL");
	}

	public MultiItemFieldTest()
	{
		super(MODEL);
	}

	@Test
	public void testHashCode()
	{
		final MultiItemFieldItemInterfaceImplementationA fieldValue = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItem expected = new MultiItemFieldItem(fieldValue);
		final MultiItemFieldItemInterface i1 = expected.getField();
		final MultiItemFieldItemInterface i2 = expected.getField();
		assertEquals(i1, i2);
		assertEquals(i1, fieldValue);
		assertSame(i1, fieldValue);
		assertFalse(i1.equals(null));
	}

	@Test
	public void testSerialization()
	{
		final MultiItemFieldItemInterfaceImplementationA fieldValue = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItem expected = new MultiItemFieldItem(fieldValue);
		final MultiItemFieldItemInterface i1 = expected.getField();
		final MultiItemFieldItemInterface i2 = expected.getOptionalField();
		final MultiItemFieldItemInterface i1S = reserialize(i1, 131);
		assertEquals(i1S, i1);
		assertEquals(i1S.hashCode(), i1.hashCode());
		assertNotSame(i1S, i1);
		assertFalse(i1S.equals(i2));
		assertEquals("MultiItemFieldItemInterfaceImplementationA-0", i1S.toString());
	}

	@Test
	public void testEqual()
	{
		final MultiItemFieldItemInterfaceImplementationA fieldValue = new MultiItemFieldItemInterfaceImplementationA();
		assertEquals(
				"MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationA='MultiItemFieldItemInterfaceImplementationA-0'",
				MultiItemFieldItem.field.equal(fieldValue).toString());
	}

	@Test
	public void testEqualConditionNull()
	{
		assertEquals(
				"(MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationA is null" +
				" AND MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationB is null)",
				MultiItemFieldItem.field.equal(null).toString());
	}

	@Test
	public void testEqualConditionInvalidClass()
	{
		final MultiItemFieldItemInterfaceImplementationC invalid = new MultiItemFieldItemInterfaceImplementationC();
		assertEquals(Condition.FALSE, MultiItemFieldItem.field.equal(invalid));
	}

	@Test
	public void testGet()
	{
		final MultiItemFieldItemInterfaceImplementationA expected = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		assertEquals(expected, item.getField());
	}

	@Test
	public void testSet()
	{
		final MultiItemFieldItemInterfaceImplementationA expected = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItemInterfaceImplementationB expected2 = new MultiItemFieldItemInterfaceImplementationB();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		item.setField(expected2);
		assertEquals(expected2, item.getField());
	}

	@Test
	public void testSetNull()
	{
		final MultiItemFieldItemInterfaceImplementationA expected = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItem item = new MultiItemFieldItem(expected);
		item.setOptionalField(expected);
		assertEquals(expected, item.getOptionalField());
		item.setOptionalField(null);
		assertEquals(null, item.getOptionalField());
	}

	@Test
	public void testSetNullForMandatory()
	{
		final MultiItemFieldItem item = new MultiItemFieldItem(
				new MultiItemFieldItemInterfaceImplementationA());
		try
		{
			item.setField(null);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("MultiItemFieldItem.field is mandatory", e.getMessage());
		}
	}

	@Test
	public void testSetInvalidInterfaceItem()
	{
		final MultiItemFieldItemInterfaceImplementationC notExpected = new MultiItemFieldItemInterfaceImplementationC();
		final MultiItemFieldItem item = new MultiItemFieldItem(
				new MultiItemFieldItemInterfaceImplementationA());
		try
		{
			item.setField(notExpected);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"value class should be on of <MultiItemFieldItemInterfaceImplementationA,MultiItemFieldItemInterfaceImplementationB>" +
					" but was <MultiItemFieldItemInterfaceImplementationC>",
					e.getMessage());
		}
	}

	@Test
	public void testConditionIsNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationA is null");
		sb.append(" AND ");
		sb.append("MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationB is null");
		sb.append(")");
		assertEquals(sb.toString(), MultiItemFieldItem.field.isNull().toString());
	}

	@Test
	public void testConditionIsNotNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationA is not null");
		sb.append(" OR ");
		sb.append("MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationB is not null");
		sb.append(")");
		assertEquals(sb.toString(), MultiItemFieldItem.field.isNotNull().toString());
	}

	@Test
	public void testGetComponents()
	{
		assertEqualsUnmodifiable(
				list(MultiItemFieldItem.field.of(MultiItemFieldItemInterfaceImplementationA.class),
						MultiItemFieldItem.field.of(MultiItemFieldItemInterfaceImplementationB.class)),
				MultiItemFieldItem.field.getComponents());
	}

	@Test
	public void testOfNotValidClassParameter()
	{
		try
		{
			MultiItemFieldItem.field.of(MultiItemFieldItemInterfaceImplementationC.class);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class >"+MultiItemFieldItemInterfaceImplementationC.class
					+"< is not supported by MultiItemFieldItem.field", e.getMessage());
		}
	}

	@Test
	public void testUniqueSetNull()
	{
		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				new MultiItemFieldItemInterfaceImplementationA());
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				new MultiItemFieldItemInterfaceImplementationA());
		item1.setUniqueField(null);
		item2.setUniqueField(null);
		assertEquals(null, item1.getUniqueField());
		assertEquals(null, item2.getUniqueField());
	}

	@Test
	public void testUnique()
	{
		final MultiItemFieldItemInterfaceImplementationA value = new MultiItemFieldItemInterfaceImplementationA();
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
					+" for MultiItemFieldItem.uniqueField-MultiItemFieldItemInterfaceImplementationAImplicitUnique",
					e.getMessage());
		}
	}

	@Test
	public void testgetPartOfReverse()
	{
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getPartOfs(MultiItemFieldItemInterfaceImplementationA.TYPE));
		assertEquals(list(MultiItemFieldItem.partOfClassA), PartOf.getDeclaredPartOfs(MultiItemFieldItemInterfaceImplementationA.TYPE));
	}

	@Test
	public void testgetPartOfReverseEmpty()
	{
		assertEquals(list(), PartOf.getPartOfs(MultiItemFieldItemInterfaceImplementationB.TYPE));
		assertEquals(list(), PartOf.getDeclaredPartOfs(MultiItemFieldItemInterfaceImplementationB.TYPE));
	}

	@Test
	public void testPartOf()
	{
		final MultiItemFieldItemInterfaceImplementationA field = new MultiItemFieldItemInterfaceImplementationA();
		final MultiItemFieldItem item1 = new MultiItemFieldItem(
				field);
		final MultiItemFieldItem item2 = new MultiItemFieldItem(
				field);
		assertEquals(list(item1, item2), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test
	public void testPartOfEmpty()
	{
		final MultiItemFieldItemInterfaceImplementationA field = new MultiItemFieldItemInterfaceImplementationA();
		new MultiItemFieldItem(new MultiItemFieldItemInterfaceImplementationB());
		assertEquals(list(), MultiItemFieldItem.partOfClassA.getParts(field));
	}

	@Test
	public void testPartOfInvalidClassParameter()
	{
		final MultiItemFieldItemInterfaceImplementationB field = new MultiItemFieldItemInterfaceImplementationB();
		try
		{
			MultiItemFieldItem.partOfClassA.getParts(field);
			fail("exception expected");
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.MultiItemFieldItemInterfaceImplementationA, but was a com.exedio.cope.pattern.MultiItemFieldItemInterfaceImplementationB",
					e.getMessage());
		}
	}

	@Test
	public void testCascade()
	{
		final MultiItemFieldItemInterfaceImplementationA item = new MultiItemFieldItemInterfaceImplementationA();
		final AnCascadeItem toBeCascadeDeleted = new AnCascadeItem(item);
		item.deleteCopeItem();
		assertEquals(false, toBeCascadeDeleted.existsCopeItem());
	}

	@Test
	public void testForbid()
	{
		final MultiItemFieldItemInterfaceImplementationA item = new MultiItemFieldItemInterfaceImplementationA();
		new MultiItemFieldItem(item);
		try
		{
			item.deleteCopeItem();
			fail("exception expected");
		}
		catch(final IntegrityViolationException e)
		{
			assertEquals(
					"integrity violation on deletion of MultiItemFieldItemInterfaceImplementationA-0" +
					" because of MultiItemFieldItem.field-MultiItemFieldItemInterfaceImplementationA referring to 1 item(s)",
					e.getMessage());
		}
	}

	@Test
	public void testMap()
	{
		final MultiItemFieldItemInterfaceImplementationA a = new MultiItemFieldItemInterfaceImplementationA();
		assertEquals(SetValue.map(MultiItemFieldItem.field, (MultiItemFieldItemInterface) a), MultiItemFieldItem.field.map(a));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldItemInterfaceImplementationA-0", MultiItemFieldItem.field.map(a).toString());
	}

	@Test
	public void testMapNull()
	{
		assertEquals(SetValue.map(MultiItemFieldItem.optionalField, (MultiItemFieldItemInterface) null), MultiItemFieldItem.optionalField.map(null));
		assertEquals("MultiItemFieldItem.optionalField=null", MultiItemFieldItem.optionalField.map(null).toString());
	}

	@Test
	public void testMapInvalid()
	{
		final MultiItemFieldItemInterfaceImplementationC c = new MultiItemFieldItemInterfaceImplementationC();
		assertEquals(SetValue.map(MultiItemFieldItem.field, (MultiItemFieldItemInterface) c), MultiItemFieldItem.field.map(c));
		assertEquals("MultiItemFieldItem.field=MultiItemFieldItemInterfaceImplementationC-0", MultiItemFieldItem.field.map(c).toString());
	}

	static final class AnCascadeItem extends Item
	{
		/** @cope.ignore */
		static final MultiItemField<MultiItemFieldItemInterface> field = MultiItemField.create(
				MultiItemFieldItemInterface.class,
				MultiItemFieldItemInterfaceImplementationA.class,
				MultiItemFieldItemInterfaceImplementationB.class).cascade();

		AnCascadeItem(final MultiItemFieldItemInterface value)
		{
			this(field.map(value));
		}
		/**

	 **
	 * Creates a new AnCascadeItem with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnCascadeItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}/**

	 **
	 * Creates a new AnCascadeItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnCascadeItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anCascadeItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnCascadeItem> TYPE = com.exedio.cope.TypesBound.newType(AnCascadeItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnCascadeItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}
