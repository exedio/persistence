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

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;

import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Query;
import com.exedio.cope.junit.CopeAssert;

public class ConsolidateTest extends ConnectedTest
{
	public void testIt()
	{
		samplerModel.createSchema();
		sampler.checkInternal();
		samplerModel.startTransaction("HistoryTest");
		assertEquals(0, SamplerModel.TYPE.search().size());
		assertEquals(0, SamplerItemCache.TYPE.search().size());
		assertEquals(0, SamplerMedia.TYPE.search().size());
		samplerModel.commit();

		final SamplerModel model1 = sampler.sampleInternal();
		waitForSystemTimeChange();
		final SamplerModel model2 = sampler.sampleInternal();
		waitForSystemTimeChange();
		final SamplerModel model3 = sampler.sampleInternal();

		final Query<List<Object>> modelQuery = SamplerConsolidate.makeQuery(SamplerModel.TYPE);
		final Query<List<Object>> mediaQuery = SamplerConsolidate.makeQuery(SamplerMedia.TYPE);

		samplerModel.startTransaction("SampleTest#consolidate");
		{
			final Iterator<List<Object>> result = modelQuery.search().iterator();
			assertEquals(list(
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				result.next().subList(0, 2));
			assertEquals(list(
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				result.next().subList(0, 2));
			assertFalse(result.hasNext());
		}

		{
			final Iterator<List<Object>> medias = mediaQuery.search().iterator();
			assertEquals(list(
					SamplerModel.date.get(model1),
					SamplerModel.date.get(model2)),
				medias.next().subList(0, 2));
			assertEquals(list(
					SamplerModel.date.get(model2),
					SamplerModel.date.get(model3)),
				medias.next().subList(0, 2));
			assertFalse(medias.hasNext());
		}

		samplerModel.commit();

	}

	/**
	 * Wait for new Date() to return a different value to avoid unique violation on SamplerModel.date. Especially useful for Windows systems which have
	 * a low system time resolution.
	 * @see Sampler#sample()
	 */
	private void waitForSystemTimeChange()
	{
		try
		{
			CopeAssert.sleepLongerThan(1);
		}
		catch (final InterruptedException e)
		{
			fail("Correctness of following code is not asserted.");
		}
	}
}
