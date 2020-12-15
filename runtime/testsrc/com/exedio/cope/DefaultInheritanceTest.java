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

import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DefaultInheritanceTest extends TestWithEnvironment
{
	public DefaultInheritanceTest()
	{
		super(MODEL);
	}

	@Test void testModel()
	{
		final Type<Alpha> alpha = Alpha.TYPE;
		final Type<Beta > beta  = Beta .TYPE;
		final Type<Gamma> gamma = Gamma.TYPE;
		final BooleanField field = Alpha.field;

		assertEquals(list(field), alpha.getFields());
		assertEquals(list(field), beta .getFields());
		assertEquals(list(field), gamma.getFields());
		assertEquals(list(field), alpha.getDeclaredFields());
		assertEquals(list(), beta .getDeclaredFields());
		assertEquals(list(), gamma.getDeclaredFields());

		assertEquals(list(alpha.getThis(), field), alpha.getFeatures());
		assertEquals(list(beta .getThis(), field), beta .getFeatures());
		assertEquals(list(gamma.getThis(), field), gamma.getFeatures());
		assertEquals(list(alpha.getThis(), field), alpha.getDeclaredFeatures());
		assertEquals(list(beta .getThis()),        beta .getDeclaredFeatures());
		assertEquals(list(gamma.getThis()),        gamma.getDeclaredFeatures());
	}

	@Test void testBeta()
	{
		final Beta item = new Beta();
		assertEquals(false, item.getField());
	}

	@Test void testGamma()
	{
		final Gamma item = new Gamma();
		assertEquals(false, item.getField());
	}

	private abstract static class Alpha extends Item
	{
		static final BooleanField field = new BooleanField().defaultTo(false);


	/**
	 * Creates a new Alpha with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private Alpha()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new Alpha and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Alpha(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean getField()
	{
		return Alpha.field.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setField(final boolean field)
	{
		Alpha.field.set(this,field);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for alpha.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<Alpha> TYPE = com.exedio.cope.TypesBound.newType(Alpha.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Alpha(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	private static class Beta extends Alpha
	{


	/**
	 * Creates a new Beta with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private Beta()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new Beta and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Beta(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for beta.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<Beta> TYPE = com.exedio.cope.TypesBound.newType(Beta.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Beta(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	private static class Gamma extends Beta
	{


	/**
	 * Creates a new Gamma with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private Gamma()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new Gamma and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Gamma(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for gamma.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<Gamma> TYPE = com.exedio.cope.TypesBound.newType(Gamma.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Gamma(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	public static final Model MODEL = new Model(Alpha.TYPE, Beta.TYPE, Gamma.TYPE);
}
