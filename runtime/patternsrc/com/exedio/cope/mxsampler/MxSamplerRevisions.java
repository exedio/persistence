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

package com.exedio.cope.mxsampler;

import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;

final class MxSamplerRevisions implements Revisions.Factory
{
	public Revisions create(final Context ctx)
	{
		final String db = ctx.getEnvironment().getDatabaseProductName();

		if("mysql".equalsIgnoreCase(db))
			return getMysql();
		else
			return getOthers();
	}

	private static Revisions getMysql()
	{
		return new Revisions(
			new Revision(1, "add ThreadMXBean", new String[] {
				"alter table `MxSamplerGlobal` " +
					"add column `threadCount` int not null," +
					"add column `peakThreadCount` int not null," +
					"add column `totalStartedThreadCount` bigint not null," +
					"add column `daemonThreadCount` int not null",
			})
		);
	}

	private static Revisions getOthers()
	{
		return new Revisions(0);
	}
}
