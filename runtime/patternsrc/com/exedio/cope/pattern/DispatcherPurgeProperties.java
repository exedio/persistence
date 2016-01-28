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

import static com.exedio.cope.misc.Check.requireNonNegative;

import com.exedio.cope.util.Properties;

public final class DispatcherPurgeProperties extends Properties
{
	final int delayDaysSuccess;
	final int delayDaysFinalFailure;

	@SuppressWarnings("synthetic-access")
	public static Factory factory()
	{
		return new Factory(0, 0);
	}

	public static class Factory implements Properties.Factory<DispatcherPurgeProperties>
	{
		private final int delayDaysSuccessDefault;
		private final int delayDaysFinalFailureDefault;

		private Factory(
				final int delayDaysSuccessDefault,
				final int delayDaysFinalFailureDefault)
		{
			this.delayDaysSuccessDefault      = requireNonNegative(delayDaysSuccessDefault,      "delayDaysSuccess");
			this.delayDaysFinalFailureDefault = requireNonNegative(delayDaysFinalFailureDefault, "delayDaysFinalFailure");
		}

		public Factory delayDaysDefault(final int value)
		{
			return delayDaysDefault(value, value);
		}

		public Factory delayDaysDefault(
				final int success,
				final int finalFailure)
		{
			return new Factory(success, finalFailure);
		}

		@Override
		@SuppressWarnings("synthetic-access")
		public DispatcherPurgeProperties create(final Source source)
		{
			return new DispatcherPurgeProperties(
					source,
					delayDaysSuccessDefault,
					delayDaysFinalFailureDefault);
		}
	}

	private DispatcherPurgeProperties(
			final Source source,
			final int delayDaysSuccessDefault,
			final int delayDaysFinalFailureDefault)
	{
		super(source);
		delayDaysSuccess      = value("delayDays.success",      delayDaysSuccessDefault,      0);
		delayDaysFinalFailure = value("delayDays.finalFailure", delayDaysFinalFailureDefault, 0);
	}
}
