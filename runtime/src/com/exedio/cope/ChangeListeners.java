/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.exedio.cope.util.ChangeListener;

final class ChangeListeners
{
	private final Types types;
	private final LinkedList<WeakReference<ChangeListener>> list = new LinkedList<WeakReference<ChangeListener>>();
	private int cleared = 0;

	ChangeListeners(final Types types)
	{
		this.types = types;
	}

	List<ChangeListener> get()
	{
		synchronized(list)
		{
			final int size = list.size();
			if(size==0)
				return Collections.<ChangeListener>emptyList();

			// make a copy to avoid ConcurrentModificationViolations
			final ArrayList<ChangeListener> result = new ArrayList<ChangeListener>(size);
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

	void add(final ChangeListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		final WeakReference<ChangeListener> ref = new WeakReference<ChangeListener>(listener);
		synchronized(list)
		{
			list.add(ref);
		}
	}

	void remove(final ChangeListener listener)
	{
		if(listener==null)
			throw new NullPointerException("listener");

		synchronized(list)
		{
			int cleared = 0;
			for(final Iterator<WeakReference<ChangeListener>> i = list.iterator(); i.hasNext(); )
			{
				final ChangeListener l = i.next().get();
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

	void invalidate(final TIntHashSet[] invalidations, final TransactionInfo transactionInfo)
	{
		final List<ChangeListener> commitListeners = get();
		if(!commitListeners.isEmpty())
		{
			final ArrayList<Item> modifiedItems = types.activate(invalidations);
			if(modifiedItems!=null && !modifiedItems.isEmpty())
			{
				final List<Item> modifiedItemsUnmodifiable = Collections.unmodifiableList(modifiedItems);
				final ChangeEvent event = new ChangeEvent(modifiedItemsUnmodifiable, transactionInfo);
				for(final ChangeListener listener : commitListeners)
				{
					try
					{
						listener.onChange(event);
					}
					catch(final RuntimeException e)
					{
						if(Model.isLoggingEnabled())
							System.err.println(
									"Suppressing exception from modification listener " + listener.getClass().getName() +
									':' + e.getClass().getName() + ' ' + e.getMessage());
					}
				}
			}
		}
	}
}
