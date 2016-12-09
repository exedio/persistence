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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Properties;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;

public abstract class ConnectedTest extends TestWithEnvironment
{
	ConnectedTest()
	{
		super(MODEL);
	}

	boolean c;

	@Before public final void setUpConnectedTest()
	{
		final ConnectProperties props = model.getConnectProperties();
		c = props.getItemCacheLimit()>0;
		samplerModel.connect(ConnectProperties.create(
				Sampler.maskConnectSourceInternal(maskRevisionSchemaNames(props.getSourceObject()))));
		sampler.reset();
	}

	private static Properties.Source maskRevisionSchemaNames(final Properties.Source original)
	{
		return new Properties.Source(){

			@Override
			public String get(final String key)
			{
				// If this is explicitly specified, the table name
				// collides between the sampler model and the sampled model.
				if("schema.revision.table" .equals(key) ||
					"schema.revision.unique".equals(key))
					return null;

				return original.get(key);
			}

			@Override
			public String getDescription()
			{
				return original.getDescription();
			}

			@Override
			public Collection<String> keySet()
			{
				return original.keySet();
			}
		};
	}

	@SuppressWarnings("static-method")
	@After public final void tearDownConnectedTest()
	{
		samplerModel.rollbackIfNotCommitted();
		samplerModel.dropSchema();
		samplerModel.disconnect();
	}
}
