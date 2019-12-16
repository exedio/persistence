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

import static com.exedio.cope.pattern.PartOf.orderBy;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.TYPE;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.container;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.order1;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.order2;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.parts1;
import static com.exedio.cope.pattern.PartOfContainerReuseTest.APart.parts2;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class PartOfContainerReuseTest
{
	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				container,
				order1, order2,
				parts1, parts2
			), TYPE.getFeatures());

		assertSame(container, parts1.getContainer());
		assertSame(container, parts2.getContainer());
		assertEqualsUnmodifiable(asList(orderBy(order1)), parts1.getOrders());
		assertEqualsUnmodifiable(asList(orderBy(order2)), parts2.getOrders());

		assertSame(parts1, container.getPattern()); // TODO should be parts2 as well
		assertSame(null, order1.getPattern());
		assertSame(null, order2.getPattern());
		assertSame(null, parts1.getPattern());
		assertSame(null, parts2.getPattern());

		assertEqualsUnmodifiable(list(container), parts1.getSourceFeatures());
		assertEqualsUnmodifiable(list(         ), parts2.getSourceFeatures()); // TODO should be container as well
	}


	static final class AContainer extends Item
	{


	/**
	 * Creates a new AContainer with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AContainer()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AContainer and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AContainer(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aContainer.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AContainer> TYPE = com.exedio.cope.TypesBound.newType(AContainer.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AContainer(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final class APart extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final ItemField<AContainer> container = ItemField.create(AContainer.class).cascade().toFinal();

		static final IntegerField order1 = new IntegerField();
		static final IntegerField order2 = new IntegerField();

		static final PartOf<AContainer> parts1 = PartOf.create(container, order1);
		@WrapperIgnore
		static final PartOf<AContainer> parts2 = PartOf.create(container, order2);


	/**
	 * Creates a new APart with all the fields initially needed.
	 * @param container the initial value for field {@link #container}.
	 * @param order1 the initial value for field {@link #order1}.
	 * @param order2 the initial value for field {@link #order2}.
	 * @throws com.exedio.cope.MandatoryViolationException if container is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	APart(
				@javax.annotation.Nonnull final AContainer container,
				final int order1,
				final int order2)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			APart.container.map(container),
			APart.order1.map(order1),
			APart.order2.map(order2),
		});
	}

	/**
	 * Creates a new APart and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private APart(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #container}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AContainer getContainer()
	{
		return APart.container.get(this);
	}

	/**
	 * Returns the value of {@link #order1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getOrder1()
	{
		return APart.order1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #order1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOrder1(final int order1)
	{
		APart.order1.set(this,order1);
	}

	/**
	 * Returns the value of {@link #order2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getOrder2()
	{
		return APart.order2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #order2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOrder2(final int order2)
	{
		APart.order2.set(this,order2);
	}

	/**
	 * Returns the container this item is part of by {@link #parts1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContainer")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AContainer getParts1Container()
	{
		return APart.parts1.getContainer(this);
	}

	/**
	 * Returns the parts of the given container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<APart> getParts1Parts(@javax.annotation.Nonnull final AContainer container)
	{
		return APart.parts1.getParts(APart.class,container);
	}

	/**
	 * Returns the parts of the given container matching the given condition.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<APart> getParts1Parts(@javax.annotation.Nonnull final AContainer container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return APart.parts1.getParts(APart.class,container,condition);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aPart.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<APart> TYPE = com.exedio.cope.TypesBound.newType(APart.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private APart(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}
