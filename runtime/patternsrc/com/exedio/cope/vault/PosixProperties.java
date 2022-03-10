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

package com.exedio.cope.vault;

import com.exedio.cope.util.Properties;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.Set;

abstract class PosixProperties extends Properties
{
	Set<PosixFilePermission> value(final String key, final Set<PosixFilePermission> defaultValue)
	{
		final String DEFAULT = "";
		final String value = value(key, defaultValue!=null ? PosixFilePermissions.toString(defaultValue) : DEFAULT);
		if(DEFAULT.equals(value))
			return null;

		final Set<PosixFilePermission> result;
		try
		{
			result = PosixFilePermissions.fromString(value);
		}
		catch(final IllegalArgumentException e)
		{
			throw newException(key,
					"must be posix file permissions according to PosixFilePermissions.fromString, " +
					"but was '" + value + '\'', e);
		}
		return Collections.unmodifiableSet(result);
	}

	PosixProperties(final Source source)
	{
		super(source);
	}
}
