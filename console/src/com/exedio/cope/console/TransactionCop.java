/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.util.ModificationListener;

final class TransactionCop extends ConsoleCop
{
	static final String ENABLE  = "txlistenerenable";
	static final String DISABLE = "txlistenerdisable";
	static final String CLEAR   = "txlistenerclear";

	TransactionCop(final Args args)
	{
		super(TAB_TRANSACTION, "transactions", args);
	}

	@Override
	protected TransactionCop newArgs(final Args args)
	{
		return new TransactionCop(args);
	}
	
	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		
		if(isPost(request))
		{
			if(request.getParameter(ENABLE)!=null && !model.getModificationListeners().contains(listener))
			{
				model.addModificationListener(listener);
			}
			else if(request.getParameter(DISABLE)!=null)
			{
				model.removeModificationListener(listener);
			}
			else if(request.getParameter(CLEAR)!=null)
			{
				synchronized(commits)
				{
					commits.clear();
				}
			}
		}
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		Transaction_Jspm.writeHead(out);
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		final Transaction[] openTransactions = model.getOpenTransactions().toArray(new Transaction[]{});
		Arrays.sort(openTransactions, new Comparator<Transaction>(){

			public int compare(final Transaction tx1, final Transaction tx2)
			{
				final long id1 = tx1.getID();
				final long id2 = tx2.getID();
				return id1<id2 ? -1 : id1>id2 ? 1 : 0;
			}
			
		});

		final Thread[] threads = new Thread[openTransactions.length];
		final long[] threadIds = new long[openTransactions.length];
		final String[] threadNames = new String[openTransactions.length];
		final int[] threadPriorities = new int[openTransactions.length];
		final Thread.State[] threadStates = new Thread.State[openTransactions.length];
		final StackTraceElement[][] stacktraces = new StackTraceElement[openTransactions.length][];
		for(int i = 0; i<openTransactions.length; i++)
		{
			final Thread thread = openTransactions[i].getBoundThread();
			if(thread!=null)
			{
				threads[i] = thread;
				threadIds[i] = thread.getId();
				threadNames[i] = thread.getName();
				threadPriorities[i] = thread.getPriority();
				threadStates[i] = thread.getState();
				stacktraces[i] = thread.getStackTrace();
			}
		}
		
		final Commit[] commits;
		synchronized(this.commits)
		{
			commits = this.commits.toArray(new Commit[this.commits.size()]);
		}

		Transaction_Jspm.writeBody(
				out, this,
				model.getNextTransactionId(),
				model.getLastTransactionStartDate(),
				model.getTransactionCounters(),
				openTransactions,
				threads, threadIds, threadNames, threadPriorities, threadStates, stacktraces,
				model.getModificationListeners().contains(listener),
				commits);
	}
	
	static final ArrayList<Commit> commits = new ArrayList<Commit>();
	
	private static final ModificationListener listener = new ModificationListener()
	{
		public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			final Commit commit = new Commit(modifiedItems, transaction);
			synchronized(commits)
			{
				commits.add(commit);
			}
		}
		
		@Override
		public String toString()
		{
			return "Console ModificationListener for Committed Transactions";
		}
	};
}
