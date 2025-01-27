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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Dispatcher.Config.DEFAULT_FAILURE_LIMIT;
import static com.exedio.cope.pattern.Dispatcher.Config.DEFAULT_NARROW_CONDITION;
import static com.exedio.cope.pattern.Dispatcher.Config.DEFAULT_SEARCH_SIZE;
import static com.exedio.cope.pattern.Dispatcher.Config.DEFAULT_SESSION_LIMIT;
import static com.exedio.cope.util.Check.requireGreaterZero;

import com.exedio.cope.misc.FactoryProperties;
import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.util.Properties;

public final class DispatcherProperties extends FactoryProperties<DispatcherProperties.Factory>
{
	private final int failureLimit = value("failureLimit", factory.failureLimit,  1);
	private final int searchSize   = value("searchSize",   DEFAULT_SEARCH_SIZE,   1);
	private final int sessionLimit = value("sessionLimit", factory.sessionLimit,  1);
	private final Config value =
			new Config(
				failureLimit,
				searchSize,
				sessionLimit,
				DEFAULT_NARROW_CONDITION);

	public Config get()
	{
		return value;
	}

	public static Factory factory()
	{
		return new Factory(
				DEFAULT_FAILURE_LIMIT,
				DEFAULT_SESSION_LIMIT);
	}

	public static final class Factory implements Properties.Factory<DispatcherProperties>
	{
		private final int failureLimit;
		private final int sessionLimit;

		Factory(
				final int failureLimit,
				final int sessionLimit)
		{
			this.failureLimit = requireGreaterZero(failureLimit, "failureLimit"); // corresponds to Dispatcher.Config constructor
			this.sessionLimit = requireGreaterZero(sessionLimit, "sessionLimit"); // corresponds to Dispatcher.Config constructor
		}

		public Factory failureLimit(final int failureLimit)
		{
			return new Factory(
					failureLimit,
					sessionLimit);
		}

		public Factory sessionLimit(final int sessionLimit)
		{
			return new Factory(
					failureLimit,
					sessionLimit);
		}

		@Override
		public DispatcherProperties create(final Source source)
		{
			return new DispatcherProperties(source, this);
		}
	}

	private DispatcherProperties(final Source source, final Factory factory)
	{
		super(source, factory);
	}
}
