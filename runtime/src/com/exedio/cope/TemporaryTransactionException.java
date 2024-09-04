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

import java.io.Serial;

/**
 * Signals, that a persistent operation has failed for a possibly temporary reason.
 * Such reasons include concurrent modification of a data unit by multiple transactions.
 * You may want to recover from this exception by rolling back the current transaction
 * and trying to do it again.
 * It is not guaranteed, that the problem will go away, so never retry indefinitely.
 */
public final class TemporaryTransactionException extends RuntimeException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final int rows;

	TemporaryTransactionException(final String statement, final int rows)
	{
		super(statement);
		this.rows = rows;
	}

	@Override
	public String getMessage()
	{
		return "expected one row, but got " + rows + " on statement: " + super.getMessage();
	}
}
