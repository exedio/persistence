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

import com.exedio.cope.util.ModificationListener;
import gnu.trove.TIntHashSet;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ModificationListeners
{
	private static final Logger logger = LoggerFactory.getLogger(ModificationListeners.class);

	private final Types types;
	private final LinkedList<WeakReference<ModificationListener>> list = new LinkedList<>();
	private int cleared = 0;

	ModificationListeners(final Types types)
	{
		this.types = types;
	}

	List<ModificationListener> get()
	{
		synchronized(list)
		{
			final int size = list.size();
			if(size==0)
				return Collections.<ModificationListener>emptyList();

			// make a copy to avoid ConcurrentModificationViolations
			final ArrayList<ModificationListener> result = new ArrayList<>(size);
			int cleared = 0;
			for(final Iterator<WeakReference<ModificationListener>> i = list.iterator(); i.hasNext(); )
			{
				final ModificationListener listener = i.next().get();
				if(listener==null)
				{
					i.remove();
					cleared++;
				}
				else
					result.add(listener);
			}

			if(cleared>0)
				this.cleared += cleared;

			return Collections.unmodifiableList(result);
		}
	}

	int getCleared()
	{
		synchronized(list)
		{
			return cleared;
		}
	}

	void add(final ModificationListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		final WeakReference<ModificationListener> ref = new WeakReference<>(listener);
		synchronized(list)
		{
			list.add(ref);
		}
	}

	void remove(final ModificationListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		synchronized(list)
		{
			int cleared = 0;
			for(final Iterator<WeakReference<ModificationListener>> i = list.iterator(); i.hasNext(); )
			{
				final ModificationListener l = i.next().get();
				if(l==null)
				{
					i.remove();
					cleared++;
				}
				else if(l==listener)
					i.remove();
			}
			if(cleared>0)
				this.cleared += cleared;
		}
	}

	void invalidate(final TIntHashSet[] invalidations, final Transaction transaction)
	{
		final List<ModificationListener> listeners = get();
		if(listeners.isEmpty())
			return;

		final Collection<Item> items = Collections.unmodifiableCollection(Arrays.asList(types.activate(invalidations)));
		assert !items.isEmpty();
		for(final ModificationListener listener : listeners)
		{
			try
			{
				onModifyingCommit(listener, items, transaction);
			}
			catch(final RuntimeException e)
			{
				if(logger.isErrorEnabled())
					logger.error("Suppressing exception from modification listener " + listener.getClass().getName(), e);
			}
			catch(final AssertionError e)
			{
				if(logger.isErrorEnabled())
					logger.error( "Suppressing exception from modification listener " + listener.getClass().getName(), e);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void onModifyingCommit(final ModificationListener listener, final Collection<Item> modifiedItems, final Transaction transaction)
	{
		listener.onModifyingCommit(modifiedItems, transaction);
	}
}
