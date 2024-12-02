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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @see PartOfSuperOrderTest
 */
public class PartOfSuperContainerTest extends TestWithEnvironment
{
	@Test
	void test()
	{
		assertSame(Super.container, Sub.parts.getContainer());
		assertEquals(List.of(orderBy(Sub.order)), Sub.parts.getOrders());
		assertEquals(List.of(Sub.parts), PartOf.getDeclaredPartOfs(Container.TYPE));
		assertEquals(List.of(Sub.parts), PartOf.getPartOfs        (Container.TYPE));
		assertEquals(List.of(), PartOf.getDeclaredPartOfs(Super.TYPE));
		assertEquals(List.of(), PartOf.getPartOfs        (Super.TYPE));
		assertEquals(List.of(), PartOf.getDeclaredPartOfs(Sub.TYPE));
		assertEquals(List.of(), PartOf.getPartOfs        (Sub.TYPE));

		final Container c = new Container();
		final Sub s7 = new Sub(c, 7);
		final Sub s3 = new Sub(c, 3);
		final Sub s9 = new Sub(c, 9);
		assertEquals(
				List.of(s3, s7, s9),
				Sub.parts.getParts(c)
		);
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static class Super extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final ItemField<Container> container = ItemField.create(Container.class).toFinal();

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
		static final IntegerField order = new IntegerField();

		@WrapperIgnore
		static final PartOf<?> parts = PartOf.create(container, order);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Sub(
					@javax.annotation.Nonnull final com.exedio.cope.pattern.PartOfSuperContainerTest.Container container,
					final int order)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.pattern.PartOfSuperContainerTest.Super.container,container),
				com.exedio.cope.SetValue.map(Sub.order,order),
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

	private static final Model MODEL = new Model(Super.TYPE, Sub.TYPE, Container.TYPE);

	PartOfSuperContainerTest()
	{
		super(MODEL);
	}
}
