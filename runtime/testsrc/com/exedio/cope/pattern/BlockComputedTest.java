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
import static com.exedio.cope.pattern.BlockComputedTest.MyBlock.compuTemp;
import static com.exedio.cope.pattern.BlockComputedTest.MyBlock.virgnTemp;
import static com.exedio.cope.pattern.BlockComputedTest.MyItem.TYPE;
import static com.exedio.cope.pattern.BlockComputedTest.MyItem.compuComp;
import static com.exedio.cope.pattern.BlockComputedTest.MyItem.virgnComp;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.misc.Computed;
import org.junit.jupiter.api.Test;

public class BlockComputedTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(BlockComputedTest.class, "MODEL");
	}

	@Test void testIt()
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

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyBlock extends Block
	{
		@WrapperIgnore static final StringField virgnTemp = new StringField();
		@Computed
		@WrapperIgnore static final StringField compuTemp = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class);

		@com.exedio.cope.instrument.Generated
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		@WrapperIgnore static final BlockField<MyBlock> virgnComp = BlockField.create(MyBlock.TYPE);
		@Computed
		@WrapperIgnore static final BlockField<MyBlock> compuComp = BlockField.create(MyBlock.TYPE);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
