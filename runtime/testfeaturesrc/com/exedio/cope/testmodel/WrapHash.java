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

package com.exedio.cope.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.HashAlgorithm;
import java.security.SecureRandom;

/**
 * A nonsense test hash for unit-testing the hashing mechanism.
 * @author Ralf Wiebicke
 */
public final class WrapHash
{
	public static final HashAlgorithm ALGORITHM = new HashAlgorithm()
	{
		@Override
		public String getID()
		{
			return "wrap";
		}

		@Override
		public String getDescription()
		{
			return "wrapDescription";
		}

		@Override
		public StringField constrainStorage(final StringField storage)
		{
			return storage;
		}

		@Override
		public String hash(final String plainText)
		{
			if(plainText==null)
				throw new NullPointerException();

			if("RETURN_NULL".equals(plainText))
				return null;

			return "[" + plainText + "]";
		}

		@Override
		public boolean check(final String plainText, final String hash)
		{
			if(plainText==null)
				throw new NullPointerException();
			if(hash==null)
				throw new NullPointerException();

			return hash.equals(hash(plainText));
		}
	};

	/** this validator throws an exception whatever member method is called */
	public static final class CorruptValidator extends Hash.PlainTextValidator
	{
		@Override protected void validate(final String plainText, final Item exceptionItem, final Hash hash)
			throws Hash.InvalidPlainTextException
		{
			throw new IllegalStateException("validate");
		}
		@Override protected String newRandomPlainText(final SecureRandom secureRandom)
		{
			throw new IllegalStateException("newRandomPlainText");
		}
	}


	private WrapHash()
	{
		// prevent instantiation
	}
}
