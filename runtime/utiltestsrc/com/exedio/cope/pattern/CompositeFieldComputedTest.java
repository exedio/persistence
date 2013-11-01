/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.CompositeFieldComputedTest.ComputedComposite.compuTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.ComputedComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.ComputedItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.ComputedItem.compuComp;
import static com.exedio.cope.pattern.CompositeFieldComputedTest.ComputedItem.virgnComp;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public final class CompositeFieldComputedTest extends CopeAssert
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldComputedTest.class, "MODEL");
	}

	public static void testIt()
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

	static final class ComputedComposite extends Composite
	{
		static final StringField virgnTemp = new StringField();
		@Computed()
		static final StringField compuTemp = new StringField();

		private ComputedComposite(final SetValue<?>... setValues) { super(setValues); }
		private static final long serialVersionUID = 1l;
	}

	static final class ComputedItem extends Item
	{
		static final CompositeField<ComputedComposite> virgnComp = CompositeField.create(ComputedComposite.class);
		@Computed()
		static final CompositeField<ComputedComposite> compuComp = CompositeField.create(ComputedComposite.class);


		private static final long serialVersionUID = 1l;
		static final Type<ComputedItem> TYPE = TypesBound.newType(ComputedItem.class);
		private ComputedItem(final ActivationParameters ap) { super(ap); }
	}
}
