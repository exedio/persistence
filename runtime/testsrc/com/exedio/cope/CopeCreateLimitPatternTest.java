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

import static com.exedio.cope.CopeCreateLimitPatternTest.MyItem.TYPE;
import static com.exedio.cope.CopeCreateLimitPatternTest.MyItem.absent;
import static com.exedio.cope.CopeCreateLimitPatternTest.MyItem.present;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeCreateLimitPatternTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CopeCreateLimitPatternTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(-1, limit(TYPE));

		assertEquals(-1, limit(absent));
		assertEquals(55, limit(present));

		assertEquals(-1, limit(absent.absentType));
		assertEquals(66, limit(absent.presentType));
		assertEquals(55, limit(present.absentType));
		assertEquals(55, limit(present.presentType)); // pattern overrides type

		assertEquals(MAX_VALUE, TYPE.getCreateLimit());
		assertEquals(MAX_VALUE, absent.absentType.getCreateLimit());
		assertEquals(66, absent.presentType.getCreateLimit());
		assertEquals(55, present.absentType.getCreateLimit());
		assertEquals(55, present.presentType.getCreateLimit()); // pattern overrides type
	}

	private static long limit(final Feature f)
	{
		final CopeCreateLimit annotation = f.getAnnotation(CopeCreateLimit.class);
		assertEquals(annotation!=null, f.isAnnotationPresent(CopeCreateLimit.class));
		return annotation!=null ? annotation.value() : -1;
	}

	private static long limit(final Type<?> t)
	{
		final CopeCreateLimit annotation = t.getAnnotation(CopeCreateLimit.class);
		assertEquals(annotation!=null, t.isAnnotationPresent(CopeCreateLimit.class));
		return annotation!=null ? annotation.value() : -1;
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final MyPattern absent = new MyPattern();
		@CopeCreateLimit(55)
		static final MyPattern present = new MyPattern();


		@com.exedio.cope.instrument.Generated
		@java.io.Serial
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
			@java.io.Serial
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private AbsentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@CopeCreateLimit(66)
		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class PresentType extends Item
		{
			@com.exedio.cope.instrument.Generated
			@java.io.Serial
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private PresentType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}
}
