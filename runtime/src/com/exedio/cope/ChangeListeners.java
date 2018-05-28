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

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChangeListeners
{
	private static final Logger logger = LoggerFactory.getLogger(ChangeListeners.class);

	private volatile boolean used = false;
	private final LinkedList<WeakReference<ChangeListener>> list = new LinkedList<>();
	private int cleared = 0;
	private int removed = 0;

	private final AtomicInteger failed = new AtomicInteger();

	List<ChangeListener> get()
	{
		final ArrayList<ChangeListener> result;

		synchronized(list)
		{
			final int size = list.size();
			if(size==0)
				return Collections.emptyList();

			// make a copy to avoid ConcurrentModificationViolations
			result = new ArrayList<>(size);
			for(final Iterator<WeakReference<ChangeListener>> i = list.iterator(); i.hasNext(); )
			{
				final ChangeListener listener = i.next().get();
				if(listener==null)
				{
					i.remove();
					cleared++;
				}
				else
					result.add(listener);
			}
		}

		return Collections.unmodifiableList(result);
	}

	boolean isUsed()
	{
		return used;
	}

	ChangeListenerInfo getInfo()
	{
		final int size;
		final int cleared;
		final int removed;

		synchronized(list)
		{
			size = list.size();
			cleared = this.cleared;
			removed = this.removed;
		}
		return new ChangeListenerInfo(size, cleared, removed, failed.get());
	}

	void add(final ChangeListener listener)
	{
		requireNonNull(listener, "listener");

		final WeakReference<ChangeListener> ref = new WeakReference<>(listener);
		synchronized(list)
		{
			list.add(ref);
		}
		used = true;
	}

	void remove(final ChangeListener listener)
	{
		requireNonNull(listener, "listener");

		synchronized(list)
		{
			for(final Iterator<WeakReference<ChangeListener>> i = list.iterator(); i.hasNext(); )
			{
				final ChangeListener l = i.next().get();
				if(l==null)
				{
					i.remove();
					cleared++;
				}
				else if(l==listener)
				{
					i.remove();
					removed++;
				}
			}
		}
	}

	void removeAll()
	{
		synchronized(list)
		{
			final int size = list.size();
			list.clear();
			removed += size;
		}
	}

	void dispatch(final ChangeEvent event, final ChangeListenerDispatcher interrupter)
	{
		final List<ChangeListener> listeners = get();

		for(final ChangeListener listener : listeners)
		{
			if(interrupter.interrupts())
				return;

			try
			{
				listener.onChange(event);
			}
			catch(final Exception | AssertionError e)
			{
				failed.incrementAndGet();
				if(logger.isErrorEnabled())
					logger.error(MessageFormat.format("change listener {0} {1}", event, listener), e);
			}
		}
	}
}
