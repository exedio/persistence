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

package com.exedio.dsmf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

public class GraphTest extends TestCase
{
	private Schema schema = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		schema = new Schema(new HsqldbDialect(), new ConnectionProvider(){
			public Connection getConnection() { throw new RuntimeException(); }
			public void putConnection(final Connection connection) { throw new RuntimeException(); }
			public boolean isSemicolonEnabled() { throw new RuntimeException(); }
		});
	}

	public void testOk()
	{
		final Table tabT = new Table(schema, "tabT");
		final Table tab1 = new Table(schema, "tab1");
		fk(tab1, "fk1", "tabT");
		final Table tab2 = new Table(schema, "tab2");
		fk(tab2, "fk2", "tabT");
		final Graph graph = new Graph(schema);

		assertEquals(list(tabT, tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	public void testSelf()
	{
		final Table tab = new Table(schema, "tab");
		fk(tab, "fk", "tab");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	public void testReorder()
	{
		final Table tab1 = new Table(schema, "tab1");
		fk(tab1, "fk1", "tabT");
		final Table tab2 = new Table(schema, "tab2");
		fk(tab2, "fk2", "tabT");
		final Table tabT = new Table(schema, "tabT");
		final Graph graph = new Graph(schema);

		assertEquals(list(tabT, tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	public void testBreak()
	{
		final Table tab1 = new Table(schema, "tab1");
		final Table tab2 = new Table(schema, "tab2");
		final ForeignKeyConstraint fk1 =
			fk(tab1, "fk1", "tab2");
		fk(tab2, "fk2", "tab1");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(fk1), setAsList(graph.getConstraintsBroken()));
	}

	public void testBreakWeight()
	{
		final Table tab1 = new Table(schema, "tab1");
		final Table tab2 = new Table(schema, "tab2");
		final ForeignKeyConstraint fk1a =
			fk(tab1, "fk1a", "tab2");
		final ForeignKeyConstraint fk1b =
			fk(tab1, "fk1b", "tab2");
		fk(tab2, "fk2", "tab1");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(fk1a, fk1b), setAsList(graph.getConstraintsBroken())); // TODO should be fk2
	}


	private static ForeignKeyConstraint fk(final Table table, final String name, final String targetTable)
	{
		return new ForeignKeyConstraint(table, name, name + "Col", targetTable, name + "Pk");
	}

	private static <E> List<E> setAsList(final Set<E> set)
	{
		return new ArrayList<E>(set);
	}

	private static final List<Object> list(final Object... o)
	{
		return Arrays.asList(o);
	}
}
