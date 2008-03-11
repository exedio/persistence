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

package com.exedio.dsmf;

import java.io.BufferedReader;
import java.io.FileReader;


public class BatchTest extends SchemaTest
{
	public void testTables() throws Exception
	{
		make(true, "create.sql");
		make(true, "drop.sql");
		make(false, "create.sql");
		make(false, "drop.sql");
	}
	
	public void make(final boolean single, final String filename) throws Exception
	{
		final BufferedReader ps = new BufferedReader(new FileReader("dsmf/batchtest-"+filename));
		Thread.sleep(2000);
		final long start1 = System.currentTimeMillis();
		if(single)
		{
			for(String line = ps.readLine(); line!=null; line = ps.readLine())
			{
				//System.out.println(line);
				java.sql.Statement sqlStatement = connection1.createStatement();
				sqlStatement.executeUpdate(line);
				sqlStatement.close();
			}
		}
		else
		{
			java.sql.Statement sqlStatement = connection1.createStatement();
			for(String line = ps.readLine(); line!=null; line = ps.readLine())
			{
				//System.out.println(line);
				sqlStatement.addBatch(line);
			}
			sqlStatement.executeBatch();
			sqlStatement.close();
		}
		final long end1 = System.currentTimeMillis();
		ps.close();
		System.out.println(filename+" "+(single?"single":"batch")+" : "+(end1-start1));
	}
	
}
