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

package com.exedio.cope;

import static com.exedio.cope.CopySimpleTest.assertFails;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class CopyDefaultTest extends TestWithEnvironment
{
	public CopyDefaultTest()
	{
		super(MODEL);
	}

	@Test public void testProvideAllOk()
	{
		final Target target = new Target("fieldValue");
		final Source source = Source.create(target, "fieldValue");
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());
	}

	@Test public void testProvideAllWrong()
	{
		final Target target = new Target("fieldValueT");
		try
		{
			Source.create(target, "fieldValueC");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraint, "fieldValueT", "fieldValueC", target,
					"copy violation on " + constraint + ", " +
					"expected 'fieldValueT' " +
					"from target " + target + ", " +
					"but was 'fieldValueC'", e);
		}
		assertContains(Source.TYPE.search());
	}

	@Test public void testOmitCopy()
	{
		final Target target = new Target("fieldValue");
		try
		{
			Source.create(target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraint, "fieldValue", "defaultValue", target,
					"copy violation on " + constraint + ", " +
					"expected 'fieldValue' " +
					"from target " + target + ", " +
					"but was 'defaultValue'", e);
		}
		assertContains(Source.TYPE.search());
	}

	@Test public void testOmitCopyNull()
	{
		final Target target = new Target((String)null);
		try
		{
			Source.create(target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraint, null, "defaultValue", target,
					"copy violation on " + constraint + ", " +
					"expected null " +
					"from target " + target + ", " +
					"but was 'defaultValue'", e);
		}
		assertContains(Source.TYPE.search());
	}

	@Test public void testOmitTarget()
	{
		final Source source = Source.create("fieldValue");
		assertContains(source, Source.TYPE.search());

		assertEquals(null, source.getTarget());
		assertEquals("fieldValue", source.getField());
	}

	@Test public void testOmitTargetAndCopy()
	{
		final Source source = Source.create();
		assertContains(source, Source.TYPE.search());

		assertEquals(null, source.getTarget());
		assertEquals("defaultValue", source.getField());
	}


	@WrapperType(constructor=PRIVATE, indent=2)
	static final class Source extends Item
	{
		static final ItemField<Target> target = ItemField.create(Target.class).toFinal().optional();
		@WrapperInitial
		static final StringField field = new StringField().toFinal().optional().copyFrom(target).defaultTo("defaultValue");


		static Source create()
		{
			return new Source(new SetValue<?>[]{});
		}

		static Source create(final Target target)
		{
			return new Source(new SetValue<?>[]{
				Source.target.map(target),
			});
		}

		static Source create(final String field)
		{
			return new Source(new SetValue<?>[]{
				Source.field.map(field),
			});
		}

		static Source create(final Target target, final String field)
		{
			return new Source(target, field);
		}

		/**
		 * Creates a new Source with all the fields initially needed.
		 * @param target the initial value for field {@link #target}.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		private Source(
					@javax.annotation.Nullable final Target target,
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Source.target.map(target),
				Source.field.map(field),
			});
		}

		/**
		 * Creates a new Source and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private Source(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #target}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final Target getTarget()
		{
			return Source.target.get(this);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.String getField()
		{
			return Source.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for source.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class Target extends Item
	{
		static final StringField field = new StringField().toFinal().optional();

		/**
		 * Creates a new Target with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		Target(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Target.field.map(field),
			});
		}

		/**
		 * Creates a new Target and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private Target(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.String getField()
		{
			return Target.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for target.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final CopyConstraint constraint = (CopyConstraint)Source.TYPE.getFeature("fieldCopyFromtarget");

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);
}
