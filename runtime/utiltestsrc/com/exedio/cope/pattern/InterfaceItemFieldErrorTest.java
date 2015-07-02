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

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class InterfaceItemFieldErrorTest extends CopeAssert
{
	@SuppressWarnings(
	{"unchecked", "rawtypes"})
	@Test
	public void testCreateNoClass()
	{
		try
		{
			InterfaceItemField.create(InterfaceItemFieldInterface.class, new Class[]
			{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 classes";
			assertEquals(expected, e.getMessage());
		}
	}

	@SuppressWarnings(
	{"unchecked", "rawtypes"})
	@Test
	public void testCreateOnlyOneClass()
	{
		try
		{
			InterfaceItemField.create(InterfaceItemFieldInterface.class, new Class[]
			{InterfaceItemFieldInterfaceImplementationA.class});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 classes";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test
	public void testCreateNull()
	{
		try
		{
			InterfaceItemField.create(InterfaceItemFieldInterface.class,
			null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			final String expected = "no null values for classes allowed";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test
	public void testCreateNotAssignable()
	{
		try
		{
			InterfaceItemField.create(InterfaceItemFieldInterface.class,
			AnotherItem1.class, AnotherItem2.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "common interface >"+InterfaceItemFieldInterface.class
					+"< must be assignable from class >"+AnotherItem1.class+"<";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test
	public void testCreateClassesNotAllowedToBeSuperClassesOfEachOther()
	{
		try
		{
			InterfaceItemField.create(InterfaceItemFieldInterface.class,
					InterfaceItemFieldInterfaceImplementationA.class,
					InterfaceItemFieldInterfaceImplementationASub.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "Classes must not be super-classes of each other: "
					+InterfaceItemFieldInterfaceImplementationA.class+" is assignable from "
					+InterfaceItemFieldInterfaceImplementationASub.class+"";
			assertEquals(expected, e.getMessage());
		}
	}

	static final class AnotherItem1 extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;
	}

	static final class AnotherItem2 extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;
	}
}
