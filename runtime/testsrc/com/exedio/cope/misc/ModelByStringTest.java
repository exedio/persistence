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

import static com.exedio.cope.misc.ModelByString.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import org.junit.Test;

public class ModelByStringTest
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	@SuppressWarnings("unused") // OK: read by reflection
	private static final Model modelPrivate = modelOk;
	public static final Model modelNull = null;
	public static final String modelNoModel = "hallo";

	@Test public void testIt()
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

	static class ModelOk extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelOk> TYPE = TypesBound.newType(ModelOk.class);
		private ModelOk(final ActivationParameters ap) { super(ap); }
	}

	static class ModelOk2 extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelOk2> TYPE = TypesBound.newType(ModelOk2.class);
		private ModelOk2(final ActivationParameters ap) { super(ap); }
	}

	static class ModelContext extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelContext> TYPE = TypesBound.newType(ModelContext.class);
		private ModelContext(final ActivationParameters ap) { super(ap); }
	}
}