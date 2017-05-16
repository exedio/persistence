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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeModelTest;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public abstract class CopeRule implements TestRule
{
	@SuppressWarnings("UnconstructableJUnitTestCase") // OK: is not constructed by junit
	private static final class Adaptee extends CopeModelTest
	{
		Adaptee(final Model model, final CopeRule adapter)
		{
			super(model);
			this.adapter = adapter;
		}

		private final CopeRule adapter;


		private Description description = null;
		private String transactionName = null;

		void setDescription(final Description description, final String transactionName)
		{
			Assert.assertNotNull(description);
			Assert.assertNull(this.description);
			this.description = description;
			this.transactionName = transactionName;
		}

		/**
		 * Just to make them visible to the adapter.
		 */
		@Override
		protected void setUp() throws Exception
		{
			super.setUp();
		}

		/**
		 * Just to make them visible to the adapter.
		 */
		@Override
		protected void tearDown() throws Exception
		{
			super.tearDown();
		}

		@Override
		public ConnectProperties getConnectProperties()
		{
			return adapter.getConnectProperties();
		}

		private boolean doesManageTransactions = true;

		void omitTransaction()
		{
			doesManageTransactions = false;
		}

		@Override
		protected boolean doesManageTransactions()
		{
			return doesManageTransactions;
		}

		@Override
		protected String getTransactionName()
		{
			return
				transactionName!=null
				? transactionName
				: "tx:" + description.getTestClass().getName();
		}
	}

	private final Adaptee test;

	protected CopeRule(final Model model)
	{
		//noinspection ThisEscapedInObjectConstruction
		this.test = new Adaptee(model, this);
	}

	/**
	 * Implement this method to provide your connect properties
	 * for connecting before tests.
	 * TODO make protected
	 */
	public abstract ConnectProperties getConnectProperties();

	public final void omitTransaction()
	{
		test.omitTransaction();
	}

	private String transactionName;

	public final void setTransactionName(final String transactionName)
	{
		this.transactionName = transactionName;
	}


	@Override
	public final Statement apply(final Statement base, final Description description)
	{
		final Adaptee test = this.test; // avoid synthetic-access warning
		final String transactionName = this.transactionName; // avoid synthetic-access warning
		return new Statement()
		{
			@Override
			public void evaluate() throws Throwable
			{
				test.setDescription(description, transactionName);
				test.setUp();
				try
				{
					base.evaluate();
				}
				finally
				{
					test.tearDown();
				}
			}
		};
	}
}
