/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.util.JobContext;

final class MockJobContext implements JobContext
{
	private int requestedToStopCount;
	private final ArrayList<Integer> progress = new ArrayList<Integer>();

	public boolean requestedToStop()
	{
		requestedToStopCount++;
		return false;
	}

	int getRequestedToStopCount()
	{
		return requestedToStopCount;
	}

	public void incrementProgress(final int delta)
	{
		progress.add(delta);
	}

	List<Integer> getProgress()
	{
		return Collections.unmodifiableList(progress);
	}


	// all others are not used

	public boolean supportsMessage()
	{
		throw new RuntimeException();
	}

	public void setMessage(final String message)
	{
		throw new RuntimeException();
	}

	public boolean supportsProgress()
	{
		throw new RuntimeException();
	}

	public void incrementProgress()
	{
		throw new RuntimeException();
	}

	public boolean supportsCompleteness()
	{
		throw new RuntimeException();
	}

	public void setCompleteness(final double completeness)
	{
		throw new RuntimeException();
	}
}
