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
import static com.exedio.cope.pattern.PartOf.orderByDesc;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PartOfOrderMultiTest extends TestWithEnvironment
{
	Container container;

	@BeforeEach final void setUp()
	{
		container = new Container();
	}

	@Test void testMulti()
	{
		assertSame(Part.container, Part.parts.getContainer());
		assertEqualsUnmodifiable(asList(
				orderBy(Part.order1), orderByDesc(Part.order2), orderBy(Part.order3)),
				Part.parts.getOrders());

		assertEquals(
				"select this from Part " +
				 "where container='" + container + "' " +
				 "order by order1, order2 desc, order3, this",
				Part.parts.getPartsQuery(container, null).toString());

		final Part part4 = container.addPart(0, -9, 9);
		final Part part2 = container.addPart(1,  1, 1);
		final Part part3 = container.addPart(1,  0, 0);
		final Part part1 = container.addPart(2, -9, 0);
		assertEquals(asList(part4, part2, part3, part1), container.getParts());
	}

	@Deprecated // OK: test deprecated api
	@Test void testDeprecated()
	{
		assertSame(Part.order1, Part.parts.getOrder());
	}

	@WrapperType(indent=2, comments=false)
	private static final class Part extends Item
	{
		@Wrapper(wrap="*", visibility=NONE) static final ItemField<Container> container = ItemField.create(Container.class).cascade().toFinal();

		@Wrapper(wrap="*", visibility=NONE) static final IntegerField order1 = new IntegerField();
		@Wrapper(wrap="*", visibility=NONE) static final IntegerField order2 = new IntegerField();
		@Wrapper(wrap="*", visibility=NONE) static final IntegerField order3 = new IntegerField();

		@Wrapper(wrap="getContainer", visibility=NONE)
		static final PartOf<Container> parts = PartOf.create(
				container, orderBy(order1), orderByDesc(order2), orderBy(order3));

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Part(
					@javax.annotation.Nonnull final Container container,
					final int order1,
					final int order2,
					final int order3)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Part.container,container),
				com.exedio.cope.SetValue.map(Part.order1,order1),
				com.exedio.cope.SetValue.map(Part.order2,order2),
				com.exedio.cope.SetValue.map(Part.order3,order3),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static java.util.List<Part> getPartsParts(@javax.annotation.Nonnull final Container container)
		{
			return Part.parts.getParts(Part.class,container);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static java.util.List<Part> getPartsParts(@javax.annotation.Nonnull final Container container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
		{
			return Part.parts.getParts(Part.class,container,condition);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class,Part::new);

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class Container extends Item
	{
		List<Part> getParts() // TODO generate this
		{
			return Part.getPartsParts(this);
		}

		Part addPart(
				final int order1, final int order2, final int order3) // TODO generate this
		{
			return new Part(this, order1, order2, order3);
		}


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Container()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	public PartOfOrderMultiTest()
	{
		super(PartOfOrderMultiTest.MODEL);
	}

	private static final Model MODEL = new Model(Part.TYPE, Container.TYPE);

	static
	{
		MODEL.enableSerialization(PartOfOrderMultiTest.class, "MODEL");
	}
}
