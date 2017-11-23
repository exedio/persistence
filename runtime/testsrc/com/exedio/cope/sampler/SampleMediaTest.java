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

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.exedio.cope.pattern.Media;
import org.junit.jupiter.api.Test;

public class SampleMediaTest extends ConnectedTest
{
	public SampleMediaTest()
	{
		copeRule.omitTransaction();
	}

	@Test public void testNormal() throws InterruptedException
	{
		samplerModel.createSchema();

		touch();
		assertEquals(null, sampler.sampleInternal());

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB, 1);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB, 1);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB, 1);
		}
	}

	@Test public void testEmptyStart() throws InterruptedException
	{
		samplerModel.createSchema();

		assertEquals(null, sampler.sampleInternal());

		sleepLongerThan(1);
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB);
		}

		sleepLongerThan(1);
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB);
		}

		sleepLongerThan(1);
		touch();
		{
			final SamplerModel model = sampler.sampleInternal();
			assertIt(model, SampledModelItem.mediaA);
			assertIt(model, SampledModelItem.mediaB, 1);
		}
	}

	private static void touch()
	{
		SampledModelItem.mediaB.incrementDelivered();
	}

	private static void assertIt(
			final SamplerModel model,
			final Media media,
			final int delivered)
	{
		samplerModel.startTransaction("HistoryTest2");
		final SamplerMedia i = SamplerMedia.forModelAndType(model, media);
		assertNotNull(i);
		assertEquals(delivered, i.getDelivered(), "delivered");
		samplerModel.commit();
	}

	private static void assertIt(
			final SamplerModel model,
			final Media media)
	{
		samplerModel.startTransaction("HistoryTest2");
		final SamplerMedia i = SamplerMedia.forModelAndType(model, media);
		assertNull(i);
		samplerModel.commit();
	}
}
