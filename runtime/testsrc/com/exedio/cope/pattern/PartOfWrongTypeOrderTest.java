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

import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.PartOf.orderBy;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @see PartOfWrongTypeContainerTest
 */
public class PartOfWrongTypeOrderTest
{
	@Test
	void test()
	{
		assertSame(Part.container, Part.partOf.getContainer());
		assertEquals(List.of(orderBy(Container.order)), Part.partOf.getOrders());
		assertFails(
				() -> newType(Part.class, failingActivator()),
				IllegalArgumentException.class,
				"order Container.order of PartOf Part.partOf " +
				"must be declared on the same type or super type");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	private static class Container extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField order = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

		@com.exedio.cope.instrument.Generated
		protected Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Part extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final ItemField<Container> container = ItemField.create(Container.class).toFinal().cascade();

		@UsageEntryPoint
		@Wrapper(wrap="*", visibility=NONE)
		static final PartOf<Container> partOf = PartOf.create(container, Container.order);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
