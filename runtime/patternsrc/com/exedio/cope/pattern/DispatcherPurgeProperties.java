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

import static java.time.Duration.ZERO;
import static java.time.Duration.ofDays;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.FactoryProperties;
import com.exedio.cope.util.Properties;
import java.time.Duration;

public final class DispatcherPurgeProperties extends FactoryProperties<DispatcherPurgeProperties.Factory>
{
	final Duration retainSuccess      = value("retain.success",      factory.retainSuccess,      ZERO);
	final Duration retainFinalFailure = value("retain.finalFailure", factory.retainFinalFailure, ZERO);

	public static Factory factory()
	{
		return new Factory(ZERO, ZERO);
	}

	private DispatcherPurgeProperties(final Source source, final Factory factory)
	{
		super(source, factory);
	}


	public static final class Factory implements Properties.Factory<DispatcherPurgeProperties>
	{
		private final Duration retainSuccess;
		private final Duration retainFinalFailure;

		private Factory(
				final Duration retainSuccess,
				final Duration retainFinalFailure)
		{
			this.retainSuccess      = requireNonNegative(retainSuccess,      "retainSuccess");
			this.retainFinalFailure = requireNonNegative(retainFinalFailure, "retainFinalFailure");
		}

		// TODO remove when available in new version of copeutil
		private static Duration requireNonNegative(final Duration value, final String name)
		{
			requireNonNull(value, name);
			if(value.isNegative())
				throw new IllegalArgumentException(name + " must not be negative, but was " + value);
			return value;
		}

		/**
		 * @param value
		 *        How many days unpended items are retained.
		 *        Zero retains forever.
		 */
		public Factory retainDaysDefault(final int value)
		{
			return retainDefault(ofDays(value));
		}

		/**
		 * @param value
		 *        How many days unpended items are retained.
		 *        {@link Duration#ZERO Zero} retains forever.
		 */
		public Factory retainDefault(final Duration value)
		{
			return retainDefault(value, value);
		}

		/**
		 * @param success
		 *        How many days {@link Dispatcher.Result#success successfully} dispatched items are retained.
		 *        Zero retains forever.
		 * @param finalFailure
		 *        How many days {@link Dispatcher.Result#finalFailure finally failed} items are retained.
		 *        Zero retains forever.
		 */
		public Factory retainDaysDefault(
				final int success,
				final int finalFailure)
		{
			return retainDefault(ofDays(success), ofDays(finalFailure));
		}

		/**
		 * @param success
		 *        How many days {@link Dispatcher.Result#success successfully} dispatched items are retained.
		 *        {@link Duration#ZERO Zero} retains forever.
		 * @param finalFailure
		 *        How many days {@link Dispatcher.Result#finalFailure finally failed} items are retained.
		 *        {@link Duration#ZERO Zero} retains forever.
		 */
		@SuppressWarnings("static-method") // OK: will have to be non-static when there are more fields
		public Factory retainDefault(
				final Duration success,
				final Duration finalFailure)
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
