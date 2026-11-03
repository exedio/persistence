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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CopeNoTypeTopTest extends TestWithEnvironment
{
	@Test void test()
	{
		assertEquals(List.of(MyType.TYPE), MODEL.getTypes());
		assertEquals(null, MyType.TYPE.getSupertype());
		assertEquals(List.of(), MyType.TYPE.getSubtypes());
		assertEquals(MyType.class, MyType.TYPE.getJavaClass());
		assertEquals(MyType.TYPE, TypesBound.forClass(MyType.class));
		assertFails(() -> TypesBound.forClass(NoType.class), IllegalArgumentException.class, "there is no type for class " + NoType.class.getName());
		assertEquals(List.of(MyType.field), MyType.TYPE.getDeclaredFields());
		assertEquals(List.of(MyType.field), MyType.TYPE.getFields());
		assertEquals(MyType.TYPE, MyType.field.getType());
		assertFails(NoType.fieldNoType::getType, IllegalStateException.class, "feature not mounted"); // TODO should work

		final var item = new MyType("one");
		assertEquals("one", item.getField());
		assertEquals(List.of(item), MyType.TYPE.search(MyType.field.is("one")));
		item.deleteCopeItem();

		assertFails(
				() -> TypesBound.newTypeAbstract(NoType.class),
				IllegalArgumentException.class,
				"class is annotated with @CopeNoType: " + NoType.class.getName());
	}

	@CopeNoType
	@WrapperType(indent=2, comments=false)
	private abstract static class NoType extends Item
	{
		@WrapperIgnore
		static final StringField fieldNoType = new StringField();

		@com.exedio.cope.instrument.Generated
		protected NoType(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		protected NoType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyType extends NoType
	{
		static final StringField field = new StringField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyType(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyType.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyType(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return MyType.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyType.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyType> TYPE = com.exedio.cope.TypesBound.newType(MyType.class,MyType::new);

		@com.exedio.cope.instrument.Generated
		private MyType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyType.TYPE);

	CopeNoTypeTopTest()
	{
		super(MODEL);
	}
}
