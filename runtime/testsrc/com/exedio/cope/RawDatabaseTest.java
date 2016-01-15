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

package com.exedio.cope;

import com.exedio.cope.util.Properties;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;

public abstract class RawDatabaseTest
{
	protected Connection con;

	@Before public final void setUpRawDatabaseTest() throws SQLException
	{
		final ConnectProperties props =
				new ConnectProperties(Properties.SYSTEM_PROPERTY_SOURCE);

		final String url = props.getConnectionUrl();
		final Driver driver = DriverManager.getDriver(url);
		if(driver==null)
			throw new RuntimeException(url);

		con = driver.connect(url, props.newConnectionInfo());
	}

	@After public final void tearDownRawDatabaseTest() throws Exception
	{
		if(con!=null)
			con.close();
	}
}
