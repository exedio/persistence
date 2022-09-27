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
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyBlock.virgnTemp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyBlock.wrongTemp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.TYPE;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.virgnComp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.wrongComp;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopeName;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class BlockRenamedIdTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(BlockRenamedIdTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(null,        ann(virgnTemp));
		assertEquals("namedTemp", ann(wrongTemp));
		assertEquals(null,        ann(virgnComp));
		assertEquals("namedComp", ann(wrongComp));

		assertEquals(null,                  ann(virgnComp.of(virgnTemp)));
		assertEquals(          "namedTemp", ann(virgnComp.of(wrongTemp))); // TODO virgnComp-namedTemp
		assertEquals(null                 , ann(wrongComp.of(virgnTemp))); // TODO namedComp-virgnTemp
		assertEquals(          "namedTemp", ann(wrongComp.of(wrongTemp))); // TODO namedComp-namedTemp

		assertEquals("virgnComp-virgnTemp", virgnComp.of(virgnTemp).getName());
		assertEquals("virgnComp-namedTemp", virgnComp.of(wrongTemp).getName());
		assertEquals("namedComp-virgnTemp", wrongComp.of(virgnTemp).getName());
		assertEquals("namedComp-namedTemp", wrongComp.of(wrongTemp).getName());
	}

	private static String ann(final Feature f)
	{
		final CopeName a = f.getAnnotation(CopeName.class);
		return a!=null ? a.value() : null;
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyBlock extends Block
	{
		@WrapperIgnore static final StringField virgnTemp = new StringField();
		@CopeName("namedTemp")
		@WrapperIgnore static final StringField wrongTemp = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class,MyBlock::new);

		@com.exedio.cope.instrument.Generated
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		@WrapperIgnore static final BlockField<MyBlock> virgnComp = BlockField.create(MyBlock.TYPE);
		@CopeName("namedComp")
		@WrapperIgnore static final BlockField<MyBlock> wrongComp = BlockField.create(MyBlock.TYPE);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
