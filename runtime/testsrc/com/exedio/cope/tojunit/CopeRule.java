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
import org.junit.rules.ExternalResource;

public abstract class CopeRule extends ExternalResource
{
	private static final class Adaptee extends CopeModelTest
	{
		Adaptee(final Model model, final CopeRule adapter)
		{
			super(model);
			this.adapter = adapter;
		}

		private final CopeRule adapter;

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
	}

	private final Adaptee test;

	public CopeRule(final Model model)
	{
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

	@Override
	protected final void before() throws Exception
	{
		test.setUp();
	}

	@Override
	protected final void after()
	{
		try
		{
			test.tearDown();
		}
		catch(final Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
