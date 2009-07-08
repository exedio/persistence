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

package com.exedio.cope.console;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.util.ModificationListener;

final class ModificationListenerCop extends ConsoleCop
{
	static final String REMOVE_SELECTED = "removeSelected";
	static final String REMOVE_CHECKBOX = "rm";

	ModificationListenerCop(final Args args)
	{
		super(TAB_MODIFICATION_LISTENER, "mods", args);
	}

	@Override
	protected ModificationListenerCop newArgs(final Args args)
	{
		return new ModificationListenerCop(args);
	}
	
	//static int debugNumber = 0;

	@Override
	final void writeBody(
			final Out out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		if(isPost(request))
		{
			if(request.getParameter(REMOVE_SELECTED)!=null)
			{
				final String[] toDeleteArray = request.getParameterValues(REMOVE_CHECKBOX);
				
				if(toDeleteArray!=null)
				{
					final HashSet<String> toDelete = new HashSet<String>(Arrays.asList(toDeleteArray));
					for(final ModificationListener listener : model.getModificationListeners())
					{
						if(toDelete.contains(toID(listener)))
							model.removeModificationListener(listener);
					}
				}
			}
		}
		
		ModificationListener_Jspm.writeBody(this, out,
				model.getModificationListenersCleared(),
				model.getModificationListeners(),
				model.getModificationListenersCleared());

		/*model.addModificationListener(new ModificationListener()
		{
			private int count = debugNumber++;
			
			public void onModifyingCommit(final Collection<Item> modifiedItems)
			{
				// do nothing
			}
			
			@Override
			public String toString()
			{
				return "toString of ModificationListener " + count;
			}
		});
		model.addModificationListener(new ModificationListener()
		{
			private int count = debugNumber++;

			public void onModifyingCommit(final Collection<Item> modifiedItems)
			{
				// do nothing
			}
			
			@Override
			public String toString()
			{
				throw new RuntimeException("Exception in toString of ModificationListener " + count);
			}
		});*/
	}
	
	final String toID(final ModificationListener listener)
	{
		return listener.getClass().getName() + '@' + System.identityHashCode(listener);
	}
}
