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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.exedio.cope.util.ModificationListener;

final class ModificationListeners
{
	private final LinkedList<WeakReference<ModificationListener>> list = new LinkedList<WeakReference<ModificationListener>>();
	private int cleared = 0;
	
	List<ModificationListener> get()
	{
		synchronized(list)
		{
			final int size = list.size();
			if(size==0)
				return Collections.<ModificationListener>emptyList();
			
			// make a copy to avoid ConcurrentModificationViolations
			final ArrayList<ModificationListener> result = new ArrayList<ModificationListener>(size);
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
		
		final WeakReference<ModificationListener> ref = new WeakReference<ModificationListener>(listener);
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
}
