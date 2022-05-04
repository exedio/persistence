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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.PartOf.orderBy;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

/**
 * @see PartOfSuperContainerTest
 */
public class PartOfSuperOrderTest extends TestWithEnvironment
{
	@Test
	void test()
	{
		assertSame(Sub.container, Sub.parts.getContainer());
		assertEqualsUnmodifiable(asList(orderBy(Super.order)), Sub.parts.getOrders());
		assertEquals(asList(Sub.parts), PartOf.getDeclaredPartOfs(Container.TYPE));
		assertEquals(asList(Sub.parts), PartOf.getPartOfs        (Container.TYPE));
		assertEquals(asList(), PartOf.getDeclaredPartOfs(Super.TYPE));
		assertEquals(asList(), PartOf.getPartOfs        (Super.TYPE));
		assertEquals(asList(), PartOf.getDeclaredPartOfs(Sub.TYPE));
		assertEquals(asList(), PartOf.getPartOfs        (Sub.TYPE));

		final Container c = new Container();
		final Sub s7 = new Sub(7, c);
		final Sub s3 = new Sub(3, c);
		final Sub s9 = new Sub(9, c);
		assertEquals(
				asList(s3, s7, s9),
				Sub.parts.getParts(c)
		);
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static class Super extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField order = new IntegerField();

		@com.exedio.cope.instrument.Generated
		protected Super(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Super> TYPE = com.exedio.cope.TypesBound.newType(Super.class,Super::new);

		@com.exedio.cope.instrument.Generated
		protected Super(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Sub extends Super
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final ItemField<Container> container = ItemField.create(Container.class).toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		static final PartOf<Container> parts = PartOf.create(container, order);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Sub(
					final int order,
					@javax.annotation.Nonnull final Container container)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.pattern.PartOfSuperOrderTest.Super.order,order),
				com.exedio.cope.SetValue.map(Sub.container,container),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected Sub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Sub> TYPE = com.exedio.cope.TypesBound.newType(Sub.class,Sub::new);

		@com.exedio.cope.instrument.Generated
		protected Sub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Container extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Container()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

		@com.exedio.cope.instrument.Generated
		protected Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Container.TYPE, Super.TYPE, Sub.TYPE);

	PartOfSuperOrderTest()
	{
		super(MODEL);
	}
}
