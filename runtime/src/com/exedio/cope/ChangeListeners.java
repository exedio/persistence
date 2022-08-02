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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChangeListeners
{
	private static final Logger logger = LoggerFactory.getLogger(ChangeListeners.class);

	private volatile boolean used = false;
	private final LinkedList<WeakReference<ChangeListener>> list = new LinkedList<>();
	private Counter cleared = new NoNameCounter();
	private Counter removed = new NoNameCounter();
	private Timer success = new NoNameTimer();
	private Timer failed  = new NoNameTimer();

	void onModelNameSet(final Tags tags)
	{
		final MetricsBuilder metrics =
				new MetricsBuilder(ChangeListener.class, tags);
		cleared = metrics.counter("remove", "Number of ChangeListeners that were removed because they were eligible for garbage collection.", Tags.of("cause", "reference"));
		removed = metrics.counter("remove", "Number of ChangeListeners removed via Model#removeChangeListener or #removeAllChangeListeners.", Tags.of("cause", "remove"));
		success = metrics.timer("dispatch", "How often calls to ChangeListener#onChange did succeed.", Tags.of("result", "success"));
		failed  = metrics.timer("dispatch", "How often calls to ChangeListener#onChange did fail.",    Tags.of("result", "failure"));
		metrics.gauge(list,
				// BEWARE:
				// Must not use Collections#synchronizedCollection because
				// it uses the synchronized wrapper as mutex, but the code
				// in this class uses "open" itself as mutex.
				l -> {
					//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: parameter is actually field "list"
					synchronized(l) { return l.size(); } },
				"size", "Number of ChangeListeners registered in Model.");
	}

	List<ChangeListener> get()
	{
		final ArrayList<ChangeListener> result;

		int cleared = 0;
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
		this.cleared.increment(cleared);

		return Collections.unmodifiableList(result);
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
		return new ChangeListenerInfo(size, cleared, removed, failed);
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

		int cleared = 0;
		int removed = 0;
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
		this.cleared.increment(cleared);
		this.removed.increment(removed);
	}

	void removeAll()
	{
		final int removed;
		synchronized(list)
		{
			removed = list.size();
			list.clear();
		}
		this.removed.increment(removed);
	}

	void dispatch(final ChangeEvent event, final ChangeListenerDispatcher interrupter)
	{
		final List<ChangeListener> listeners = get();

		for(final ChangeListener listener : listeners)
		{
			if(interrupter.interrupts())
				return;

			final Timer.Sample start = Timer.start();
			try
			{
				listener.onChange(event);
				start.stop(success);
			}
			catch(final Exception | AssertionError e)
			{
				final long elapsed = start.stop(failed);
				if(logger.isErrorEnabled())
					//noinspection StringConcatenationArgumentToLogCall
					logger.error("change listener " + event + ' ' + listener + ' ' + elapsed + "ns", e);
			}
		}
	}
}
