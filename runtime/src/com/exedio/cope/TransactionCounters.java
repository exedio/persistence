/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class TransactionCounters
{
	private final long commitWithoutConnection;
	private final long commitWithConnection;
	private final long rollbackWithoutConnection;
	private final long rollbackWithConnection;
	
	TransactionCounters(
			final long commitWithoutConnection,
			final long commitWithConnection,
			final long rollbackWithoutConnection,
			final long rollbackWithConnection)
	{
		this.commitWithoutConnection = commitWithoutConnection;
		this.commitWithConnection = commitWithConnection;
		this.rollbackWithoutConnection = rollbackWithoutConnection;
		this.rollbackWithConnection = rollbackWithConnection;
	}

	public long getCommit()
	{
		return commitWithoutConnection + commitWithConnection;
	}

	public long getCommitWithoutConnection()
	{
		return commitWithoutConnection;
	}

	public long getCommitWithConnection()
	{
		return commitWithConnection;
	}

	public long getRollback()
	{
		return rollbackWithoutConnection + rollbackWithConnection;
	}

	public long getRollbackWithoutConnection()
	{
		return rollbackWithoutConnection;
	}

	public long getRollbackWithConnection()
	{
		return rollbackWithConnection;
	}
}
