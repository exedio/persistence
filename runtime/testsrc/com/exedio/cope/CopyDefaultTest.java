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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopyDefaultTest extends TestWithEnvironment
{
	public CopyDefaultTest()
	{
		super(MODEL);
	}

	@Test void testProvideAllOk()
	{
		final Target target = new Target("fieldValue");
		final Source source = Source.create(target, "fieldValue");
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());

		final Target targetSet = new Target("fieldValueSet");
		source.setTargetAndField(targetSet, "fieldValueSet");
		assertEquals(targetSet, source.getTarget());
		assertEquals("fieldValueSet", source.getField());
	}

	@Test void testProvideAllWrongCreate()
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
					"copy violation for " + constraint + ", " +
					"expected 'fieldValueT' " +
					"from target " + target + ", " +
					"but was 'fieldValueC'", e);
		}
		assertContains(Source.TYPE.search());
	}

	@Test void testProvideAllWrongSet()
	{
		final Target target = new Target("fieldValue");
		final Source source = Source.create(target, "fieldValue");
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());

		try
		{
			source.setTargetAndField(target, "fieldValueSet");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraint, source, "fieldValue", "fieldValueSet", target,
					"copy violation on " + source + " " +
					"for " + constraint + ", " +
					"expected 'fieldValue' " +
					"from target " + target + ", " +
					"but was 'fieldValueSet'", e);
		}
		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());
	}

	@Test void testOmitCopy()
	{
		final Target target = new Target("fieldValue");
		final Source source = Source.create(target);
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());

		final Target targetSet = new Target("fieldValueSet");
		source.setTarget(targetSet);
		assertEquals(targetSet, source.getTarget());
		assertEquals("fieldValueSet", source.getField());
	}

	@Test void testOmitCopyNullCreate()
	{
		final Target target = new Target((String)null);
		final Source source = Source.create(target);
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals(null, source.getField());
	}

	@Test void testOmitCopyNullSet()
	{
		final Target target = new Target("fieldValue");
		final Source source = Source.create(target);
		assertContains(source, Source.TYPE.search());

		assertEquals(target, source.getTarget());
		assertEquals("fieldValue", source.getField());

		final Target targetSet = new Target((String)null);
		source.setTarget(targetSet);
		assertEquals(targetSet, source.getTarget());
		assertEquals(null, source.getField());
	}

	@Test void testOmitTarget()
	{
		final Source source = Source.create("fieldValue");
		assertContains(source, Source.TYPE.search());

		assertEquals(null, source.getTarget());
		assertEquals("fieldValue", source.getField());

		source.setField("fieldValueSet");
		assertEquals(null, source.getTarget());
		assertEquals("fieldValueSet", source.getField());
	}

	@Test void testOmitTargetAndCopy()
	{
		final Source source = Source.create();
		assertContains(source, Source.TYPE.search());

		assertEquals(null, source.getTarget());
		assertEquals("defaultValue", source.getField());
	}


	@WrapperType(constructor=PRIVATE, indent=2)
	private static final class Source extends Item
	{
		@WrapperInitial
		static final ItemField<Target> target = ItemField.create(Target.class).optional();
		@WrapperInitial
		static final StringField field = new StringField().optional().copyFrom(target).defaultTo("defaultValue");


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

		void setTargetAndField(final Target target, final String field)
		{
			set(
				Source.target.map(target),
				Source.field.map(field));
		}

		/**
		 * Creates a new Source with all the fields initially needed.
		 * @param target the initial value for field {@link #target}.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #target}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Target getTarget()
		{
			return Source.target.get(this);
		}

		/**
		 * Sets a new value for {@link #target}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTarget(@javax.annotation.Nullable final Target target)
		{
			Source.target.set(this,target);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return Source.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			Source.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for source.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class Target extends Item
	{
		static final StringField field = new StringField().toFinal().optional();

		/**
		 * Creates a new Target with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Target(
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
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return Target.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for target.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final CopyConstraint constraint = (CopyConstraint)Source.TYPE.getFeature("fieldCopyFromtarget");

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);
}
