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

import static com.exedio.cope.SchemaInfo.checkTypeColumn;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.SI;
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


		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(Type01C.TYPE) + "," + SI.tab(Type0A.TYPE) + " " +
				"WHERE " + SI.pkq(Type01C.TYPE) + "=" + SI.pkq(Type0A.TYPE) + " " +
				"AND 'Type01C'<>" + SI.typeq(Type0A.TYPE),
				checkTypeColumn(this01C));
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(Type02A.TYPE) + "," + SI.tab(Type0A.TYPE) + " " +
				"WHERE " + SI.pkq(Type02A.TYPE) + "=" + SI.pkq(Type0A.TYPE) + " " +
				"AND 'Type021C'<>" + SI.typeq(Type0A.TYPE),
				checkTypeColumn(this02A));
		final String alias1 = SchemaInfo.quoteName(model, "return");
		final String alias2 = SchemaInfo.quoteName(model, "break");
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(TypeRef.TYPE) + " " + alias1 + "," + SI.tab(Type0A.TYPE) + " " + alias2 + " " +
				"WHERE " + alias1 + "." + SI.col(TypeRef.ref0A) + "=" + alias2 + "." + SI.pk(Type0A.TYPE) + " " +
				"AND " + alias1 + "." + SI.type(TypeRef.ref0A) + "<>" + alias2 + "." + SI.type(Type0A.TYPE),
				checkTypeColumn(ref0A));
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
		assertFails(
				() -> checkTypeColumn(f),
				IllegalArgumentException.class,
				"no check for type column needed for " + f);
		assertFails(
				f::checkTypeColumnL,
				IllegalArgumentException.class,
				"no check for type column needed for " + f);
	}


	@WrapperType(indent=2, comments=false)
	private abstract static class Type0A extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Type0A()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Type0A(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Type0A> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(Type0A.class);

		@com.exedio.cope.instrument.Generated
		protected Type0A(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(indent=2, comments=false)
	private static final class Type01C extends Type0A
	{
		static final IntegerField intField = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Type01C(
					final int intField)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Type01C.intField,intField),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Type01C(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntField()
		{
			return Type01C.intField.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntField(final int intField)
		{
			Type01C.intField.set(this,intField);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Type01C> TYPE = com.exedio.cope.TypesBound.newType(Type01C.class,Type01C::new);

		@com.exedio.cope.instrument.Generated
		private Type01C(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(indent=2, comments=false)
	private abstract static class Type02A extends Type0A
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Type02A()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Type02A(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Type02A> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(Type02A.class);

		@com.exedio.cope.instrument.Generated
		protected Type02A(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(indent=2, comments=false)
	private static final class Type021C extends Type02A
	{
		static final IntegerField intField = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Type021C(
					final int intField)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Type021C.intField,intField),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Type021C(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntField()
		{
			return Type021C.intField.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntField(final int intField)
		{
			Type021C.intField.set(this,intField);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Type021C> TYPE = com.exedio.cope.TypesBound.newType(Type021C.class,Type021C::new);

		@com.exedio.cope.instrument.Generated
		private Type021C(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(indent=2, comments=false)
	private static final class TypeRef extends Item
	{
		static final ItemField<Type0A  > ref0A   = ItemField.create(Type0A  .class);
		static final ItemField<Type01C > ref01C  = ItemField.create(Type01C .class).optional();
		static final ItemField<Type02A > ref02A  = ItemField.create(Type02A .class).optional();
		static final ItemField<Type021C> ref021C = ItemField.create(Type021C.class).optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private TypeRef(
					@javax.annotation.Nonnull final Type0A ref0A)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(TypeRef.ref0A,ref0A),
			});
		}

		@com.exedio.cope.instrument.Generated
		private TypeRef(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Type0A getRef0A()
		{
			return TypeRef.ref0A.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setRef0A(@javax.annotation.Nonnull final Type0A ref0A)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			TypeRef.ref0A.set(this,ref0A);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Type01C getRef01C()
		{
			return TypeRef.ref01C.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setRef01C(@javax.annotation.Nullable final Type01C ref01C)
		{
			TypeRef.ref01C.set(this,ref01C);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Type02A getRef02A()
		{
			return TypeRef.ref02A.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setRef02A(@javax.annotation.Nullable final Type02A ref02A)
		{
			TypeRef.ref02A.set(this,ref02A);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Type021C getRef021C()
		{
			return TypeRef.ref021C.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setRef021C(@javax.annotation.Nullable final Type021C ref021C)
		{
			TypeRef.ref021C.set(this,ref021C);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<TypeRef> TYPE = com.exedio.cope.TypesBound.newType(TypeRef.class,TypeRef::new);

		@com.exedio.cope.instrument.Generated
		private TypeRef(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
