/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChangeListeners
{
	static final Logger logger = LoggerFactory.getLogger(ChangeListeners.class);

	private volatile boolean used = false;
	private final LinkedList<WeakReference<ChangeListener>> list = new LinkedList<>();
	private final VolatileInt cleared = new VolatileInt();
	private final VolatileInt removed = new VolatileInt();
	private final VolatileInt failed = new VolatileInt();

	ChangeListeners()
	{
		// empty
	}

	List<ChangeListener> get()
	{
		synchronized(list)
		{
			final int size = list.size();
			if(size==0)
				return Collections.<ChangeListener>emptyList();

			// make a copy to avoid ConcurrentModificationViolations
			final ArrayList<ChangeListener> result = new ArrayList<>(size);
			int cleared = 0;
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

			this.cleared.inc(cleared);

			return Collections.unmodifiableList(result);
		}
	}

	boolean isUsed()
	{
		return used;
	}

	ChangeListenerInfo getInfo()
	{
		final int size;
		synchronized(list)
		{
			size = list.size();
		}
		return new ChangeListenerInfo(size, cleared.get(), removed.get(), failed.get());
	}

	void add(final ChangeListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		final WeakReference<ChangeListener> ref = new WeakReference<>(listener);
		synchronized(list)
		{
			list.add(ref);
		}
		used = true;
	}

	void remove(final ChangeListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		synchronized(list)
		{
			int cleared = 0;
			int removed = 0;
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
			this.cleared.inc(cleared);
			this.removed.inc(removed);
		}
	}

	void dispatch(final ChangeEvent event, final ChangeListenerDispatcher interrupter)
	{
		final List<ChangeListener> listeners = get();

		for(final ChangeListener listener : listeners)
		{
			if(interrupter.requestedToStop())
				return;

			try
			{
				listener.onChange(event);
			}
			catch(final Exception e)
			{
				onDispatchFailure(event, listener, e);
			}
			catch(final AssertionError e)
			{
				onDispatchFailure(event, listener, e);
			}
		}
	}

	private void onDispatchFailure(
			final ChangeEvent event,
			final ChangeListener listener,
			final Throwable throwable)
	{
		failed.inc();
		if(logger.isErrorEnabled())
			logger.error(MessageFormat.format("change listener {0} {1}", event, listener), throwable);
	}
}
