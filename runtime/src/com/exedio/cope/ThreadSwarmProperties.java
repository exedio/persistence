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

package com.exedio.cope;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

import com.exedio.cope.util.Properties;

final class ThreadSwarmProperties extends Properties
{
	final int initial = value("initial", 1, 1);
	final int max = value("max", 10, 1);

	private final boolean prioritySet   = value("priority.set", false);
	private final int     priorityValue = value("priority.value", MAX_PRIORITY, MIN_PRIORITY);
	final double priorityForGauge = prioritySet ? priorityValue : Thread.NORM_PRIORITY;


	ThreadSwarmProperties(final Source source)
	{
		super(source);

		if(initial>max)
			throw newException(
					"initial",
					"must be less or equal max=" + max + ", " +
					"but was " + initial);
	}

	void setPriority(final ThreadController thread)
	{
		if(prioritySet)
			thread.setPriority(priorityValue);
	}
}
