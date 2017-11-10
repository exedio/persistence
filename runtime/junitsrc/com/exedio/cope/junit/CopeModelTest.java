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

package com.exedio.cope.junit;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;

/**
 * An abstract test case class for tests creating/using some persistent data.
 * @author Ralf Wiebicke
 */
public abstract class CopeModelTest extends CopeAssert
{
	protected final Model model;

	protected CopeModelTest(final Model model)
	{
		this.model = requireNonNull(model, "model");
	}

	public final Model getModel()
	{
		return model;
	}

	/**
	 * Override this method to provide your own connect properties
	 * to method {@link #setUp()} for connecting.
	 */
	public ConnectProperties getConnectProperties()
	{
		return new ConnectProperties(ConnectProperties.getDefaultPropertyFile());
	}

	/**
	 * Override this method returning false if you do not want
	 * method {@link #setUp()} to create a transaction for you.
	 * The default implementation returns true.
	 */
	protected boolean doesManageTransactions()
	{
		return true;
	}

	protected String getTransactionName()
	{
		return "tx:" + getClass().getName();
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ModelConnector.connectAndCreate(model, getConnectProperties());
		model.deleteSchemaForTest(); // typically faster than checkEmptySchema

		if(doesManageTransactions())
			model.startTransaction(getTransactionName());
	}

	@Override
	protected void tearDown() throws Exception
	{
		// NOTE:
		// do rollback even if manageTransactions is false
		// because test could have started a transaction
		model.rollbackIfNotCommitted();

		if(model.isConnected())
		{
			model.getConnectProperties().mediaFingerprintOffset().reset();
			model.setDatabaseListener(null);
		}
		model.removeAllChangeListeners();
		ModelConnector.dropAndDisconnect();
		super.tearDown();
	}
}
