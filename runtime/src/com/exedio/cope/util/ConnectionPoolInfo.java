/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;


public final class ConnectionPoolInfo
{
	private final int idleCount;
	private final PoolCounter counter;
	
	public ConnectionPoolInfo(
			final int idleCount,
			final PoolCounter counter)
	{
		if(counter==null)
			throw new NullPointerException();
		
		this.idleCount = idleCount;
		this.counter = counter;
	}
	
	public int getIdleCounter()
	{
		return idleCount;
	}
	
	public PoolCounter getCounter()
	{
		return counter;
	}
}
