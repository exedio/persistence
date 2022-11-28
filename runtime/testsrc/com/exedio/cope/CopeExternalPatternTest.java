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

import static com.exedio.cope.CopeExternalPatternTest.MyItem.TYPE;
import static com.exedio.cope.CopeExternalPatternTest.MyItem.absent;
import static com.exedio.cope.CopeExternalPatternTest.MyItem.present;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeExternalPatternTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CopeExternalPatternTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(false, external(TYPE));

		assertEquals(false, external(absent));
		assertEquals(true,  external(present));

		assertEquals(false, external(absent.absentType));
		assertEquals(true,  external(absent.presentType));
		assertEquals(true,  external(present.absentType));
		assertEquals(true,  external(present.presentType));
	}

	private static boolean external(final Feature f)
	{
		final CopeExternal annotation = f.getAnnotation(CopeExternal.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeExternal.class));
		return annotation!=null;
	}

	private static boolean external(final Type<?> t)
	{
		final CopeExternal annotation = t.getAnnotation(CopeExternal.class);
		assertEquals(annotation!=null, t.isAnnotationPresent(CopeExternal.class));
		return annotation!=null;
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final MyPattern absent = new MyPattern();
		@CopeExternal
		static final MyPattern present = new MyPattern();


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final class MyPattern extends Pattern
	{
		private static final long serialVersionUID = 1l;

		Type<?> absentType = null;
		Type<?> presentType = null;

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			this.absentType = newSourceType(AbsentType.class, AbsentType::new, features, "absent");
			this.presentType = newSourceType(PresentType.class, PresentType::new, features, "present");
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class AbsentType extends Item
		{
			@com.exedio.cope.instrument.Generated
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private AbsentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@CopeExternal
		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class PresentType extends Item
		{
			@com.exedio.cope.instrument.Generated
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private PresentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}
}
