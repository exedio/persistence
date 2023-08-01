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

package com.exedio.cope.misc;

import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.ChangeHook.Factory;
import com.exedio.cope.Model;
import javax.annotation.Nonnull;

public final class ChangeHooks
{
	public static final Factory EMPTY = new EmptyFactory();

	private static final class EmptyFactory implements Factory
	{
		@Override
		public ChangeHook create(final Model model)
		{
			requireNonNull(model, "model");
			return EMPTY_HOOK;
		}

		@Override
		public String toString()
		{
			return "empty";
		}
	}

	private static final ChangeHook EMPTY_HOOK = new Empty();

	private static final class Empty implements ChangeHook
	{
		@Override
		public String toString()
		{
			return "empty";
		}
	}

	public static Factory cascade(Factory... hooks)
	{
		if(hooks!=null && hooks.length==0)
			return EMPTY;

		//noinspection DataFlowIssue OK: should fail if null
		hooks = requireNonEmptyAndCopy(hooks, "hooks");
		if(hooks.length==1)
			return hooks[0];

		return new CascadeFactory(hooks);
	}

	private static final class CascadeFactory implements Factory
	{
		private final Factory[] hooks;

		CascadeFactory(final Factory[] hooks)
		{
			this.hooks = hooks;
		}

		@Override
		public ChangeHook create(final Model model)
		{
			final ChangeHook[] h = new ChangeHook[hooks.length];
			for(int i = 0; i<hooks.length; i++)
				h[i] = ChangeHooks.create(hooks[i], model);
			return new CascadeChangeHook(h);
		}

		@Override
		public String toString()
		{
			final StringBuilder bf = new StringBuilder();
			boolean first = true;
			for(final Factory hook : hooks)
			{
				if(first)
					first = false;
				else
					bf.append(" / ");

				bf.append(hook);
			}
			return bf.toString();
		}
	}


	@Nonnull
	public static ChangeHook create(
			@Nonnull final Factory factory,
			@Nonnull final Model model)
	{
		requireNonNull(model, "model");

		final ChangeHook result = factory.create(model);

		//noinspection ConstantValue OK: checking bad behaviour
		if(result==null)
			throw new NullPointerException(
					"ChangeHook.Factory returned null: " + factory);

		return result;
	}


	private ChangeHooks()
	{
		// prevent instantiation
	}
}
