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

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.sql.Statement.NO_GENERATED_KEYS;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests, whether there is a significant performance penalty
 * for using auto_increment on MySQL.
 * There seems to be no such penalty.
 */
@Disabled
public class AutoIncrementEvaluator extends RawDatabaseTest
{
	private static final int ITERATIONS = 100;

	private Statement stat;

	@BeforeEach final void setUp() throws SQLException
	{
		stat = con.createStatement();
	}

	@AfterEach final void tearDown() throws SQLException
	{
		if(stat!=null)
			stat.close();
	}

	@Test void testAutoIncrement() throws SQLException
	{
		stat.execute("drop table if exists testAutoIncrement");
		stat.execute(
				"create table testAutoIncrement ( " +
					"pk integer primary key auto_increment, " +
					"payLoad varchar(80) )");
		{
			final long start = System.nanoTime();
			long executeUpdateAccu = 0;
			long getGeneratedKeysAccu = 0;
			for(long i=0; i<ITERATIONS; i++)
			{
				final long pk;
				try(PreparedStatement stmt =
						con.prepareStatement(
								"INSERT INTO testAutoIncrement (payLoad) VALUES (?)",
								RETURN_GENERATED_KEYS))
				{
					stmt.setObject(1, "payLoad + i");
					final long executeUpdateStart = System.nanoTime();
					stmt.executeUpdate();
					executeUpdateAccu += (System.nanoTime() - executeUpdateStart);
					final long getGeneratedKeysStart = System.nanoTime();
					try(ResultSet resultSet = stmt.getGeneratedKeys())
					{
						getGeneratedKeysAccu += (System.nanoTime() - getGeneratedKeysStart);
						if(!resultSet.next())
							throw new RuntimeException("empty in sequence ");
						pk = resultSet.getLong(1);
					}
				}
				assertEquals(i+1, pk);
			}
			System.out.println(
					"auto_increment " + toMillies(System.nanoTime(), start) + "ms " +
					"executeUpdate " + (executeUpdateAccu/1000000) + "ms " +
					"getGeneratedKeys " + (getGeneratedKeysAccu/1000000) + "ms");
		}
		{
			final long start = System.nanoTime();
			long executeUpdateAccu = 0;
			for(int i=0; i<ITERATIONS; i++)
			{
				try(PreparedStatement stmt =
						con.prepareStatement(
								"INSERT INTO testAutoIncrement (payLoad) VALUES (?)",
								NO_GENERATED_KEYS))
				{
					stmt.setObject(1, "payLoad + i");
					final long executeUpdateStart = System.nanoTime();
					stmt.executeUpdate();
					executeUpdateAccu += (System.nanoTime() - executeUpdateStart);
				}
			}
			System.out.println(
					"auto_increment empty " + toMillies(System.nanoTime(), start) + "ms " +
					"executeUpdate " + (executeUpdateAccu/1000000) + "ms");
		}
		stat.execute("drop table testAutoIncrement");
		stat.execute(
				"create table testAutoIncrement ( " +
					"pk integer primary key, " +
					"payLoad varchar(80) )");
		{
			final long start = System.nanoTime();
			long executeUpdateAccu = 0;
			for(int i=0; i<ITERATIONS; i++)
			{
				try(PreparedStatement stmt =
						con.prepareStatement(
								"INSERT INTO testAutoIncrement (pk,payLoad) VALUES (?,?)",
								NO_GENERATED_KEYS))
				{
					stmt.setObject(1, i);
					stmt.setObject(2, "payLoad + i");
					final long executeUpdateStart = System.nanoTime();
					stmt.executeUpdate();
					executeUpdateAccu += (System.nanoTime() - executeUpdateStart);
				}
			}
			System.out.println(
					"normal " + toMillies(System.nanoTime(), start) + "ms " +
					"executeUpdate " + (executeUpdateAccu/1000000) + "ms");
		}
		stat.execute("drop table testAutoIncrement");
	}
}
