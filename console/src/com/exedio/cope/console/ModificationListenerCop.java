/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.util.ModificationListener;

final class ModificationListenerCop extends ConsoleCop
{
	static final String ADD_INTROSPECTOR = "addIntrospector";
	static final String REMOVE_SELECTED = "removeSelected";
	static final String REMOVE_CHECKBOX = "rm";

	ModificationListenerCop()
	{
		super(TAB_MODIFICATION_LISTENER, "mods");
	}
	
	//static int debugNumber = 0;

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
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
			if(request.getParameter(ADD_INTROSPECTOR)!=null)
			{
				model.addModificationListener(new Introspector(request.getSession()));
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
	
	static final class Introspector implements ModificationListener
	{
		private static final String SESSION_KEY = "com.exedio.cope.console.introspector";
		private final long start = System.currentTimeMillis();
		private final HttpSession session;
		private final ArrayList<Commit> commits = new ArrayList<Commit>();
		
		Introspector(final HttpSession session)
		{
			this.session = session;
			session.setAttribute(SESSION_KEY, this);
		}
		
		public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			final Commit commit = new Commit(modifiedItems, transaction);
			synchronized(commits)
			{
				commits.add(commit);
			}
		}
		
		Date getStart()
		{
			return new Date(start);
		}
		
		String getSessionId()
		{
			return session.getId();
		}
		
		boolean isSessionAttached()
		{
			try
			{
				return session.getAttribute(SESSION_KEY)==this;
			}
			catch(RuntimeException e)
			{
				return false;
			}
		}
		
		ArrayList<Commit> getCommits()
		{
			synchronized(commits)
			{
				return new ArrayList<Commit>(commits); // prevent ConcurrentModificationException
			}
		}
	}
	
	static final class Commit
	{
		private final long timestamp = System.currentTimeMillis();
		private final String modifiedItems;
		final long transactionId;
		private final String transactionName;
		final long elapsedTime;
		
		Commit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			final StringBuffer bf = new StringBuffer();
			boolean first = true;
			for(final Item item : modifiedItems)
			{
				if(first)
					first = false;
				else
					bf.append(',').append(' ');
				
				bf.append(item.getCopeID());
			}
			this.modifiedItems = bf.toString();
			this.transactionId = transaction.getID();
			this.transactionName = transaction.getName();
			this.elapsedTime = timestamp - transaction.getStartDate().getTime();
		}
		
		Date getTimeStamp()
		{
			return new Date(timestamp);
		}
		
		String getModifiedItems()
		{
			return modifiedItems;
		}
		
		String getTransactionName()
		{
			return transactionName;
		}
	}
}
