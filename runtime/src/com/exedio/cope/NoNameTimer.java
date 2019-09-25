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

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class NoNameTimer implements Timer
{
	private long count = 0;

	@Override
	public void record(final long amount, final TimeUnit unit)
	{
		count++;
	}

	@Override
	public long count()
	{
		return count;
	}

	@Override
	public <T> T record(final Supplier<T> f)
	{
		throw new RuntimeException();
	}
	@Override
	public <T> T recordCallable(final Callable<T> f)
	{
		throw new RuntimeException();
	}
	@Override
	public void record(final Runnable f)
	{
		throw new RuntimeException();
	}
	@Override
	public double totalTime(final TimeUnit unit)
	{
		throw new RuntimeException();
	}
	@Override
	public double max(final TimeUnit unit)
	{
		throw new RuntimeException();
	}
	@Override
	public TimeUnit baseTimeUnit()
	{
		throw new RuntimeException();
	}
	@Override
	public HistogramSnapshot takeSnapshot()
	{
		throw new RuntimeException();
	}
	@Override
	public Id getId()
	{
		throw new RuntimeException();
	}
}
