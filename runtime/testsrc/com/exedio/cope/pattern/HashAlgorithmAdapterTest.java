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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class HashAlgorithmAdapterTest
{
	@Test public void testAlgorithm()
	{
		@SuppressWarnings("deprecation")
		final Hash.Algorithm algorithm = AnItem.hash.getAlgorithm();
		assertEquals("algorithmID", algorithm.name());
		assertEquals(66, algorithm.length());
		try
		{
			algorithm.hash(new byte[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not implementable", e.getMessage());
		}
		try
		{
			algorithm.check(new byte[]{}, new byte[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not implementable", e.getMessage());
		}
		try
		{
			algorithm.compatibleTo(algorithm);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not implementable", e.getMessage());
		}
	}

	@Test public void testEncoding()
	{
		@SuppressWarnings("deprecation")
		final String encoding = AnItem.hash.getEncoding();
		assertEquals("UNKNOWN", encoding);
	}

	static final class AnAlgorithm implements HashAlgorithm
	{
		@Override
		public String getID()
		{
			return "algorithmID";
		}

		@Override
		public String getDescription()
		{
			return "algorithmDescription";
		}

		@Override
		public StringField constrainStorage(final StringField storage)
		{
			return storage.lengthRange(66, 77);
		}

		@Override
		public String hash(final String plainText)
		{
			throw new RuntimeException();
		}

		@Override
		public boolean check(final String plainText, final String hash)
		{
			throw new RuntimeException();
		}
	}

	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final Hash hash = new Hash(new AnAlgorithm());

		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
	}

	@SuppressWarnings("unused")
	private static final Model model = new Model(AnItem.TYPE);
}
