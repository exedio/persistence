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

import static com.exedio.cope.PatternComputedTest.MyItem.TYPE;
import static com.exedio.cope.PatternComputedTest.MyItem.compuComp;
import static com.exedio.cope.PatternComputedTest.MyItem.virgnComp;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;
import org.junit.Test;

public class PatternComputedTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PatternComputedTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEquals(false, comp(virgnComp));
		assertEquals(true,  comp(compuComp));

		assertEquals(false, comp(virgnComp.virgnSource));
		assertEquals(true,  comp(virgnComp.compuSource));
		assertEquals(true,  comp(compuComp.virgnSource));
		assertEquals(true,  comp(compuComp.compuSource));

		assertEquals(false, comp(virgnComp.virgnType));
		assertEquals(true,  comp(virgnComp.compuType));
		assertEquals(true,  comp(compuComp.virgnType));
		assertEquals(true,  comp(compuComp.compuType));

		assertEquals(false, comp(virgnComp.virgnTypeVirgnField));
		assertEquals(true,  comp(virgnComp.virgnTypeCompuField));
		assertEquals(false, comp(virgnComp.compuTypeVirgnField));
		assertEquals(true,  comp(virgnComp.compuTypeCompuField));
		assertEquals(true,  comp(compuComp.virgnTypeVirgnField));
		assertEquals(true,  comp(compuComp.virgnTypeCompuField));
		assertEquals(true,  comp(compuComp.compuTypeVirgnField));
		assertEquals(true,  comp(compuComp.compuTypeCompuField));
	}

	private static boolean comp(final Feature f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}

	private static boolean comp(final Type<?> f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}


	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final MyPattern virgnComp = new MyPattern();
		@Computed
		static final MyPattern compuComp = new MyPattern();


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

		final StringField virgnSource = new StringField();
		final StringField compuSource = new StringField();

		MyPattern()
		{
			addSource(virgnSource, "virgnSource");
			addSource(compuSource, "compuSource", ComputedElement.get());
		}

		Type<?> virgnType = null;
		Type<?> compuType = null;
		final StringField virgnTypeVirgnField = new StringField();
		final StringField virgnTypeCompuField = new StringField();
		final StringField compuTypeVirgnField = new StringField();
		final StringField compuTypeCompuField = new StringField();

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			features.put("virgnField", virgnTypeVirgnField);
			features.put("compuField", virgnTypeCompuField, ComputedElement.get());
			this.virgnType = newSourceType(VirgnType.class, features, "virgn");

			features.clear();
			features.put("virgnField", compuTypeVirgnField);
			features.put("compuField", compuTypeCompuField, ComputedElement.get());
			this.compuType = newSourceType(CompuType.class, features, "compu");
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		static final class VirgnType extends Item
		{
			@javax.annotation.Generated("com.exedio.cope.instrument")
			private static final long serialVersionUID = 1l;

			@javax.annotation.Generated("com.exedio.cope.instrument")
			@SuppressWarnings("unused") private VirgnType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@Computed
		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		static final class CompuType extends Item
		{
			@javax.annotation.Generated("com.exedio.cope.instrument")
			private static final long serialVersionUID = 1l;

			@javax.annotation.Generated("com.exedio.cope.instrument")
			@SuppressWarnings("unused") private CompuType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}
}
