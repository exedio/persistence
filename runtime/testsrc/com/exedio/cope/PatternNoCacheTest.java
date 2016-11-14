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

import static com.exedio.cope.PatternNoCacheTest.MyItem.TYPE;
import static com.exedio.cope.PatternNoCacheTest.MyItem.absent;
import static com.exedio.cope.PatternNoCacheTest.MyItem.noCache;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class PatternNoCacheTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PatternNoCacheTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEquals(false, noCache(MyItem.TYPE));

		assertEquals(false, noCache(absent));
		assertEquals(true,  noCache(noCache));

		assertEquals(false, noCache(absent.absentType));
		assertEquals(true,  noCache(absent.noCacheType));
		assertEquals(true,  noCache(noCache.absentType));
		assertEquals(true,  noCache(noCache.noCacheType));
	}

	private static boolean noCache(final Feature f)
	{
		final CopeNoCache annotation = f.getAnnotation(CopeNoCache.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeNoCache.class));
		return annotation!=null;
	}

	private static boolean noCache(final Type<?> f)
	{
		final CopeNoCache annotation = f.getAnnotation(CopeNoCache.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeNoCache.class));
		return annotation!=null;
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final MyPattern absent = new MyPattern();
		@CopeNoCache
		static final MyPattern noCache = new MyPattern();


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final class MyPattern extends Pattern
	{
		private static final long serialVersionUID = 1l;

		Type<?> absentType = null;
		Type<?> noCacheType = null;

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			this.absentType = newSourceType(AbsentType.class, features, "absent");
			this.noCacheType = newSourceType(NoCacheType.class, features, "set333");
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		static final class AbsentType extends Item
		{
			@javax.annotation.Generated("com.exedio.cope.instrument")
			private static final long serialVersionUID = 1l;

			@javax.annotation.Generated("com.exedio.cope.instrument")
			@SuppressWarnings("unused") private AbsentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@CopeNoCache
		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		static final class NoCacheType extends Item
		{
			@javax.annotation.Generated("com.exedio.cope.instrument")
			private static final long serialVersionUID = 1l;

			@javax.annotation.Generated("com.exedio.cope.instrument")
			@SuppressWarnings("unused") private NoCacheType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}
}
