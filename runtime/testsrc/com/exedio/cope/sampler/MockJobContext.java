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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class MockJobContext extends AssertionErrorJobContext
{
	private final Model model;
	private int stopIfRequestedCount;
	private final ArrayList<Integer> progress = new ArrayList<>();

	MockJobContext(final Model model)
	{
		this.model = model;
		assertNotNull(model);
	}

	@Override
	public void stopIfRequested()
	{
		assertFalse(model.hasCurrentTransaction());
		stopIfRequestedCount++;
	}

	@Override
	public Duration requestsDeferral()
	{
		assertFalse(model.hasCurrentTransaction());
		return Duration.ZERO;
	}

	@Override
	public boolean supportsMessage()
	{
		return false;
	}

	int getStopIfRequestedCount()
	{
		return stopIfRequestedCount;
	}

	@Override
	public void incrementProgress(final int delta)
	{
		progress.add(delta);
	}

	List<Integer> getProgress()
	{
		return Collections.unmodifiableList(progress);
	}
}
