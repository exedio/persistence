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

import static com.exedio.cope.pattern.PartOf.getDeclaredPartOfs;
import static com.exedio.cope.pattern.PartOf.getPartOfs;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
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

	@WrapperIgnore
	static class SuperContainer extends Item
	{
		static final Type<SuperContainer> TYPE = TypesBound.newType(SuperContainer.class);
		private static final long serialVersionUID = 1l;
		SuperContainer(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static class Container extends SuperContainer
	{
		static final Type<Container> TYPE = TypesBound.newType(Container.class);
		private static final long serialVersionUID = 1l;
		Container(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static class SubContainer extends Container
	{
		static final Type<SubContainer> TYPE = TypesBound.newType(SubContainer.class);
		private static final long serialVersionUID = 1l;
		SubContainer(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static class Order extends Item
	{
		static final Type<Order> TYPE = TypesBound.newType(Order.class);
		private static final long serialVersionUID = 1l;
		Order(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static class Part extends Item
	{
		static final ItemField<Container> container = ItemField.create(Container.class);
		static final PartOf<Container> parts = PartOf.create(container);

		static final ItemField<Container> containerOrdered = ItemField.create(Container.class);
		static final ItemField<Order> order = ItemField.create(Order.class);
		static final PartOf<Container> partsOrdered = PartOf.create(containerOrdered, order);

		static final Type<Part> TYPE = TypesBound.newType(Part.class);
		private static final long serialVersionUID = 1l;
		Part(final ActivationParameters ap) { super(ap); }
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(
			SuperContainer.TYPE, Container.TYPE, SubContainer.TYPE,
			Order.TYPE, Part.TYPE);
}
