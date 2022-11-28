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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.misc.Arrays;
import java.security.SecureRandom;

public final class MockSecureRandom extends SecureRandom
{
	private static final long serialVersionUID = 1l;

	private byte[] nextBytesExpected = null;
	private boolean setSeedDone = false;

	@Override
	public void setSeed(final long seed)
	{
		if(setSeedDone)
			throw new RuntimeException("exhausted");
		else
			setSeedDone = true;

		super.setSeed(seed);
	}

	@Override
	public void nextBytes(final byte[] bytes)
	{
		assertTrue(nextBytesExpected!=null);
		assertEquals(nextBytesExpected.length, bytes.length);

		System.arraycopy(nextBytesExpected, 0, bytes, 0, bytes.length);
		nextBytesExpected = null;
	}

	void expectNextBytes(final byte[] bytes)
	{
		assertTrue(bytes!=null);
		assertTrue(nextBytesExpected==null);

		nextBytesExpected = Arrays.copyOf(bytes);
	}

	// all others do fail

	@Override
	public int nextInt()
	{
		throw new RuntimeException();
	}

	@Override
	public int nextInt(final int n)
	{
		throw new RuntimeException();
	}

	@Override
	public long nextLong()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean nextBoolean()
	{
		throw new RuntimeException();
	}

	@Override
	public float nextFloat()
	{
		throw new RuntimeException();
	}

	@Override
	public double nextDouble()
	{
		throw new RuntimeException();
	}

	@Override
	public synchronized double nextGaussian()
	{
		throw new RuntimeException();
	}

	@Override
	public String getAlgorithm()
	{
		throw new RuntimeException();
	}

	@Override
	public void setSeed(final byte[] seed)
	{
		throw new RuntimeException();
	}

	@Override
	public byte[] generateSeed(final int numBytes)
	{
		throw new RuntimeException();
	}
}
