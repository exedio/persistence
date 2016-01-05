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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public final class Subclasses extends Suite
{
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Include
	{
		public Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Exclude
	{
		public Class<?>[] value();
	}

	public Subclasses(final Class<?> klass, final RunnerBuilder builder) throws InitializationError
	{
		super(klass, builder);

		final Include include = klass.getAnnotation(Include.class);
		final Exclude exclude = klass.getAnnotation(Exclude.class);
		if(include==null && exclude==null)
			throw new InitializationError(
					"missing annotation @" +
					Include.class.getName() + " or @" +
					Exclude.class.getName() +" at " + klass);
		if(include!=null && exclude!=null)
			throw new InitializationError(
					"not allowed both annotation @" +
					Include.class.getName() + " and @" +
					Exclude.class.getName() +" at " + klass);

		final Filter filter =
				include!=null
				? new IncludeFilter(include)
				: new ExcludeFilter(exclude);
		try
		{
			filter(filter);
		}
		catch(final NoTestsRemainException e)
		{
			throw new InitializationError(e);
		}
	}

	private static class IncludeFilter extends Filter
	{
		private final Class<?>[] value;

		IncludeFilter(final Include annotation)
		{
			this.value = annotation.value();
		}

		@Override
		public String describe()
		{
			return "include subclasses of " + Arrays.asList(value);
		}

		@Override
		public boolean shouldRun(final Description description)
		{
			for(final Class<?> c : value)
				if(c.isAssignableFrom(description.getTestClass()))
					return true;

			for(final Description each : description.getChildren())
				if(shouldRun(each))
					return true;

			return false;
		}
	}

	private static class ExcludeFilter extends Filter
	{
		private final Class<?>[] value;

		ExcludeFilter(final Exclude annotation)
		{
			this.value = annotation.value();
		}

		@Override
		public String describe()
		{
			return "exclude subclasses of " + Arrays.asList(value);
		}

		@Override
		public boolean shouldRun(final Description description)
		{
			for(final Class<?> c : value)
				if(c.isAssignableFrom(description.getTestClass()))
					return false;

			return true;
		}
	}
}
