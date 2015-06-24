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
import com.exedio.cope.Model;
import com.exedio.cope.UniqueViolationException;
import org.junit.Test;

public class InterfaceItemFieldTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(InterfaceItemFieldItem.TYPE,
			InterfaceItemFieldItemInterfaceImplementationA.TYPE,
			InterfaceItemFieldItemInterfaceImplementationB.TYPE,
			InterfaceItemFieldItemInterfaceImplementationC.TYPE);

	static
	{
		MODEL.enableSerialization(InterfaceItemFieldTest.class, "MODEL");
	}

	public InterfaceItemFieldTest()
	{
		super(MODEL);
	}

	@Test
	public void testHashCode()
	{
		final InterfaceItemFieldItemInterfaceImplementationA fieldValue = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem expected = new InterfaceItemFieldItem(fieldValue);
		final InterfaceItemFieldItemInterface i1 = expected.getField();
		final InterfaceItemFieldItemInterface i2 = expected.getField();
		assertEquals(i1, i2);
		assertEquals(i1, fieldValue);
		assertSame(i1, fieldValue);
		assertFalse(i1.equals(null));
	}

	@Test
	public void testSerialization()
	{
		final InterfaceItemFieldItemInterfaceImplementationA fieldValue = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem expected = new InterfaceItemFieldItem(fieldValue);
		final InterfaceItemFieldItemInterface i1 = expected.getField();
		final InterfaceItemFieldItemInterface i2 = expected.getOptionalField();
		final InterfaceItemFieldItemInterface i1S = reserialize(i1, 135);
		assertEquals(i1S, i1);
		assertEquals(i1S.hashCode(), i1.hashCode());
		assertNotSame(i1S, i1);
		assertFalse(i1S.equals(i2));
		assertEquals("InterfaceItemFieldItemInterfaceImplementationA-0", i1S.toString());
	}

	@Test
	public void testGetSource()
	{
		final InterfaceItemFieldItemInterfaceImplementationA fieldValue = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem expected = new InterfaceItemFieldItem(fieldValue);
		assertEquals(expected, InterfaceItemFieldItem.field.getSource(InterfaceItemFieldItem.class, fieldValue));
	}

	@Test
	public void testGet()
	{
		final InterfaceItemFieldItemInterfaceImplementationA expected = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(expected);
		assertEquals(expected, item.getField());
	}

	@Test
	public void testSet()
	{
		final InterfaceItemFieldItemInterfaceImplementationA expected = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItemInterfaceImplementationB expected2 = new InterfaceItemFieldItemInterfaceImplementationB();
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(expected);
		item.setField(expected2);
		assertEquals(expected2, item.getField());
	}

	@Test
	public void testSetNull()
	{
		final InterfaceItemFieldItemInterfaceImplementationA expected = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(expected);
		item.setOptionalField(expected);
		assertEquals(expected, item.getOptionalField());
		item.setOptionalField(null);
		assertEquals(null, item.getOptionalField());
	}

	@Test
	public void testSetNullForMandatory()
	{
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(
				new InterfaceItemFieldItemInterfaceImplementationA());
		try
		{
			item.setField(null);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("InterfaceItemFieldItem.field is mandatory", e.getMessage());
		}
	}

	@Test
	public void testSetInvalidInterfaceItem()
	{
		final InterfaceItemFieldItemInterfaceImplementationC notExpected = new InterfaceItemFieldItemInterfaceImplementationC();
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(
				new InterfaceItemFieldItemInterfaceImplementationA());
		try
		{
			item.setField(notExpected);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"value class should be on of <InterfaceItemFieldItemInterfaceImplementationA,InterfaceItemFieldItemInterfaceImplementationB> but was <InterfaceItemFieldItemInterfaceImplementationC>",
					e.getMessage());
		}
	}

	@Test
	public void testConditionIsNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("InterfaceItemFieldItem.field-InterfaceItemFieldItemInterfaceImplementationA is null");
		sb.append(" AND ");
		sb.append("InterfaceItemFieldItem.field-InterfaceItemFieldItemInterfaceImplementationB is null");
		sb.append(")");
		assertEquals(sb.toString(), InterfaceItemFieldItem.field.isNull().toString());
	}

	@Test
	public void testConditionIsNotNull()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("InterfaceItemFieldItem.field-InterfaceItemFieldItemInterfaceImplementationA is not null");
		sb.append(" OR ");
		sb.append("InterfaceItemFieldItem.field-InterfaceItemFieldItemInterfaceImplementationB is not null");
		sb.append(")");
		assertEquals(sb.toString(), InterfaceItemFieldItem.field.isNotNull().toString());
	}

	@Test
	public void testGetComponents()
	{
		assertEqualsUnmodifiable(
				list(InterfaceItemFieldItem.field.of(InterfaceItemFieldItemInterfaceImplementationA.class),
						InterfaceItemFieldItem.field.of(InterfaceItemFieldItemInterfaceImplementationB.class)),
				InterfaceItemFieldItem.field.getComponents());
	}

	@Test
	public void testOfNotValidClassParameter()
	{
		try
		{
			InterfaceItemFieldItem.field.of(InterfaceItemFieldItemInterfaceImplementationC.class);
			fail("exception expected");
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class >"+InterfaceItemFieldItemInterfaceImplementationC.class
					+"< is not supported by InterfaceItemFieldItem.field", e.getMessage());
		}
	}

	@Test
	public void testUniqueSetNull()
	{
		final InterfaceItemFieldItem item1 = new InterfaceItemFieldItem(
				new InterfaceItemFieldItemInterfaceImplementationA());
		final InterfaceItemFieldItem item2 = new InterfaceItemFieldItem(
				new InterfaceItemFieldItemInterfaceImplementationA());
		item1.setUniqueField(null);
		item2.setUniqueField(null);
		assertEquals(null, item1.getUniqueField());
		assertEquals(null, item2.getUniqueField());
	}

	@Test
	public void testUnique()
	{
		final InterfaceItemFieldItemInterfaceImplementationA value = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem item1 = new InterfaceItemFieldItem(
				value);
		final InterfaceItemFieldItem item2 = new InterfaceItemFieldItem(
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
					+" for InterfaceItemFieldItem.uniqueField-InterfaceItemFieldItemInterfaceImplementationAImplicitUnique",
					e.getMessage());
		}
	}
}
