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
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import java.io.Serial;
import org.junit.jupiter.api.Test;

public class PatternErrorAbstractTest
{
	@Test void testAbstract()
	{
		assertFails(
				() -> TypesBound.newType(ContainerAbstract.class, ContainerAbstract::new),
				IllegalArgumentException.class,
				TypeAbstract.class + " must not be abstract");
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ContainerAbstract extends Item
	{
		@UsageEntryPoint // OK: used by reflection
		static final MyPattern pattern = new MyPattern(TypeAbstract.class, false);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private ContainerAbstract(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor = NONE, genericConstructor = NONE, indent = 2, comments = false)
	@SuppressWarnings("AbstractClassNeverImplemented") // OK
	private abstract static class TypeAbstract extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<TypeAbstract> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(TypeAbstract.class);

		@com.exedio.cope.instrument.Generated
		protected TypeAbstract(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testConcrete()
	{
		assertFails(
				() -> TypesBound.newType(ContainerConcrete.class, ContainerConcrete::new),
				IllegalArgumentException.class,
				TypeConcrete.class + " must be abstract");
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ContainerConcrete extends Item
	{
		@UsageEntryPoint // OK: used by reflection
		static final MyPattern pattern = new MyPattern(TypeConcrete.class, true);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private ContainerConcrete(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor = NONE, genericConstructor = NONE, indent = 2, comments = false)
	private static final class TypeConcrete extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<TypeConcrete> TYPE = com.exedio.cope.TypesBound.newType(TypeConcrete.class,TypeConcrete::new);

		@com.exedio.cope.instrument.Generated
		private TypeConcrete(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	private static final class MyPattern extends Pattern
	{
		final Class<? extends Item> javaClass;
		final boolean isAbstract;

		MyPattern(
				final Class<? extends Item> javaClass,
				final boolean isAbstract)
		{
			this.javaClass = javaClass;
			this.isAbstract = isAbstract;
		}

		@Override
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			newSourceType(javaClass, isAbstract, null, features, "postfix");
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}
}
