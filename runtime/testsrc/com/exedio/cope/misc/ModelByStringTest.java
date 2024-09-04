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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.ModelByString.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ModelByStringTest
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	@SuppressWarnings("unused") // OK: read by reflection
	private static final Model modelPrivate = modelOk;
	@SuppressWarnings("unused") // OK: read by reflection
	public static final Model modelNull = null;
	@SuppressWarnings("unused") // OK: read by reflection
	public static final String modelNoModel = "hallo";

	@Test void testIt()
	{
		assertSame(modelOk, get("com.exedio.cope.misc.ModelByStringTest#modelOk"));

		try
		{
			get("zick");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("does not contain '#', but was zick", e.getMessage());
		}

		try
		{
			get("com.exedio.cope.misc.ModelByStringTestX#modelNotExists");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class com.exedio.cope.misc.ModelByStringTestX does not exist.", e.getMessage());
		}

		try
		{
			get("com.exedio.cope.misc.ModelByStringTest#modelNotExists");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field modelNotExists in class com.exedio.cope.misc.ModelByStringTest does not exist or is not public.", e.getMessage());
		}

		try
		{
			get("com.exedio.cope.misc.ModelByStringTest#modelPrivate");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field modelPrivate in class com.exedio.cope.misc.ModelByStringTest does not exist or is not public.", e.getMessage());
		}

		try
		{
			get("com.exedio.cope.misc.ModelByStringTest#modelNull");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field com.exedio.cope.misc.ModelByStringTest#modelNull is null.", e.getMessage());
		}

		try
		{
			get("com.exedio.cope.misc.ModelByStringTest#modelNoModel");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field com.exedio.cope.misc.ModelByStringTest#modelNoModel is not a model, but a java.lang.String.", e.getMessage());
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelOk extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelOk> TYPE = com.exedio.cope.TypesBound.newType(ModelOk.class,ModelOk::new);

		@com.exedio.cope.instrument.Generated
		private ModelOk(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelOk2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelOk2> TYPE = com.exedio.cope.TypesBound.newType(ModelOk2.class,ModelOk2::new);

		@com.exedio.cope.instrument.Generated
		private ModelOk2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelContext extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelContext> TYPE = com.exedio.cope.TypesBound.newType(ModelContext.class,ModelContext::new);

		@com.exedio.cope.instrument.Generated
		private ModelContext(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
