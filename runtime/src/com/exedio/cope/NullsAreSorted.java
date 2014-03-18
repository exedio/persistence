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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.EnumSet;

enum NullsAreSorted
{
	atStart,
	atEnd,
	high    { @Override boolean low() { return false; }},
	low     { @Override boolean low() { return true; }};

	static NullsAreSorted valueOf(final DatabaseMetaData dmd) throws SQLException
	{
		final EnumSet<NullsAreSorted> set = EnumSet.noneOf(NullsAreSorted.class);
		if(dmd.nullsAreSortedAtStart()) set.add(atStart);
		if(dmd.nullsAreSortedAtEnd  ()) set.add(atEnd  );
		if(dmd.nullsAreSortedHigh   ()) set.add(high   );
		if(dmd.nullsAreSortedLow    ()) set.add(low    );
		if(set.size()!=1)
			throw new IllegalStateException("inconsistent nullsAreSorted: " + set);
		return set.iterator().next();
	}

	boolean low()
	{
		throw new RuntimeException(name());
	}
}
