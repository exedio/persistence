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

import com.exedio.cope.StringField;

/**
 * Implements a hash migration strategy using nested hashes.
 * @see <a href="https://crypto.stackexchange.com/questions/2945/is-this-password-migration-strategy-secure">stackexchange</a>
 */
public final class NestedHashAlgorithm
{
	public static HashAlgorithm create(
			final HashAlgorithm legacy,
			final HashAlgorithm target)
	{
		if(target==null)
			throw new NullPointerException();
		if(legacy==null)
			throw new NullPointerException();

		final String plainText = "1234";
		if(!legacy.hash(plainText).equals(legacy.hash(plainText)))
			throw new IllegalArgumentException("legacy algorithm must be deterministic (i.e. unsalted), but was " + legacy.getDescription());

		return new Algorithm(legacy, target);
	}

	private record Algorithm(
			HashAlgorithm legacy,
			HashAlgorithm target)
			implements HashAlgorithm
	{
		@Override
		public String getID()
		{
			return legacy.getID() + '-' + target.getID();
		}

		@Override
		public String getDescription()
		{
			return legacy.getDescription() + '-' + target.getDescription();
		}

		@Override
		public StringField constrainStorage(final StringField storage)
		{
			return target.constrainStorage(storage);
		}

		@Override
		public String hash(final String plainText)
		{
			return target.hash(legacy.hash(plainText));
		}

		@Override
		public boolean check(final String plainText, final String hash)
		{
			return target.check(legacy.hash(plainText), hash);
		}
	}

	private NestedHashAlgorithm()
	{
		// prevent instantiation
	}
}
