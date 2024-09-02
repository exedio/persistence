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

package com.exedio.cope.instrument.testlib;

import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockActivationParameters;
import com.exedio.cope.pattern.BlockType;
import java.io.Serial;

@WrapperIgnore // needed in contrast to LibComposite because TYPE is evaluated by instrumentor
@SuppressWarnings("unused")
public class LibBlock extends Block
{
	protected static final StringField superField = new StringField();

	@Serial
	private static final long serialVersionUID = 1l;

	public static final BlockType<LibBlock> TYPE = BlockType.newType(LibBlock.class, LibBlock::new);

	protected LibBlock(final BlockActivationParameters ap){super(ap);}
}
