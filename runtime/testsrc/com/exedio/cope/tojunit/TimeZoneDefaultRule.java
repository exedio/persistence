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

package com.exedio.cope.tojunit;

import static java.util.TimeZone.getDefault;
import static java.util.TimeZone.setDefault;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.TimeZone;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class TimeZoneDefaultRule implements AfterEachCallback, ParameterResolver
{
	private TimeZoneDefaultRule()
	{
		// just make private
	}

	private TimeZone backup;

	public void set(final TimeZone zone)
	{
		if(backup==null)
		{
			backup = getDefault();
			assertNotNull(backup);
		}

		setDefault(zone);
	}

	@Override
	public boolean supportsParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext)
	{
		return TimeZoneDefaultRule.class==parameterContext.getParameter().getType();
	}

	@Override
	public Object resolveParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext)
	{
		return this;
	}

	@Override
	public void afterEach(final ExtensionContext context)
	{
		if(backup!=null)
		{
			setDefault(backup);
			backup = null;
		}
	}
}
