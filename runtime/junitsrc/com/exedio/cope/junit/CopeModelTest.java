/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.junit;

import com.exedio.cope.ChangeListener;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.misc.TimeUtil;
import com.exedio.cope.util.ModificationListener;

/**
 * An abstract test case class for tests creating/using some persistent data.
 * @author Ralf Wiebicke
 */
public abstract class CopeModelTest extends CopeAssert
{
	protected final Model model;

	protected CopeModelTest(final Model model)
	{
		this.model = model;
	}

	/**
	 * Override this method to provide your own connect properties
	 * to method {@link #setUp()} for connecting.
	 */
	protected ConnectProperties getConnectProperties()
	{
		return new ConnectProperties(ConnectProperties.getSystemPropertySource());
	}

	protected boolean doesWriteTiming()
	{
		return false;
	}

	private long nanoTime()
	{
		return doesWriteTiming() ? System.nanoTime() : 0;
	}

	private void writeTime(final String message, final long before)
	{
		if(doesWriteTiming())
			System.out.println("" + TimeUtil.toMillies(System.nanoTime(), before) + ' ' + message);
	}


	private boolean manageTransactions;

	protected boolean doesManageTransactions()
	{
		return true;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ModelConnector.connectAndCreate(model, getConnectProperties(), doesWriteTiming());

		manageTransactions = doesManageTransactions();
		if(manageTransactions)
		{
			final long beforeStartTransaction = nanoTime();
			model.startTransaction("tx:" + getClass().getName());
			writeTime("startTransaction", beforeStartTransaction);

			final long beforeCheckEmptySchema = nanoTime();
			model.checkEmptySchema();
			writeTime("checkEmptySchema", beforeCheckEmptySchema);
		}
	}

	@Override
	protected void tearDown() throws Exception
	{
		// do this even if manageTransactions is false
		// because test could have started a transaction
		final long beforeRollbackIfNotCommitted = nanoTime();
		model.rollbackIfNotCommitted();
		writeTime("rollbackIfNotCommitted", beforeRollbackIfNotCommitted);

		final long beforeDeleteSchema = nanoTime();
		model.deleteSchema();
		writeTime("deleteSchema", beforeDeleteSchema);

		model.setDatabaseListener(null);
		for(final ChangeListener cl : model.getChangeListeners())
			model.removeChangeListener(cl);
		for(final ModificationListener ml : model.getModificationListeners())
			model.removeModificationListener(ml);
		ModelConnector.dropAndDisconnect();
		super.tearDown();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated
	 * Not needed anymore, since {@link #tearDown()}
	 * deletes the contents of the database.
	 * Does not do anything.
	 */
	@Deprecated
	protected final <I extends Item> I deleteOnTearDown(final I item)
	{
		return item;
	}
}
