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

import static com.exedio.cope.pattern.BlockRenamedIdTest.MyBlock.virgnTemp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyBlock.wrongTemp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.TYPE;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.virgnComp;
import static com.exedio.cope.pattern.BlockRenamedIdTest.MyItem.wrongComp;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeName;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import org.junit.Test;

public class BlockRenamedIdTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(BlockRenamedIdTest.class, "MODEL");
	}

	@Test public void testIt()
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

	static final class MyBlock extends Block
	{
		static final StringField virgnTemp = new StringField();
		@CopeName("namedTemp")
		static final StringField wrongTemp = new StringField();

		private static final long serialVersionUID = 1l;
		static final BlockType<MyBlock> TYPE = BlockType.newType(MyBlock.class);
		private MyBlock(final BlockActivationParameters ap) { super(ap); }
	}

	static final class MyItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final BlockField<MyBlock> virgnComp = BlockField.create(MyBlock.TYPE);
		@CopeName("namedComp")
		static final BlockField<MyBlock> wrongComp = BlockField.create(MyBlock.TYPE);

		private MyItem(final SetValue<?>... setValues) { super(setValues); }
		private static final long serialVersionUID = 1l;
		static final Type<MyItem> TYPE = TypesBound.newType(MyItem.class);
		private MyItem(final ActivationParameters ap){super(ap);}
	}
}