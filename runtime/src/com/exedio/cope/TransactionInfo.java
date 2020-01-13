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

import com.exedio.cope.ChangeEvent.NotAvailableException;
import java.time.Duration;
import java.util.Date;

abstract class TransactionInfo
{
	abstract String getNodeID();
	abstract boolean isRemote();
	abstract int getRemoteNodeID() throws NotAvailableException;
	abstract long getID() throws NotAvailableException;
	abstract String getName() throws NotAvailableException;
	abstract Date getStartDate() throws NotAvailableException;
	abstract Duration getDuration() throws NotAvailableException;

	// enforce implementation
	@Override
	public abstract String toString();
}
