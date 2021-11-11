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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopyMultiTargetMandatoryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);

	private static final CopyConstraint constraintA = (CopyConstraint)Source.TYPE.getFeature("copyCopyFromtargetA");
	private static final CopyConstraint constraintB = (CopyConstraint)Source.TYPE.getFeature("copyCopyFromtargetB");

	public CopyMultiTargetMandatoryTest()
	{
		super(MODEL);
	}

	@Test void create()
	{
		final Target targetOne = new Target(1);
		final Target targetTwo = new Target(2);
		assertEquals(1, new Source(targetOne, targetOne).getCopy());
		assertEquals(1, new Source(targetOne, new Target(1)).getCopy());
		try
		{
			new Source(targetOne, targetTwo);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				null,
				1, 2,
				targetOne, targetTwo,
				"copy violation for Source.copyCopyFromtargetA and Source.copyCopyFromtargetB, " +
					"expected '1' from target "+targetOne+" but also '2' from target "+targetTwo,
				e
			);
		}
	}

	@Test void createGeneric()
	{
		try
		{
			new Source(
				SetValue.map(Source.targetA, new Target(1)),
				SetValue.map(Source.targetB, new Target(2)),
				SetValue.map(Source.copy, null)
			);
			fail();
		}
		catch (final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for Source.copy", e.getMessage());
		}
		final Target targetOne = new Target(1);
		try
		{
			new Source(
				SetValue.map(Source.targetA, targetOne),
				SetValue.map(Source.targetB, new Target(2)),
				SetValue.map(Source.copy, 3)
			);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertFails(
				constraintA,
				null,
				1, 3, targetOne,
				"copy violation for Source.copyCopyFromtargetA, expected '1' from target "+targetOne+", but was '3'",
				e
			);
		}
	}

	@Test void set()
	{
		final Target target6 = new Target(6);
		final Target target7a = new Target(7);
		final Target target7b = new Target(7);
		final Source source = new Source(new Target(7), target7b);
		source.setTargetA(target7a);
		try
		{
			source.setTargetA(target6);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				6, 7,
				target6, target7b,
				"copy violation on " + source + " " +
					"for Source.copyCopyFromtargetA and Source.copyCopyFromtargetB, " +
					"expected '6' from target "+target6+" but also '7' from target "+target7b,
				e
			);
		}
		assertEquals(7, source.getCopy());
		assertEquals(target7a, source.getTargetA());
		try
		{
			source.setTargetB(target6);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				7, 6,
				target7a, target6,
				"copy violation on " + source + " " +
					"for Source.copyCopyFromtargetA and Source.copyCopyFromtargetB, " +
					"expected '7' from target "+target7a+" but also '6' from target "+target6,
				e
			);
		}
		assertEquals(7, source.getCopy());
		assertEquals(target7b, source.getTargetB());
	}

	@Test void setBoth()
	{
		final Source source = new Source(new Target(0), new Target(0));
		assertEquals(0, source.getCopy());
		source.set(
			SetValue.map(Source.targetA, new Target(1)),
			SetValue.map(Source.targetB, new Target(1))
		);
		assertEquals(1, source.getCopy());
	}

	@Test void setBothFails()
	{
		final Target t0a = new Target(0);
		final Target t0b = new Target(0);
		final Target t1 = new Target(1);
		final Source source = new Source(t0a, t0a);
		assertEquals(0, source.getCopy());
		try
		{
			source.set(
				SetValue.map(Source.targetA, t0b),
				SetValue.map(Source.targetB, t1)
			);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				0, 1,
				t0b, t1,
				"copy violation on " + source + " " +
					"for " + constraintA + " and " + constraintB + ", " +
					"expected '0' from target "+t0b+" but also '1' from target "+t1,
				e
			);
		}
		assertEquals(0, source.getCopy());
		assertEquals(t0a, source.getTargetA());
		assertEquals(t0a, source.getTargetB());
	}

	@WrapperType(comments=false, indent=2)
	private static final class Source extends Item
	{
		static final ItemField<Target> targetA = ItemField.create(Target.class);
		static final ItemField<Target> targetB = ItemField.create(Target.class);

		static final IntegerField copy = new IntegerField().copyFrom(targetA).copyFrom(targetB);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Source(
					@javax.annotation.Nonnull final Target targetA,
					@javax.annotation.Nonnull final Target targetB)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Source.targetA,targetA),
				com.exedio.cope.SetValue.map(Source.targetB,targetB),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Target getTargetA()
		{
			return Source.targetA.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTargetA(@javax.annotation.Nonnull final Target targetA)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.targetA.set(this,targetA);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Target getTargetB()
		{
			return Source.targetB.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTargetB(@javax.annotation.Nonnull final Target targetB)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.targetB.set(this,targetB);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getCopy()
		{
			return Source.copy.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static final class Target extends Item
	{
		static final IntegerField copy = new IntegerField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Target(
					final int copy)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Target.copy,copy),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getCopy()
		{
			return Target.copy.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

}
