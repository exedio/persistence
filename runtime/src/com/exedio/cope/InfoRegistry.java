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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

final class InfoRegistry
{
	private static final String[] ACCEPTED = {
			Transaction.class.getName() + '.',
			QueryCache.class.getName() + '.',
			ItemCache.class.getName() + '.',
			ChangeListener.class.getName() + '.',
			DataField.class.getName() + '.'};

	private static boolean isAccepted(final String name)
	{
		for(final String s : ACCEPTED)
			if(name.startsWith(s))
				return true;
		return false;
	}


	static final SimpleMeterRegistry REGISTRY = new SimpleMeterRegistry();

	static
	{
		REGISTRY.config().meterFilter(new MeterFilter()
		{
			@Override
			public MeterFilterReply accept(final Meter.Id id)
			{
				return isAccepted(id.getName())
						? MeterFilterReply.ACCEPT
						: MeterFilterReply.DENY;
			}
		});
		Metrics.globalRegistry.add(REGISTRY);
	}

	static Counter.Builder counter(final String name)
	{
		assert isAccepted(name) : name;
		return Counter.builder(name);
	}

	static Timer.Builder timer(final String name)
	{
		assert isAccepted(name) : name;
		return Timer.builder(name);
	}

	@SuppressFBWarnings("FE_FLOATING_POINT_EQUALITY") // OK: tests backward conversion
	static long count(final Counter counter)
	{
		final double d = counter.count();
		final long l = Math.round(d);
		//noinspection FloatingPointEquality OK: tests backward conversion
		if(l!=d)
			throw new IllegalStateException(counter.getId().toString() + '/' + d);
		return l;
	}

	static int countInt(final Counter counter)
	{
		return Math.toIntExact(count(counter));
	}

	static int countInt(final Timer timer)
	{
		return Math.toIntExact(timer.count());
	}


	private InfoRegistry()
	{
		// prevent instantiation
	}
}
