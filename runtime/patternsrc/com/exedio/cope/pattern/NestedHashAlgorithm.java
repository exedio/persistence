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

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.HashAlgorithm;

/**
 * Implements a hash migration strategy using nested hashes.
 * See also {@linktourl http://crypto.stackexchange.com/questions/2945/is-this-password-migration-strategy-secure}.
 */
public final class NestedHashAlgorithm
{
	public static final HashAlgorithm create(
			final HashAlgorithm inner,
			final HashAlgorithm outer)
	{
		if(outer==null)
			throw new NullPointerException();
		if(inner==null)
			throw new NullPointerException();

		final String plainText = "1234";
		if(!inner.hash(plainText).equals(inner.hash(plainText)))
			throw new IllegalArgumentException("inner algorithm must be deterministic (i.e. unsalted), but was " + inner.getDescription());

		return new Algorithm(inner, outer);
	}

	private static final class Algorithm implements HashAlgorithm
	{
		final HashAlgorithm inner;
		final HashAlgorithm outer;

		Algorithm(final HashAlgorithm inner, final HashAlgorithm outer)
		{
			this.inner = inner;
			this.outer = outer;
		}

		@Override
		public String getID()
		{
			return inner.getID() + '-' + outer.getID();
		}

		@Override
		public String getDescription()
		{
			return inner.getDescription() + '-' + outer.getDescription();
		}

		@Override
		public StringField constrainStorage(final StringField storage)
		{
			return outer.constrainStorage(storage);
		}

		@Override
		public String hash(final String plainText)
		{
			return outer.hash(inner.hash(plainText));
		}

		@Override
		public boolean check(final String plainText, final String hash)
		{
			return outer.check(inner.hash(plainText), hash);
		}
	}

	private NestedHashAlgorithm()
	{
		// prevent instantiation
	}
}
