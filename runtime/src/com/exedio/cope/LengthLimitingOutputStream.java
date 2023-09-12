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

package com.exedio.cope;

import static com.exedio.cope.util.Check.requireNonNegative;

import java.io.IOException;
import java.io.OutputStream;

abstract class LengthLimitingOutputStream extends OutputStream
{
	private final long maximumLength;
	private final OutputStream back;

	private boolean exhausted = false;
	private long transferred = 0;

	LengthLimitingOutputStream(
			final long maximumLength,
			final OutputStream back)
	{
		this.maximumLength = requireNonNegative(maximumLength, "maximumLength");
		this.back = back;

		if(back instanceof LengthLimitingOutputStream)
			throw new IllegalArgumentException();
	}

	@Override
	public final void write(final int b) throws IOException
	{
		checkTransferred(1);
		back.write(b);
	}

	@Override
	public final void write(final byte[] b) throws IOException
	{
		checkTransferred(b.length);
		back.write(b);
	}

	@Override
	public final void write(final byte[] b, final int off, final int len) throws IOException
	{
		checkTransferred(len);
		back.write(b, off, len);
	}

	private void checkTransferred(final int len)
	{
		if(exhausted)
			throw new IllegalStateException("exhausted");

		transferred += len;
		if(transferred>maximumLength)
		{
			exhausted = true;
			throw newFailure(transferred);
		}
	}

	protected abstract RuntimeException newFailure(long transferred);


	@Override
	public void flush() throws IOException
	{
		back.flush();
	}

	@Override
	public void close() throws IOException
	{
		back.close();
	}
}
