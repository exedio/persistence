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
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Type;
import org.junit.jupiter.api.Test;

public class SampleItemCacheTest extends ConnectedTest
{
	public SampleItemCacheTest()
	{
		copeRule.omitTransaction();
	}

	@Test void testNormal() throws InterruptedException
	{
		samplerModel.createSchema();

		touch();
		assertEquals(null, sampler.sampleInternal());

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE, 1);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE, 1);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE, 1);
		}
	}

	@Test void testEmptyStart() throws InterruptedException
	{
		samplerModel.createSchema();

		assertEquals(null, sampler.sampleInternal());

		sleepLongerThan(1);
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE);
		}

		sleepLongerThan(1);
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem .TYPE);
			assertIt(model, SampledModelItem2.TYPE, 1);
		}
	}

	private static void touch()
	{
		MODEL.startTransaction("HistoryTest2");
		SampledModelItem2.TYPE.newItem(SampledModelItem2.code.map("zack"));
		MODEL.commit();
	}

	private void assertIt(
			final SamplerModel model,
			final Type<?> type,
			final int invalidationsOrdered)
	{
		samplerModel.startTransaction("HistoryTest2");
		final SamplerItemCache i = SamplerItemCache.forModelAndType(model, type);
		if(c)
		{
			assertNotNull(i);
			assertEquals(invalidationsOrdered, i.getInvalidationsOrdered(), "invalidationsOrdered");
		}
		else
		{
			assertNull(i);
		}
		samplerModel.commit();
	}

	private static void assertIt(
			final SamplerModel model,
			final Type<?> type)
	{
		samplerModel.startTransaction("HistoryTest2");
		assertNull(SamplerItemCache.forModelAndType(model, type));
		samplerModel.commit();
	}
}
