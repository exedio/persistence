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

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.junit.CopeAssert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class InterfaceItemFieldStandardTest extends CopeAssert
{
	static final class AnMandatoryItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		@SuppressWarnings(
		{"unchecked", "rawtypes"})
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class, new Class[]
				{InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class});

		final InterfaceItemFieldInterface getField()
		{
			return field.get(this);
		}
	}

	static final class AnOptionalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		@SuppressWarnings(
		{"unchecked", "rawtypes"})
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class, new Class[]
				{InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class}).optional();

		final InterfaceItemFieldInterface getField()
		{
			return field.get(this);
		}
	}

	static final class AnFinalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		@SuppressWarnings(
		{"unchecked", "rawtypes"})
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class, new Class[]
				{InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class}).toFinal();

		final InterfaceItemFieldInterface getField()
		{
			return field.get(this);
		}
	}

	@Test
	public void testGetClasses()
	{
		final List<Class<? extends Item>> expected = new ArrayList<>();
		expected.add(InterfaceItemFieldInterfaceImplementationA.class);
		expected.add(InterfaceItemFieldInterfaceImplementationB.class);
		assertContainsList(expected, Arrays.asList(AnMandatoryItem.field.getClasses()));
	}

	@Test
	public void testGetInitialExceptionsMandatory()
	{
		assertContains(MandatoryViolationException.class, AnMandatoryItem.field.getInitialExceptions());
	}

	@Test
	public void testGetInitialExceptionsOptional()
	{
		assertContains(AnOptionalItem.field.getInitialExceptions());
	}

	@Test
	public void testGetInitialType()
	{
		assertEquals(InterfaceItemFieldInterface.class, AnMandatoryItem.field.getInitialType());
	}

	@Test
	public void testIsFinal()
	{
		assertEquals(true, AnFinalItem.field.isFinal());
	}

	@Test
	public void testIsFinalFalse()
	{
		assertEquals(false, AnMandatoryItem.field.isFinal());
	}

	public void testIsInitial()
	{
		assertEquals(true, AnMandatoryItem.field.isInitial());
	}

	@Test
	public void testIsMandatory()
	{
		assertEquals(true, AnMandatoryItem.field.isMandatory());
	}

	@Test
	public void testIsMandatoryFalse()
	{
		assertEquals(false, AnOptionalItem.field.isMandatory());
	}
}
