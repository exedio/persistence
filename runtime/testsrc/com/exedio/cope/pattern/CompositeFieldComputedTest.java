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

import static com.exedio.cope.pattern.CompositeFieldComputedTest.MyComposite.compuTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.MyComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.MyItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.MyItem.compuComp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.MyItem.virgnComp;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.misc.Computed;
import org.junit.jupiter.api.Test;

public class CompositeFieldComputedTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldComputedTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEquals(false, comp(virgnTemp));
		assertEquals(true,  comp(compuTemp));
		assertEquals(false, comp(virgnComp));
		assertEquals(true,  comp(compuComp));

		assertEquals(false, comp(virgnComp.of(virgnTemp)));
		assertEquals(true,  comp(virgnComp.of(compuTemp)));
		assertEquals(true,  comp(compuComp.of(virgnTemp)));
		assertEquals(true,  comp(compuComp.of(compuTemp)));
	}

	private static boolean comp(final Feature f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}

	@com.exedio.cope.instrument.WrapperIgnore // TODO use import, but this is not accepted by javac
	static final class MyComposite extends Composite
	{
		static final StringField virgnTemp = new StringField();
		@Computed
		static final StringField compuTemp = new StringField();

		private MyComposite(final SetValue<?>... setValues) { super(setValues); }
		private static final long serialVersionUID = 1l;
	}

	@com.exedio.cope.instrument.WrapperIgnore // TODO use import, but this is not accepted by javac
	static final class MyItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final CompositeField<MyComposite> virgnComp = CompositeField.create(MyComposite.class);
		@Computed
		static final CompositeField<MyComposite> compuComp = CompositeField.create(MyComposite.class);

		private static final long serialVersionUID = 1l;
		static final Type<MyItem> TYPE = TypesBound.newType(MyItem.class);
		private MyItem(final ActivationParameters ap) { super(ap); }
	}
}
