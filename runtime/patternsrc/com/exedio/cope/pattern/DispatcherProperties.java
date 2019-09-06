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
import static com.exedio.cope.pattern.Dispatcher.Config.DEFAULT_SEARCH_SIZE;

import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.util.Properties;

public final class DispatcherProperties extends Properties
{
	private final int failureLimit = value("failureLimit", DEFAULT_FAILURE_LIMIT, 1);
	private final int searchSize   = value("searchSize",   DEFAULT_SEARCH_SIZE,   1);
	private final Config value =
			new Config(
				failureLimit,
				searchSize);

	public Config get()
	{
		return value;
	}

	public static Factory<DispatcherProperties> factory()
	{
		return DispatcherProperties::new;
	}

	private DispatcherProperties(final Source source)
	{
		super(source);
	}
}
