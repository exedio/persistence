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

import com.exedio.cope.util.Properties;

final class MysqlProperties extends Properties
{
	private final String timeZone = valueX("connection.timeZone", "+00:00", '\'');

	String timeZoneStatement()
	{
		if("<default>".equals(timeZone))
			return null;

		// from mysql doc:
		// https://dev.mysql.com/doc/refman/5.7/en/time-zone-support.html#time-zone-variables
		//
		// If set to SYSTEM, every MySQL function call that requires a time zone
		// calculation makes a system library call to determine the current
		// system time zone. This call may be protected by a global mutex,
		// resulting in contention.
		return "SET TIME_ZONE='" + timeZone + '\'';
	}


	final boolean connectionCompress = value("connection.compress", false);

	// schema
	final boolean utf8mb4 = value("utf8mb4", true);
	final boolean smallIntegerTypes = value("smallIntegerTypes", true);
	final boolean longConstraintNames = value("longConstraintNames", true);
	final MysqlRowFormat rowFormat = value("rowFormat", MysqlRowFormat.NONE);

	/**
	 * Limits how many rows are purged from sequence tables at once.
	 * Avoids long write locks on sequence tables.
	 */
	final int purgeSequenceLimit = value("purgeSequenceLimit", 10000, 1);


	private String valueX(final String key, final String defaultValue, final char forbidden)
	{
		final String result = value(key, defaultValue);

		final int position = result.indexOf(forbidden);
		if(position>=0)
			throw newException(key,
					"must not contain \"" + forbidden + "\", " +
					"but did at position " + position + " and was \"" + result + '"');

		return result;
	}

	MysqlProperties(final Source source)
	{
		super(source);
	}
}
