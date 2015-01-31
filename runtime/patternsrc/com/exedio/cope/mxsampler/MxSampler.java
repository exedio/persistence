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

package com.exedio.cope.mxsampler;

import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import java.util.Date;

public class MxSampler
{
	/**
	 * @deprecated Use project mxsampler instead.
	 */
	@Deprecated
	public MxSampler()
	{
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Always throws a {@link NoSuchMethodError}.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public final Model getModel()
	{
		throw new NoSuchMethodError("MxSampler is no longer supported");
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Always returns {@code original}.
	 */
	@Deprecated
	public static final Properties.Source maskConnectSource(final Properties.Source original)
	{
		return original;
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Always throws a {@link NoSuchMethodError}.
	 */
	@Deprecated
	@SuppressWarnings({"static-method", "unused"})
	public final ConnectToken connect(final String tokenName)
	{
		throw new NoSuchMethodError("MxSampler is no longer supported");
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Does nothing.
	 */
	@Deprecated
	public final void sample()
	{
		// empty
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Does nothing.
	 */
	@Deprecated
	@SuppressWarnings({"static-method", "unused"})
	public final void purge(final int days, final JobContext ctx)
	{
		if(days<=0)
			throw new IllegalArgumentException(String.valueOf(days));
	}

	/**
	 * @deprecated
	 * MxSampler is no longer supported.
	 * Does nothing.
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public final void purge(final Date limit, final JobContext ctx)
	{
		// empty
	}
}
