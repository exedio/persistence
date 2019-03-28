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

import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.BooleanField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class BlockActivationConstructorTest
{
	@Test void testParameterNull()
	{
		assertFails(
				() -> new MyBlock(null),
				RuntimeException.class,
				"activation constructor is for internal purposes only, " +
				"don't use it in your application!");
	}

	@WrapperType(indent=2, comments=false)
	static final class MyBlock extends Block
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: Block must not be empty
		static final BooleanField field = new BooleanField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}
}
