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

import static java.util.Objects.requireNonNull;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public final class WithinPurgeStampsRule implements TestRule
{
	private final Model model;
	private final Runnable within;

	public WithinPurgeStampsRule(final Model model, final Runnable within)
	{
		this.model = requireNonNull(model);
		this.within = requireNonNull(within);
	}

	@Override
	public Statement apply(final Statement base, final Description description)
	{
		final Model model = this.model; // avoid synthetic-access warning
		final Runnable within = this.within; // avoid synthetic-access warning
		return new Statement()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void evaluate() throws Throwable
			{
				try
				{
					model.withinPurgeStamps = within;
					base.evaluate();
				}
				finally
				{
					model.withinPurgeStamps = null;
				}
			}
		};
	}
}
