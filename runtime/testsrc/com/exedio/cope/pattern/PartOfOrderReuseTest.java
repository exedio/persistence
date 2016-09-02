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

import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.TYPE;
import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.container1;
import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.container2;
import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.order;
import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.parts1;
import static com.exedio.cope.pattern.PartOfOrderReuseTest.APart.parts2;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import org.junit.Test;

public class PartOfOrderReuseTest
{
	@Test public void testIt()
	{
		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				container1, container2,
				order,
				parts1, parts2
			), TYPE.getFeatures());

		assertSame(container1, parts1.getContainer());
		assertSame(container2, parts2.getContainer());
		assertSame(order, parts1.getOrder());
		assertSame(order, parts2.getOrder());

		assertSame(parts1, container1.getPattern());
		assertSame(parts2, container2.getPattern());
		assertSame(parts1, order.getPattern()); // TODO should be parts2 as well
		assertSame(null, parts1.getPattern());
		assertSame(null, parts2.getPattern());

		assertEqualsUnmodifiable(list(container1, order), parts1.getSourceFeatures());
		assertEqualsUnmodifiable(list(container2       ), parts2.getSourceFeatures()); // TODO should be order as well
	}


	static final class AContainer extends Item
	{
		/**

	 **
	 * Creates a new AContainer with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AContainer()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}/**

	 **
	 * Creates a new AContainer and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AContainer(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for aContainer.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AContainer> TYPE = com.exedio.cope.TypesBound.newType(AContainer.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AContainer(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class APart extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final ItemField<AContainer> container1 = ItemField.create(AContainer.class).cascade().toFinal();
		static final ItemField<AContainer> container2 = ItemField.create(AContainer.class).cascade().toFinal();

		static final IntegerField order = new IntegerField();

		static final PartOf<AContainer> parts1 = PartOf.create(container1, order);
		static final PartOf<AContainer> parts2 = PartOf.create(container2, order);


		/**

	 **
	 * Creates a new APart with all the fields initially needed.
	 * @param container1 the initial value for field {@link #container1}.
	 * @param container2 the initial value for field {@link #container2}.
	 * @param order the initial value for field {@link #order}.
	 * @throws com.exedio.cope.MandatoryViolationException if container1, container2 is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	APart(
				@javax.annotation.Nonnull final AContainer container1,
				@javax.annotation.Nonnull final AContainer container2,
				final int order)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			APart.container1.map(container1),
			APart.container2.map(container2),
			APart.order.map(order),
		});
	}/**

	 **
	 * Creates a new APart and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private APart(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #container1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final AContainer getContainer1()
	{
		return APart.container1.get(this);
	}/**

	 **
	 * Returns the value of {@link #container2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final AContainer getContainer2()
	{
		return APart.container2.get(this);
	}/**

	 **
	 * Returns the value of {@link #order}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getOrder()
	{
		return APart.order.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #order}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOrder(final int order)
	{
		APart.order.set(this,order);
	}/**

	 **
	 * Returns the container this item is part of by {@link #parts1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContainer")
	@javax.annotation.Nonnull
	final AContainer getParts1Container()
	{
		return APart.parts1.getContainer(this);
	}/**

	 **
	 * Returns the parts of the given container.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParts")
	@javax.annotation.Nonnull
	static final java.util.List<APart> getParts1Parts(@javax.annotation.Nonnull final AContainer container)
	{
		return APart.parts1.getParts(APart.class,container);
	}/**

	 **
	 * Returns the parts of the given container matching the given condition.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParts")
	@javax.annotation.Nonnull
	static final java.util.List<APart> getParts1Parts(@javax.annotation.Nonnull final AContainer container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return APart.parts1.getParts(APart.class,container,condition);
	}/**

	 **
	 * Returns the container this item is part of by {@link #parts2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContainer")
	@javax.annotation.Nonnull
	final AContainer getParts2Container()
	{
		return APart.parts2.getContainer(this);
	}/**

	 **
	 * Returns the parts of the given container.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParts")
	@javax.annotation.Nonnull
	static final java.util.List<APart> getParts2Parts(@javax.annotation.Nonnull final AContainer container)
	{
		return APart.parts2.getParts(APart.class,container);
	}/**

	 **
	 * Returns the parts of the given container matching the given condition.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParts")
	@javax.annotation.Nonnull
	static final java.util.List<APart> getParts2Parts(@javax.annotation.Nonnull final AContainer container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return APart.parts2.getParts(APart.class,container,condition);
	}/**

	 **
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for aPart.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<APart> TYPE = com.exedio.cope.TypesBound.newType(APart.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private APart(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

}
