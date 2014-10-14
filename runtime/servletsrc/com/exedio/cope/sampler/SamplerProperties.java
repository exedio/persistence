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

package com.exedio.cope.sampler;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class SamplerProperties extends Properties
{
	// cope

	private final ConnectProperties cope = value("cope", mask(ConnectProperties.factory()));

	private static Factory<ConnectProperties> mask(final Factory<ConnectProperties> original)
	{
		return new Factory<ConnectProperties>()
		{
			@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
			@Override
			public ConnectProperties create(final Source source)
			{
				// TODO deprecate Sampler.maskConnectSource when moved into framework
				return original.create(Sampler.maskConnectSource(source));
			}
		};
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

	private final int purgeDays = value("purgeDays", 8, 0); // amply one week

	public void purge(final Sampler sampler, final JobContext ctx)
	{
		if(purgeDays>0)
			sampler.purge(purgeDays, ctx);
	}


	// common code

	public static Factory<SamplerProperties> factory()
	{
		return new Factory<SamplerProperties>()
		{
			@Override
			@SuppressWarnings("synthetic-access")
			public SamplerProperties create(final Source source)
			{
				return new SamplerProperties(source);
			}
		};
	}

	private SamplerProperties(final Source source)
	{
		super(source);
	}
}
