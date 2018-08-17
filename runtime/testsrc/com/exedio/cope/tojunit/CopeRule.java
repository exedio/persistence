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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.misc.ConnectToken;
import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class CopeRule extends MainRule
{
	private final Model model;

	protected CopeRule(final Model model)
	{
		this.model = requireNonNull(model, "model");
	}

	/**
	 * Implement this method to provide your connect properties
	 * for connecting before tests.
	 * TODO make protected
	 */
	public abstract ConnectProperties getConnectProperties();

	private boolean doesManageTransactions = true;

	public final void omitTransaction()
	{
		doesManageTransactions = false;
	}


	@Override
	protected final void before(final ExtensionContext context)
	{
		assertEquals(null, ConnectToken.getProperties(model));
		ModelConnector.connectAndCreate(model, getConnectProperties());
		model.deleteSchemaForTest(); // typically faster than checkEmptySchema

		if(doesManageTransactions)
			model.startTransaction("tx:" + context.getTestClass().get().getName());
	}

	@Override
	protected final void after()
	{
		// NOTE:
		// do rollback even if manageTransactions is false
		// because test could have started a transaction
		model.rollbackIfNotCommitted();

		// test may have started multiple transactions with Model#leaveTransaction
		for(final Transaction tx : model.getOpenTransactions())
		{
			model.joinTransaction(tx);
			model.rollbackIfNotCommitted();
		}

		if(model.isConnected())
		{
			model.getConnectProperties().mediaFingerprintOffset().reset();
			model.setDatabaseListener(null);
		}
		model.removeAllChangeListeners();
		ModelConnector.dropAndDisconnect();
	}
}
