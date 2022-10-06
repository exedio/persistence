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

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

final class TransactionCounter
{
	private Timer commitWithout   = new NoNameTimer();
	private Timer commitWith      = new NoNameTimer();
	private Timer rollbackWithout = new NoNameTimer();
	private Timer rollbackWith    = new NoNameTimer();

	void onModelNameSet(final MetricsBuilder metricsTemplate)
	{
		final MetricsBuilder metrics = metricsTemplate.name(Transaction.class);
		commitWithout   = counter(metrics, "commit", "without");
		commitWith      = counter(metrics, "commit", "with");
		rollbackWithout = counter(metrics, "rollback", "without");
		rollbackWith    = counter(metrics, "rollback", "with");
	}

	private static Timer counter(
			final MetricsBuilder metrics,
			final String end,
			final String connection)
	{
		return metrics.timer("finished",
				"Transactions finished that required or did not require a database (JDBC) connection",
				Tags.of("end", end, "connection", connection));
	}

	long countAndReturnNanos(final Timer.Sample start, final boolean commit, final boolean hadConnection)
	{
		final Timer c;

		if(hadConnection)
			if(commit)
				c = commitWith;
			else
				c = rollbackWith;
		else
			if(commit)
				c = commitWithout;
			else
				c = rollbackWithout;

		return start.stop(c);
	}

	TransactionCounters get()
	{
		return new TransactionCounters(
				commitWithout,
				commitWith,
				rollbackWithout,
				rollbackWith);
	}
}
