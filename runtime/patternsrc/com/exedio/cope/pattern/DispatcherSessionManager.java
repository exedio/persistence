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

import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.JobContext;
import io.micrometer.core.instrument.Timer;
import java.util.function.Supplier;
import org.slf4j.Logger;

final class DispatcherSessionManager implements AutoCloseable
{
	private final Supplier<? extends AutoCloseable> supplier;
	private final int limit;
	private final JobContext ctx;
	private final String id;
	private final FeatureTimer createTimer;
	private final FeatureTimer closeTimer;
	private final Logger logger;
	private AutoCloseable session = null;
	private int uses = 0;

	DispatcherSessionManager(
			final Supplier<? extends AutoCloseable> supplier,
			final Dispatcher.Config config,
			final JobContext ctx,
			final String id,
			final FeatureTimer createTimer,
			final FeatureTimer closeTimer,
			final Logger logger)
	{
		this.supplier = supplier;
		this.limit = config.getSessionLimit();
		this.ctx = requireNonNull(ctx);
		this.id = requireNonNull(id);
		this.createTimer = requireNonNull(createTimer);
		this.closeTimer = requireNonNull(closeTimer);
		this.logger = requireNonNull(logger);
	}

	void prepare()
	{
		if(supplier==null)
			return;

		if(session!=null)
		{
			if(uses<limit)
				return;

			if(ctx.supportsMessage())
				ctx.setMessage("closing session after exhausting limit");
			closeInternal("exhausting limit of " + limit);
		}
		assert session==null;

		if(ctx.supportsMessage())
			ctx.setMessage("creating session");
		deferOrStopIfRequested(ctx);
		if(logger.isDebugEnabled())
			logger.debug("creating session");
		final Timer.Sample start = Timer.start();
		session = requireNonNull(supplier.get(), "session");
		final long elapsed = createTimer.stopMillies(start);
		logger.info("created session, took {}ms", elapsed);
		uses = 0;
	}

	AutoCloseable get()
	{
		if(supplier==null)
			return null;

		final AutoCloseable result = requireNonNull(session);
		uses++;
		return result;
	}

	void onFailure()
	{
		if(supplier==null)
			return;

		if(session==null)
			return;

		if(ctx.supportsMessage())
			ctx.setMessage("closing session after failure");
		closeInternal("failure");
	}

	@Override
	public void close()
	{
		if(session==null)
			return;

		closeInternal("dispatching");
	}

	private void closeInternal(final String reason)
	{
		final AutoCloseable session = this.session;
		this.session = null;
		uses = 0;

		if(logger.isDebugEnabled())
			logger.debug("closing session after {}", reason);
		final Timer.Sample start = Timer.start();
		try
		{
			session.close();
		}
		catch(final Exception e)
		{
			if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else
				throw new RuntimeException(id, e);
		}
		final long elapsed = closeTimer.stopMillies(start);
		logger.info("closed session after {}, took {}ms", reason, elapsed);
	}
}
