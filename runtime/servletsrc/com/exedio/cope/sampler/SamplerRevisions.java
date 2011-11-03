/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sampler;

import com.exedio.cope.EnvironmentInfo;
import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;
import com.exedio.cope.RevisionsFuture;

final class SamplerRevisions implements RevisionsFuture
{
	public Revisions get(final EnvironmentInfo environment)
	{
		final String db = environment.getDatabaseProductName();

		if("mysql".equalsIgnoreCase(db))
			return getMysql();
		else
			return getOthers();
	}

	private Revisions getMysql()
	{
		return new Revisions(
			new Revision(1, "use composite SamplerClusterListener",
				"alter table `SamplerModel` " +
					"change `clusterListenerException`  `clusterListener_exception` bigint, " +
					"change `clusterListeneWrongSecret` `clusterListen_wrongSecret` bigint, " +
					"change `clusterListeneMissinMagic` `clusterListene_missiMagic` bigint, " +
					"change `clusterListenerFromMyself` `clusterListene_fromMyself` bigint"
			)
		);
	}

	private Revisions getOthers()
	{
		return new Revisions(0);
	}
}
