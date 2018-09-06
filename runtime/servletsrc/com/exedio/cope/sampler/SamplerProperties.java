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

import static com.exedio.cope.sampler.Sampler.daysBeforeNow;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
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

	private final long transactionDuration = 1000l * value("transactionDurationSeconds", 10, 0); // 10 seconds

	public void sample(final Sampler sampler)
	{
		sample(sampler, null);
	}

	public void sample(final Sampler sampler, final String buildTag)
	{
		sampler.sampleInternal(transactionDuration, buildTag);
	}


	// purge

	final PurgeDays purgeDays = value("purgeDays", true, PurgeDays::new);

	static final class PurgeDays extends Properties
	{
		final int model       = value("model",       ampleWeeks(8), 1    );
		final int transaction = subVl("transaction", ampleWeeks(8), model);
		final int itemCache   = subVl("itemCache",   ampleWeeks(1), model);
		final int clusterNode = subVl("clusterNode", ampleWeeks(4), model);
		final int media       = subVl("media",       ampleWeeks(4), model);

		private static int ampleWeeks(final int weeks)
		{
			return 7*weeks + 1;
		}

		private int subVl(final String key, final int defaultValue, final int maximum)
		{
			final int result = value(key, defaultValue, 1);
			if(result>maximum)
				throw newException(key,
						"must be less or equal purgeDays.model=" + maximum + ", " +
						"but was " + result);
			return result;
		}

		void purge(final Sampler sampler, final JobContext ctx)
		{
			final long now = Clock.currentTimeMillis();
			final HashMap<Type<?>,Date> limits = new HashMap<>();
			limits.put(SamplerModel      .TYPE, daysBeforeNow(now, model));
			limits.put(SamplerTransaction.TYPE, daysBeforeNow(now, transaction));
			limits.put(SamplerItemCache  .TYPE, daysBeforeNow(now, itemCache));
			limits.put(SamplerClusterNode.TYPE, daysBeforeNow(now, clusterNode));
			limits.put(SamplerMedia      .TYPE, daysBeforeNow(now, media));
			sampler.purge(limits, ctx);
		}

		PurgeDays(final Source source)
		{
			super(source);
		}
	}

	public void purge(final Sampler sampler, final JobContext ctx)
	{
		if(purgeDays!=null)
			purgeDays.purge(sampler, ctx);
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
