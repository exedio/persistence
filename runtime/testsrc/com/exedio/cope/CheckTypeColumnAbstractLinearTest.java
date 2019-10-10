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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CheckTypeColumnAbstractLinearTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
			Type0A.TYPE,
				Type01C.TYPE,
				Type02A.TYPE,
					Type021C.TYPE,
			TypeRef.TYPE);

	private static final ItemFunction<?> this0A   = Type0A  .TYPE.getThis();
	private static final ItemFunction<?> this01C  = Type01C .TYPE.getThis();
	private static final ItemFunction<?> this02A  = Type02A .TYPE.getThis();
	private static final ItemFunction<?> this021C = Type021C.TYPE.getThis();

	private static final ItemFunction<?> ref0A   = TypeRef.ref0A;
	private static final ItemFunction<?> ref01C  = TypeRef.ref01C;
	private static final ItemFunction<?> ref02A  = TypeRef.ref02A;
	private static final ItemFunction<?> ref021C = TypeRef.ref021C;

	public CheckTypeColumnAbstractLinearTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		assertEquals(false, this0A  .needsCheckTypeColumn());
		assertEquals(true,  this01C .needsCheckTypeColumn());
		assertEquals(true,  this02A .needsCheckTypeColumn());
		assertEquals(false, this021C.needsCheckTypeColumn());

		assertEquals(true,  ref0A  .needsCheckTypeColumn());
		assertEquals(false, ref01C .needsCheckTypeColumn());
		assertEquals(false, ref02A .needsCheckTypeColumn());
		assertEquals(false, ref021C.needsCheckTypeColumn());

		assertNotNeeded(this0A);
		assertNotNeeded(this021C);
		assertNotNeeded(ref01C);
		assertNotNeeded(ref02A);
		assertNotNeeded(ref021C);


		assertEquals(0, this01C.checkTypeColumnL());
		assertEquals(0, this02A.checkTypeColumnL());
		assertEquals(0, ref0A  .checkTypeColumnL());

		new TypeRef(new Type01C(1));
		assertEquals(0, this01C.checkTypeColumnL());
		assertEquals(0, this02A.checkTypeColumnL());
		assertEquals(0, ref0A  .checkTypeColumnL());

		new TypeRef(new Type01C(2));
		assertEquals(0, this01C.checkTypeColumnL());
		assertEquals(0, this02A.checkTypeColumnL());
		assertEquals(0, ref0A  .checkTypeColumnL());

		new TypeRef(new Type021C(5));
		assertEquals(0, this01C.checkTypeColumnL());
		assertEquals(0, this02A.checkTypeColumnL());
		assertEquals(0, ref0A  .checkTypeColumnL());

		new TypeRef(new Type021C(5));
		assertEquals(0, this01C.checkTypeColumnL());
		assertEquals(0, this02A.checkTypeColumnL());
		assertEquals(0, ref0A  .checkTypeColumnL());
	}

	private static void assertNotNeeded(final ItemFunction<?> f)
	{
		try
		{
			f.checkTypeColumnL();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for " + f, e.getMessage());
		}
	}


	abstract static class Type0A extends Item
	{

	/**
	 * Creates a new Type0A with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Type0A()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new Type0A and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Type0A(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for type0A.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Type0A> TYPE = com.exedio.cope.TypesBound.newType(Type0A.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Type0A(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	static final class Type01C extends Type0A
	{
		static final IntegerField intField = new IntegerField();

	/**
	 * Creates a new Type01C with all the fields initially needed.
	 * @param intField the initial value for field {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Type01C(
				final int intField)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			Type01C.intField.map(intField),
		});
	}

	/**
	 * Creates a new Type01C and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private Type01C(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getIntField()
	{
		return Type01C.intField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntField(final int intField)
	{
		Type01C.intField.set(this,intField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for type01C.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Type01C> TYPE = com.exedio.cope.TypesBound.newType(Type01C.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Type01C(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	abstract static class Type02A extends Type0A
	{

	/**
	 * Creates a new Type02A with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Type02A()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new Type02A and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Type02A(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for type02A.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Type02A> TYPE = com.exedio.cope.TypesBound.newType(Type02A.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Type02A(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	static final class Type021C extends Type02A
	{
		static final IntegerField intField = new IntegerField();

	/**
	 * Creates a new Type021C with all the fields initially needed.
	 * @param intField the initial value for field {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Type021C(
				final int intField)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			Type021C.intField.map(intField),
		});
	}

	/**
	 * Creates a new Type021C and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private Type021C(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getIntField()
	{
		return Type021C.intField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntField(final int intField)
	{
		Type021C.intField.set(this,intField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for type021C.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Type021C> TYPE = com.exedio.cope.TypesBound.newType(Type021C.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Type021C(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	static final class TypeRef extends Item
	{
		static final ItemField<Type0A  > ref0A   = ItemField.create(Type0A  .class);
		static final ItemField<Type01C > ref01C  = ItemField.create(Type01C .class).optional();
		static final ItemField<Type02A > ref02A  = ItemField.create(Type02A .class).optional();
		static final ItemField<Type021C> ref021C = ItemField.create(Type021C.class).optional();

	/**
	 * Creates a new TypeRef with all the fields initially needed.
	 * @param ref0A the initial value for field {@link #ref0A}.
	 * @throws com.exedio.cope.MandatoryViolationException if ref0A is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	TypeRef(
				@javax.annotation.Nonnull final Type0A ref0A)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			TypeRef.ref0A.map(ref0A),
		});
	}

	/**
	 * Creates a new TypeRef and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private TypeRef(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #ref0A}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Type0A getRef0A()
	{
		return TypeRef.ref0A.get(this);
	}

	/**
	 * Sets a new value for {@link #ref0A}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRef0A(@javax.annotation.Nonnull final Type0A ref0A)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		TypeRef.ref0A.set(this,ref0A);
	}

	/**
	 * Returns the value of {@link #ref01C}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Type01C getRef01C()
	{
		return TypeRef.ref01C.get(this);
	}

	/**
	 * Sets a new value for {@link #ref01C}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRef01C(@javax.annotation.Nullable final Type01C ref01C)
	{
		TypeRef.ref01C.set(this,ref01C);
	}

	/**
	 * Returns the value of {@link #ref02A}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Type02A getRef02A()
	{
		return TypeRef.ref02A.get(this);
	}

	/**
	 * Sets a new value for {@link #ref02A}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRef02A(@javax.annotation.Nullable final Type02A ref02A)
	{
		TypeRef.ref02A.set(this,ref02A);
	}

	/**
	 * Returns the value of {@link #ref021C}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Type021C getRef021C()
	{
		return TypeRef.ref021C.get(this);
	}

	/**
	 * Sets a new value for {@link #ref021C}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRef021C(@javax.annotation.Nullable final Type021C ref021C)
	{
		TypeRef.ref021C.set(this,ref021C);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for typeRef.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<TypeRef> TYPE = com.exedio.cope.TypesBound.newType(TypeRef.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private TypeRef(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}
