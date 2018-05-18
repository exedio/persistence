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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class MultiItemFieldErrorTest
{
	@Test void testCreateNoInterface()
	{
		try
		{
			MultiItemField.create(null, AnotherItem1.class, AnotherItem2.class);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test void testCreateNoComponentClass()
	{
		try
		{
			MultiItemField.create(MultiItemFieldValue.class, new Class[]
					{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 componentClasses";
			assertEquals(expected, e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test void testCreateOnlyOneComponentClass()
	{
		try
		{
			MultiItemField.create(MultiItemFieldValue.class, new Class[]
					{MultiItemFieldComponentA.class});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 componentClasses";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test void testCreateNull()
	{
		try
		{
			MultiItemField.create(MultiItemFieldValue.class,
					null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			final String expected = "componentClass";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test void testCreateNotAssignable()
	{
		try
		{
			MultiItemField.create(MultiItemFieldValue.class,
					AnotherItem1.class, AnotherItem2.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "valueClass >"+MultiItemFieldValue.class
					+"< must be assignable from componentClass >"+AnotherItem1.class+"<";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test void testCreateComponentClassesNotAllowedToBeSuperClassesOfEachOther()
	{
		try
		{
			MultiItemField.create(MultiItemFieldValue.class,
					MultiItemFieldComponentA.class,
					MultiItemFieldComponentASub.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "componentClasses must not be super-classes of each other: "
					+MultiItemFieldComponentA.class+" is assignable from "
					+MultiItemFieldComponentASub.class+"";
			assertEquals(expected, e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNullComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("componentClass", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNoSuchComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(MultiItemFieldComponentC.class, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"illegal componentClass class com.exedio.cope.pattern.MultiItemFieldComponentC, " +
				"must be one of [" +
				"class com.exedio.cope.pattern.MultiItemFieldComponentA, " +
				"class com.exedio.cope.pattern.MultiItemFieldComponentB].",
				e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNullCopy()
	{
		final MultiItemField<?> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(MultiItemFieldComponentA.class, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("copyTo", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test void testNonItem()
	{
		try
		{
			MultiItemField.create(
					MultiItemFieldValue.class,
					new Class[]{MultiItemFieldComponentA.class, MultiItemFieldComponentB.class, NonItemMultiItemFieldValue.class}
			);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("is not a subclass of " + Item.class.getName() + ": " + NonItemMultiItemFieldValue.class.getName(), e.getMessage());
		}
	}

	@WrapperIgnore
	static final class AnotherItem1 extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;
	}

	@WrapperIgnore
	static final class AnotherItem2 extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;
	}

	static final class NonItemMultiItemFieldValue implements MultiItemFieldValue
	{
		private static final long serialVersionUID = 1l;
	}
}
