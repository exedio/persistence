/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import com.exedio.cope.junit.CopeAssert;

abstract class RawDatabaseTest extends CopeAssert
{
	protected Connection con;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		final ConnectProperties props =
				new ConnectProperties(ConnectProperties.SYSTEM_PROPERTY_SOURCE);

		final String url = props.getConnectionUrl();
		final Driver driver = DriverManager.getDriver(url);
		if(driver==null)
			throw new RuntimeException(url);

		con = driver.connect(url, props.newConnectionInfo());
	}

	@Override
	public void tearDown() throws Exception
	{
		if(con!=null)
			con.close();
		con = null;

		super.tearDown();
	}
}
