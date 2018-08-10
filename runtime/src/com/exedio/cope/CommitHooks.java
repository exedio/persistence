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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;

final class CommitHooks
{
	private volatile boolean expired = false;
	private volatile Content content = null;

	@Nonnull
	<R extends Runnable> R add(final R hook)
	{
		requireNonNull(hook, "hook");
		if(expired)
			throw new IllegalStateException("hooks are or have been handled");

		if(content==null)
			content = new Content();

		return content.add(hook);
	}

	void handle(final boolean commit)
	{
		expired = true;

		if(content!=null)
		{
			content.handle(commit);
			content = null; // GC
		}
	}

	int getCount()
	{
		final Content content = this.content;
		return content!=null ? content.count : 0;
	}

	int getDuplicates()
	{
		final Content content = this.content;
		return content!=null ? content.duplicates : 0;
	}

	private static final class Content
	{
		private final LinkedHashMap<Runnable,Runnable> list = new LinkedHashMap<>();
		@SuppressFBWarnings("VO_VOLATILE_INCREMENT") // OK: is never incremented concurrently, as this works on current transaction only
		volatile int count = 0;
		@SuppressFBWarnings("VO_VOLATILE_INCREMENT") // OK: is never incremented concurrently, as this works on current transaction only
		volatile int duplicates = 0;

		Content(){} // just make package private

		@Nonnull
		<R extends Runnable> R add(final R hook)
		{
			final Runnable present = list.putIfAbsent(hook, hook);
			if(present==null)
			{
				//noinspection NonAtomicOperationOnVolatileField OK: is never incremented concurrently, as this works on current transaction only
				count++;
				return hook;
			}
			else
			{
				//noinspection NonAtomicOperationOnVolatileField OK: is never incremented concurrently, as this works on current transaction only
				duplicates++;
				@SuppressWarnings("unchecked")
				final R result = (R)present;
				return result;
			}
		}

		void handle(final boolean commit)
		{
			if(commit)
			{
				for(final Runnable hook : list.keySet())
					hook.run();
			}

			list.clear(); // GC
		}
	}
}
