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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Sampler.beforeNow;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

public final class SamplerProperties extends Properties
{
	// cope

	private final ConnectProperties cope = value("cope", ConnectProperties.factory().
			revisionTable("SamplerRevision", "SamplerRevisionUnique"));

	public ConnectProperties getCope()
	{
		return cope;
	}

	public void setProperties(final Model model)
	{
		ConnectToken.setProperties(model, cope);
	}


	// sample

	private final Duration transactionDuration = value("transactionDuration", Duration.ofSeconds(10), Duration.ZERO);

	@SuppressWarnings("unused") // TODO test
	public void sample(final Sampler sampler)
	{
		sampler.sampleInternal(transactionDuration);
	}

	/**
	 * @deprecated
	 * Sampler no longer supports recording {@code buildTag}.
	 * Use {@link #sample(Sampler)} instead.
	 * Consider recording {@code buildTag} somewhere else,
	 * for instance by prometheus.
	 */
	@Deprecated
	public void sample(final Sampler sampler, @SuppressWarnings("unused") final String buildTag)
	{
		sample(sampler);
	}


	// purge

	final PurgeDays purge = value("purge", true, PurgeDays::new);

	static final class PurgeDays extends Properties
	{
		final Duration model       = value("model",       ampleWeeks(8), Duration.ofDays(1));
		final Duration transaction = subVl("transaction", ampleWeeks(8), model);
		final Duration itemCache   = subVl("itemCache",   ampleWeeks(1), model);
		final Duration clusterNode = subVl("clusterNode", ampleWeeks(4), model);
		final Duration media       = subVl("media",       ampleWeeks(4), model);

		private static Duration ampleWeeks(final long weeks)
		{
			return Duration.ofDays(7*weeks + 1);
		}

		private Duration subVl(final String key, final Duration defaultValue, final Duration maximum)
		{
			return value(key, min(defaultValue, maximum), Duration.ofDays(1), maximum);
		}

		private static Duration min(final Duration a, final Duration b)
		{
			return a.compareTo(b)>0 ? b : a;
		}

		void purge(final Sampler sampler, final JobContext ctx)
		{
			final long now = Clock.currentTimeMillis();
			final HashMap<Type<?>,Date> limits = new HashMap<>();
			limits.put(SamplerModel      .TYPE, beforeNow(now, model));
			limits.put(SamplerTransaction.TYPE, beforeNow(now, transaction));
			limits.put(SamplerItemCache  .TYPE, beforeNow(now, itemCache));
			limits.put(SamplerClusterNode.TYPE, beforeNow(now, clusterNode));
			limits.put(SamplerMedia      .TYPE, beforeNow(now, media));
			sampler.purge(limits, ctx);
		}

		PurgeDays(final Source source)
		{
			super(source);
		}
	}

	public void purge(final Sampler sampler, final JobContext ctx)
	{
		if(purge!=null)
			purge.purge(sampler, ctx);
	}


	// common code

	public static Factory<SamplerProperties> factory()
	{
		return SamplerProperties::new;
	}

	private SamplerProperties(final Source source)
	{
		super(source);
	}
}
