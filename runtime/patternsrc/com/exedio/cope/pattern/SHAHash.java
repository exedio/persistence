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

package com.exedio.cope.pattern;

import java.util.Set;

import com.exedio.cope.StringCharSetViolationException;
import com.exedio.cope.StringLengthViolationException;

public final class SHAHash extends JavaSecurityHash
{
	private static final long serialVersionUID = 1l;
	
	private static final String HASH = "SHA-512";

	private SHAHash(final boolean optional, final String encoding)
	{
		super(optional, HASH, encoding);
	}

	public SHAHash(final String encoding)
	{
		super(false, HASH, encoding);
	}

	public SHAHash()
	{
		super(false, HASH);
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		result.remove(StringLengthViolationException.class);
		result.remove(StringCharSetViolationException.class);
		return result;
	}
	
	@Override
	public SHAHash optional()
	{
		return new SHAHash(true, getEncoding());
	}
}
