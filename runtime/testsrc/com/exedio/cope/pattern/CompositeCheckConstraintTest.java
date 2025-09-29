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

import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.AbstractType;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CompositeCheckConstraintTest
{
	@Test void testGetAbstractType()
	{
		assertEquals(MyComposite.alpha.getAbstractType(), MyComposite.gamma.getAbstractType());
		assertEquals(MyComposite.alpha.getAbstractType(), MyComposite.check.getAbstractType());
	}

	@Test void testGetName()
	{
		assertEquals("alpha", MyComposite.alpha.getName());
		assertEquals("gamma", MyComposite.gamma.getName());
		assertEquals("check", MyComposite.check.getName());
	}

	@Test void testToString()
	{
		assertEquals(MyComposite.class.getName() + "#alpha", MyComposite.alpha.toString());
		assertEquals(MyComposite.class.getName() + "#gamma", MyComposite.gamma.toString());
		assertEquals(MyComposite.class.getName() + "#check", MyComposite.check.toString());
	}

	@Test void testGetID()
	{
		assertFails(MyComposite.alpha::getID, IllegalStateException.class, NOT_MOUNTED + MyComposite.alpha);
		assertFails(MyComposite.gamma::getID, IllegalStateException.class, NOT_MOUNTED + MyComposite.gamma);
		assertFails(MyComposite.check::getID, IllegalStateException.class, NOT_MOUNTED + MyComposite.check);
	}

	@Test void testGetType()
	{
		assertFails(MyComposite.alpha::getType, IllegalStateException.class, NOT_MOUNTED + MyComposite.alpha);
		assertFails(MyComposite.gamma::getType, IllegalStateException.class, NOT_MOUNTED + MyComposite.gamma);
		assertFails(MyComposite.check::getType, IllegalStateException.class, NOT_MOUNTED + MyComposite.check);
	}

	private static final String NOT_MOUNTED = "feature not mounted to a type, but to com.exedio.cope.pattern.CompositeType: ";

	@Test void testGetFeatures()
	{
		final AbstractType<?> type = MyComposite.alpha.getAbstractType();
		final List<Feature> expected = asList(
				MyComposite.alpha,
				MyComposite.gamma,
				MyComposite.check);
		assertEqualsUnmodifiable(expected, type.getDeclaredFeatures());
		assertEqualsUnmodifiable(expected, type.getFeatures());

		assertSame(MyComposite.alpha, type.getDeclaredFeature("alpha"));
		assertSame(MyComposite.gamma, type.getDeclaredFeature("gamma"));
		assertSame(MyComposite.check, type.getDeclaredFeature("check"));
		assertSame(MyComposite.alpha, type.getFeature("alpha"));
		assertSame(MyComposite.gamma, type.getFeature("gamma"));
		assertSame(MyComposite.check, type.getFeature("check"));
	}

	@Test void testGetCondition()
	{
		assertEquals(
				MyComposite.alpha + "<" + MyComposite.gamma,
				MyComposite.check.getCondition().toString());
	}


	@Test void testCreateOk()
	{
		final MyComposite c = new MyComposite(1, 2);
		assertEquals(1, c.getAlpha());
		assertEquals(2, c.getGamma());
	}

	@Test void testCreateOkNull()
	{
		final MyComposite c = new MyComposite(1, null);
		assertEquals(1, c.getAlpha());
		assertEquals(null, c.getGammaInternal());
	}

	@Test void testCreateFail()
	{
		assertFails(
				() -> new MyComposite(2, 2),
				CheckViolationException.class,
				"check violation for " + MyComposite.check,
				MyComposite.check);
	}

	@Test void testSetOk()
	{
		final MyComposite c = new MyComposite(1, 2);
		c.setAlpha(0);
		assertEquals(0, c.getAlpha());
		assertEquals(2, c.getGamma());
	}

	@Test void testSetFail()
	{
		final MyComposite c = new MyComposite(1, 2);
		assertFails(
				() -> c.setAlpha(2),
				CheckViolationException.class,
				"check violation for " + MyComposite.check,
				MyComposite.check);
		assertEquals(1, c.getAlpha());
		assertEquals(2, c.getGamma());
	}

	@Test void testSetMultiOk()
	{
		final MyComposite c = new MyComposite(1, 2);
		c.set(
				SetValue.map(MyComposite.alpha, 3),
				SetValue.map(MyComposite.gamma, 4));
		assertEquals(3, c.getAlpha());
		assertEquals(4, c.getGamma());
	}

	@Test void testSetMultiFail()
	{
		final MyComposite c = new MyComposite(1, 2);
		assertFails(
				() -> c.set(
						SetValue.map(MyComposite.alpha, 3),
						SetValue.map(MyComposite.gamma, 3)),
				CheckViolationException.class,
				"check violation for " + MyComposite.check,
				MyComposite.check);
		assertEquals(1, c.getAlpha());
		assertEquals(2, c.getGamma());
	}

	@WrapperType(indent=2)
	static final class MyComposite extends Composite
	{
		static final IntegerField alpha = new IntegerField();
		@WrapperInitial
		@Wrapper(wrap="get", visibility=PACKAGE, internal=true)
		static final IntegerField gamma = new IntegerField().optional();

		static final CheckConstraint check = new CheckConstraint(alpha.less(gamma));

		int getGamma()
		{
			final Integer result = getGammaInternal();
			assertNotNull(result);
			return result;
		}

		/**
		 * Creates a new MyComposite with all the fields initially needed.
		 * @param alpha the initial value for field {@link #alpha}.
		 * @param gamma the initial value for field {@link #gamma}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyComposite(
					final int alpha,
					@javax.annotation.Nullable final java.lang.Integer gamma)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyComposite.alpha,alpha),
				com.exedio.cope.SetValue.map(MyComposite.gamma,gamma),
			});
		}

		/**
		 * Creates a new MyComposite and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private MyComposite(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #alpha}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getAlpha()
		{
			return getMandatory(MyComposite.alpha);
		}

		/**
		 * Sets a new value for {@link #alpha}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAlpha(final int alpha)
		{
			set(MyComposite.alpha,alpha);
		}

		/**
		 * Returns the value of {@link #gamma}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getGammaInternal()
		{
			return get(MyComposite.gamma);
		}

		/**
		 * Sets a new value for {@link #gamma}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGamma(@javax.annotation.Nullable final java.lang.Integer gamma)
		{
			set(MyComposite.gamma,gamma);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@SuppressWarnings("unused") // OK: just loads CompositeType
	private static final CompositeField<?> F = CompositeField.create(MyComposite.class);
}
