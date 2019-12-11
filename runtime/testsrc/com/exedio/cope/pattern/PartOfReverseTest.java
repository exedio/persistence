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
import static com.exedio.cope.pattern.PartOf.getDeclaredPartOfs;
import static com.exedio.cope.pattern.PartOf.getPartOfs;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class PartOfReverseTest
{
	@Test void testSuperContainer()
	{
		assertEqualsUnmodifiable(asList(), getDeclaredPartOfs(SuperContainer.TYPE));
		assertEqualsUnmodifiable(asList(), getPartOfs(SuperContainer.TYPE));
	}
	@Test void testContainer()
	{
		assertEqualsUnmodifiable(asList(Part.parts, Part.partsOrdered), getDeclaredPartOfs(Container.TYPE));
		assertEqualsUnmodifiable(asList(Part.parts, Part.partsOrdered), getPartOfs(Container.TYPE));
	}
	@Test void testSubContainer()
	{
		assertEqualsUnmodifiable(asList(), getDeclaredPartOfs(SubContainer.TYPE));
		assertEqualsUnmodifiable(asList(Part.parts, Part.partsOrdered), getPartOfs(SubContainer.TYPE));
	}
	@Test void testPart()
	{
		assertEqualsUnmodifiable(asList(), getDeclaredPartOfs(Part.TYPE));
		assertEqualsUnmodifiable(asList(), getPartOfs(Part.TYPE));
	}
	@Test void testOrder()
	{
		assertEqualsUnmodifiable(asList(), getDeclaredPartOfs(Order.TYPE));
		assertEqualsUnmodifiable(asList(), getPartOfs(Order.TYPE));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class SuperContainer extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<SuperContainer> TYPE = com.exedio.cope.TypesBound.newType(SuperContainer.class);

		@com.exedio.cope.instrument.Generated
		protected SuperContainer(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Container extends SuperContainer
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class);

		@com.exedio.cope.instrument.Generated
		protected Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class SubContainer extends Container
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<SubContainer> TYPE = com.exedio.cope.TypesBound.newType(SubContainer.class);

		@com.exedio.cope.instrument.Generated
		protected SubContainer(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Order extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Order> TYPE = com.exedio.cope.TypesBound.newType(Order.class);

		@com.exedio.cope.instrument.Generated
		protected Order(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Part extends Item
	{
		@WrapperIgnore
		static final ItemField<Container> container = ItemField.create(Container.class);
		@WrapperIgnore
		static final PartOf<Container> parts = PartOf.create(container);

		@WrapperIgnore
		static final ItemField<Container> containerOrdered = ItemField.create(Container.class);
		@WrapperIgnore
		static final ItemField<Order> order = ItemField.create(Order.class);
		@WrapperIgnore
		static final PartOf<Container> partsOrdered = PartOf.create(containerOrdered, order);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class);

		@com.exedio.cope.instrument.Generated
		protected Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(
			SuperContainer.TYPE, Container.TYPE, SubContainer.TYPE,
			Order.TYPE, Part.TYPE);
}
