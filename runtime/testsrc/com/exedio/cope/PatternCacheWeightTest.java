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

package com.exedio.cope;

import static com.exedio.cope.PatternCacheWeightTest.MyItem.TYPE;
import static com.exedio.cope.PatternCacheWeightTest.MyItem.absent;
import static com.exedio.cope.PatternCacheWeightTest.MyItem.set222;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class PatternCacheWeightTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PatternCacheWeightTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(ABSENT, weight(absent));
		assertEquals(222,    weight(set222));

		assertEquals(ABSENT, weight(absent.absentType));
		assertEquals(333,    weight(absent.set333Type));
		assertEquals(222,    weight(set222.absentType));
		assertWeightFails(set222.set333Type, "conflicting @CopeCacheWeight: 222 vs. 333");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	private static int weight(final Feature f)
	{
		final CopeCacheWeight annotation = f.getAnnotation(CopeCacheWeight.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeCacheWeight.class));
		return annotation!=null ? annotation.value() : ABSENT;
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	private static int weight(final Type<?> f)
	{
		final CopeCacheWeight annotation = f.getAnnotation(CopeCacheWeight.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeCacheWeight.class));
		return annotation!=null ? annotation.value() : ABSENT;
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	private static void assertWeightFails(final Type<?> f, final String message)
	{
		try
		{
			f.getAnnotation(CopeCacheWeight.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
		try
		{
			f.isAnnotationPresent(CopeCacheWeight.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	private static final int ABSENT = -123456789;


	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final MyPattern absent = new MyPattern();
		@SuppressWarnings("deprecation")
		@CopeCacheWeight(222)
		static final MyPattern set222 = new MyPattern();


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final class MyPattern extends Pattern
	{
		private static final long serialVersionUID = 1l;

		Type<?> absentType = null;
		Type<?> set333Type = null;

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			this.absentType = newSourceType(AbsentType.class, features, "absent");
			this.set333Type = newSourceType(Set333Type.class, features, "set333");
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class AbsentType extends Item
		{
			@com.exedio.cope.instrument.Generated
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private AbsentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@SuppressWarnings("deprecation")
		@CopeCacheWeight(333)
		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class Set333Type extends Item
		{
			@com.exedio.cope.instrument.Generated
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private Set333Type(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}
}
