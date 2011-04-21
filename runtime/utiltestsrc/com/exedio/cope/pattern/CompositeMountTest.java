/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class CompositeMountTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			Value.string4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		try
		{
			Value.intMax4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testName()
	{
		try
		{
			Value.string4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		try
		{
			Value.intMax4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testID()
	{
		try
		{
			Value.string4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		try
		{
			Value.intMax4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testToString()
	{
		assertTrue(Value.string4.toString().startsWith(StringField.class.getName() + '@')); // TODO
		assertTrue(Value.intMax4.toString().startsWith(IntegerField.class.getName() + '@')); // TODO

		final LongField negative = new LongField();
		assertTrue(negative.toString().startsWith(LongField.class.getName() + '@'));
	}

	public void testGetAnnotation()
	{
		// TODO
		try
		{
			Value.string4.getAnnotation(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		// TODO
		try
		{
			Value.intMax4.getAnnotation(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getAnnotation(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testIsAnnotation()
	{
		// TODO
		try
		{
			Value.string4.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		// TODO
		try
		{
			Value.intMax4.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno
	{
		String value();
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno2
	{
		String value();
	}

	static final class Value extends Composite
	{
		@Anno("stringAnno")
		static final StringField string4 = new StringField().lengthMax(4);
		@Anno("intAnno")
		static final IntegerField intMax4 = new IntegerField().max(4);

		Value(final SetValue[] setValues)
		{
			super(setValues);
		}

		private static final long serialVersionUID = 1l;
	}

	/**
	 * Needed to instantiate {@link CompositeType}.
	 */
	static final CompositeField field = CompositeField.newComposite(Value.class);
}
