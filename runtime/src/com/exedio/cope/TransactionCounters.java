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

public final class TransactionCounters
{
	private final long commitWithout;
	private final long commitWith;
	private final long rollbackWithout;
	private final long rollbackWith;

	TransactionCounters(
			final Timer commitWithout,
			final Timer commitWith,
			final Timer rollbackWithout,
			final Timer rollbackWith)
	{
		this.commitWithout   = commitWithout  .count();
		this.commitWith      = commitWith     .count();
		this.rollbackWithout = rollbackWithout.count();
		this.rollbackWith    = rollbackWith   .count();
	}

	public long getCommit()
	{
		return commitWithout + commitWith;
	}

	public long getCommitWithoutConnection()
	{
		return commitWithout;
	}

	public long getCommitWithConnection()
	{
		return commitWith;
	}

	public long getRollback()
	{
		return rollbackWithout + rollbackWith;
	}

	public long getRollbackWithoutConnection()
	{
		return rollbackWithout;
	}

	public long getRollbackWithConnection()
	{
		return rollbackWith;
	}
}
