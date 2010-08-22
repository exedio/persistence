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

import java.util.Date;

public final class TransactionInfo
{
	private final boolean remote;
	private final long id;
	private final String name;
	private final long startDate;

	TransactionInfo(final Transaction transaction)
	{
		this.remote = false;
		this.id = transaction.getID();
		this.name = transaction.getName();
		this.startDate = transaction.getStartDate().getTime();
	}

	TransactionInfo()
	{
		this.remote = true;
		this.id = 0;
		this.name = null;
		this.startDate = 0;
	}

	public boolean isRemote()
	{
		return remote;
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 */
	public long getID()
	{
		assertLocal();
		return id;
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 */
	public String getName()
	{
		assertLocal();
		return name;
	}

	/**
	 * @throws IllegalStateException is that information is not available
	 */
	public Date getStartDate()
	{
		assertLocal();
		return new Date(startDate);
	}

	private void assertLocal()
	{
		if(remote)
			throw new IllegalStateException("not available");
	}
}
