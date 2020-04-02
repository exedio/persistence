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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Stuff.MODEL;
import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;
import static com.exedio.cope.tojunit.TestSources.erase;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ConnectedTest extends TestWithEnvironment
{
	ConnectedTest()
	{
		super(MODEL);
	}

	boolean c;

	@BeforeEach final void setUpConnectedTest()
	{
		final ConnectProperties props = model.getConnectProperties();
		c = props.getItemCacheLimit()>0;
		samplerModel.connect(ConnectProperties.factory().
				revisionTable("SamplerRevision", "SamplerRevisionUnique").create(maskRevisionSchemaNames(props.getSourceObject())));
		sampler.reset();
	}

	private static Properties.Source maskRevisionSchemaNames(final Properties.Source original)
	{
		// If this is explicitly specified, the table name
		// collides between the sampler model and the sampled model.
		return
				erase("schema.revision.table",
				erase("schema.revision.unique",
				original));
	}

	@AfterEach final void tearDownConnectedTest()
	{
		samplerModel.rollbackIfNotCommitted();
		samplerModel.dropSchema();
		samplerModel.disconnect();
	}
}
