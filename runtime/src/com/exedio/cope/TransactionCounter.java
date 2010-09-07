/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

final class TransactionCounter
{
	private volatile long commitWithoutConnection = 0;
	private volatile long commitWithConnection = 0;
	private volatile long rollbackWithoutConnection = 0;
	private volatile long rollbackWithConnection = 0;

	void count(final boolean rollback, final boolean hadConnection)
	{
		if(hadConnection)
			if(rollback)
				rollbackWithConnection++;
			else
				commitWithConnection++;
		else
			if(rollback)
				rollbackWithoutConnection++;
			else
				commitWithoutConnection++;
	}

	TransactionCounters getCounters()
	{
		return new TransactionCounters(
				commitWithoutConnection,
				commitWithConnection,
				rollbackWithoutConnection,
				rollbackWithConnection);
	}
}
