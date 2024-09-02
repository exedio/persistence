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
import static com.exedio.cope.pattern.MediaFilter.forSource;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class MediaFilterForSourceTest
{
	@Test void test()
	{
		assertEqualsUnmodifiable(asList(
				AnItem.alpha1, AnItem.alpha2),
				forSource(AnItem.alpha));
	}
	@Test void testSubClass()
	{
		assertEqualsUnmodifiable(asList(
				AnItem.beta1, ASubItem.beta2),
				forSource(AnItem.beta));
	}
	@Test void testNothing()
	{
		assertEqualsUnmodifiable(asList(),
				forSource(ASubItem.nothing));
	}
	@Test void testNullSource()
	{
		assertFails(
				() -> forSource(null),
				NullPointerException.class, "Cannot invoke \"com.exedio.cope.pattern.Media.getType()\" because \"source\" is null");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnItem extends Item
	{
		@WrapperIgnore static final Media alpha = new Media();
		@WrapperIgnore static final Media beta  = new Media();

		@WrapperIgnore static final AFilter alpha1 = new AFilter(alpha);
		@WrapperIgnore static final AFilter alpha2 = new AFilter(alpha);
		@WrapperIgnore static final AFilter beta1  = new AFilter(beta);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class ASubItem extends AnItem
	{
		@WrapperIgnore static final AFilter beta2 = new AFilter(beta);

		@WrapperIgnore static final Media nothing = new Media();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ASubItem> TYPE = com.exedio.cope.TypesBound.newType(ASubItem.class,ASubItem::new);

		@com.exedio.cope.instrument.Generated
		protected ASubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final class AFilter extends MediaFilter
	{
		AFilter(final Media source)
		{
			super(source);
		}
		@Override
		public Set<String> getSupportedSourceContentTypes()
		{
			throw new AssertionFailedError();
		}
		@Override
		public String getContentType(@Nonnull final Item item)
		{
			throw new AssertionFailedError();
		}
		@Override
		public void doGetAndCommit(
				final HttpServletRequest request,
				final HttpServletResponse response,
				final Item item)
		{
			throw new AssertionFailedError();
		}
		@Serial
		private static final long serialVersionUID = 1l;
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	private static final Model MODEL = new Model(AnItem.TYPE, ASubItem.TYPE);
}
