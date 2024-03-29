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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Sources;

public final class CopeRuntimeRule extends CopeRule
{
	private final TestWithEnvironment test;

	public CopeRuntimeRule(final Model model, final TestWithEnvironment test)
	{
		super(model);
		this.test = test;
	}

	@Override
	public ConnectProperties getConnectProperties()
	{
		return ConnectProperties.create(test.override(
				Sources.load(ConnectProperties.getDefaultPropertyFile())));
	}
}
