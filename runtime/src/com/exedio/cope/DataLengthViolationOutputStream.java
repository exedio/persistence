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

import java.io.OutputStream;

final class DataLengthViolationOutputStream extends LengthLimitingOutputStream
{
	private final DataField field;
	private final Item exceptionItem;

	DataLengthViolationOutputStream(
			final DataField field,
			final OutputStream back,
			final Item exceptionItem)
	{
		super(field.getMaximumLength(), back);
		this.field = field;
		this.exceptionItem = exceptionItem;

		if(back instanceof DataLengthViolationOutputStream)
			throw new RuntimeException(field.toString());
	}

	@Override
	protected DataLengthViolationException newFailure(final long transferred)
	{
		return new DataLengthViolationException(field, exceptionItem, transferred, false);
	}

	@Override
	public void flush()
	{
		throw new RuntimeException(field.toString());
	}

	@Override
	public void close()
	{
		throw new RuntimeException(field.toString());
	}
}
