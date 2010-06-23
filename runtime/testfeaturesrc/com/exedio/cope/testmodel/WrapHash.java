/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.ByteAlgorithm;
import com.exedio.cope.pattern.Hash;

/**
 * A nonsense test hash for unit-testing the hashing mechanism.
 * @author Ralf Wiebicke
 */
public class WrapHash extends Hash
{
	private static final long serialVersionUID = 1l;
	
	public WrapHash(final StringField storage)
	{
		super(storage, new ByteAlgorithm(ALGORITHM));
	}

	public WrapHash()
	{
		super(new ByteAlgorithm(ALGORITHM));
	}
	
	private static final ByteAlgorithm.Algorithm ALGORITHM = new ByteAlgorithm.Algorithm()
	{
		public String name()
		{
			return "wrap";
		}

		public int length()
		{
			return 6;
		}
		
		public byte[] hash(final byte[] plainText)
		{
			if(plainText==null)
				throw new NullPointerException();
			
			final String x;
			try
			{
				x = new String(plainText, "utf8");
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			final int i = Integer.parseInt(x, 16);
			final byte[] result = new byte[]{
					0x34,
					(byte)( i >> 24),
					(byte)((i >> 16) & 0x000000ff),
					(byte)((i >>  8) & 0x000000ff),
					(byte)( i        & 0x000000ff),
					0x43,
			};
			assert result.length==length();
			return result;
		}
		
		public boolean check(final byte[] plainText, final byte[] hash)
		{
			if(plainText==null)
				throw new NullPointerException();
			if(hash==null)
				throw new NullPointerException();
			
			return Arrays.equals(hash(plainText), hash);
		}
	};
}
