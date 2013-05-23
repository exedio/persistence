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

package com.exedio.cope;

final class TransactionCounter
{
	private final VolatileLong commitWithoutConnection = new VolatileLong();
	private final VolatileLong commitWithConnection = new VolatileLong();
	private final VolatileLong rollbackWithoutConnection = new VolatileLong();
	private final VolatileLong rollbackWithConnection = new VolatileLong();

	void count(final boolean rollback, final boolean hadConnection)
	{
		final VolatileLong c;

		if(hadConnection)
			if(rollback)
				c = rollbackWithConnection;
			else
				c = commitWithConnection;
		else
			if(rollback)
				c = rollbackWithoutConnection;
			else
				c = commitWithoutConnection;

		c.inc();
	}

	TransactionCounters getCounters()
	{
		return new TransactionCounters(
				commitWithoutConnection.get(),
				commitWithConnection.get(),
				rollbackWithoutConnection.get(),
				rollbackWithConnection.get());
	}
}
