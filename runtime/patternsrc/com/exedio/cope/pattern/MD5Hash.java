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

package com.exedio.cope.pattern;

import java.util.Set;

import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.Field.Option;

public final class MD5Hash extends JavaSecurityHash
{
	private static final String HASH = "MD5";
	private static final int LENGTH = 32;

	private MD5Hash(final boolean optional, final String encoding)
	{
		super(optional, HASH, LENGTH, encoding);
	}

	public MD5Hash(final String encoding)
	{
		super(false, HASH, LENGTH, encoding);
	}

	public MD5Hash()
	{
		super(false, HASH, LENGTH);
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		final StringField storage = getStorage();
		if(storage.getMinimumLength()<=LENGTH && storage.getMaximumLength()>=LENGTH)
			result.remove(StringLengthViolationException.class);
		return result;
	}
	
	@Override
	public MD5Hash optional()
	{
		return new MD5Hash(true, getEncoding());
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated use {@link com.exedio.cope.Field#toFinal()}, {@link com.exedio.cope.FunctionField#unique()} and {@link com.exedio.cope.Field#optional()} instead.
	 */
	@Deprecated
	public MD5Hash(final Option storageOption)
	{
		super(storageOption.optional, HASH, LENGTH);
	}
}
