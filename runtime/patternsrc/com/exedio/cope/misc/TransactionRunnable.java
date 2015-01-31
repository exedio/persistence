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

package com.exedio.cope.misc;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import com.exedio.cope.TransactionTry;

public class TransactionRunnable implements Runnable
{
	private final Model model;
	private final Runnable runnable;
	private final String name;

	public TransactionRunnable(
			final Model model,
			final Runnable runnable)
	{
		this(model, runnable, null);
	}

	public TransactionRunnable(
			final Model model,
			final Runnable runnable,
			final String name)
	{
		this.model = requireNonNull(model, "model");
		this.runnable = requireNonNull(runnable, "runnable");
		this.name = name;
	}

	public void run()
	{
		try(TransactionTry tx = model.startTransactionTry(name))
		{
			runnable.run();
			tx.commit();
		}
	}
}
