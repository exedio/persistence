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

import com.exedio.cope.misc.FactoryProperties;
import com.exedio.cope.util.Properties;

public final class DispatcherPurgeProperties extends FactoryProperties<DispatcherPurgeProperties.Factory>
{
	final int retainDaysSuccess      = value("retainDays.success",      factory.retainDaysSuccess,      0);
	final int retainDaysFinalFailure = value("retainDays.finalFailure", factory.retainDaysFinalFailure, 0);

	public static Factory factory()
	{
		return new Factory(0, 0);
	}

	private DispatcherPurgeProperties(final Source source, final Factory factory)
	{
		super(source, factory);
	}


	public static final class Factory implements Properties.Factory<DispatcherPurgeProperties>
	{
		private final int retainDaysSuccess;
		private final int retainDaysFinalFailure;

		private Factory(
				final int retainDaysSuccess,
				final int retainDaysFinalFailure)
		{
			this.retainDaysSuccess      = requireNonNegative(retainDaysSuccess,      "retainDaysSuccess");
			this.retainDaysFinalFailure = requireNonNegative(retainDaysFinalFailure, "retainDaysFinalFailure");
		}

		public Factory retainDaysDefault(final int value)
		{
			return retainDaysDefault(value, value);
		}

		@SuppressWarnings("static-method") // OK: will have to be non-static when there are more fields
		public Factory retainDaysDefault(
				final int success,
				final int finalFailure)
		{
			return new Factory(success, finalFailure);
		}

		@Override
		public DispatcherPurgeProperties create(final Source source)
		{
			return new DispatcherPurgeProperties(source, this);
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #retainDaysDefault(int)} instead
		 */
		@Deprecated
		public Factory delayDaysDefault(final int value)
		{
			return retainDaysDefault(value);
		}

		/**
		 * @deprecated Use {@link #retainDaysDefault(int,int)} instead
		 */
		@Deprecated
		public Factory delayDaysDefault(
				final int success,
				final int finalFailure)
		{
			return retainDaysDefault(success, finalFailure);
		}
	}
}
