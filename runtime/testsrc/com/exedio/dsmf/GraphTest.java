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

package com.exedio.dsmf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.AssertionFailedSchemaDialect;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphTest
{
	private Schema schema = null;

	@BeforeEach final void setUp()
	{
		schema = new Schema(new AssertionFailedSchemaDialect(), new ConnectionProvider(){
			@Override
			public Connection getConnection() { throw new RuntimeException(); }
			@Override
			public void putConnection(final Connection connection) { throw new RuntimeException(); }
			@Override
			public boolean isSemicolonEnabled() { throw new RuntimeException(); }
		});
	}

	@Test void testOk()
	{
		final Table tabT = schema.newTable("tabT");
		final Table tab1 = schema.newTable("tab1");
		final Column col1 = tab1.newColumn("col1", "type1");
		fk(col1, "fk1", "tabT");
		final Table tab2 = schema.newTable("tab2");
		final Column col2 = tab2.newColumn("col2", "type2");
		fk(col2, "fk2", "tabT");
		final Graph graph = new Graph(schema);

		assertEquals(list(tabT, tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	@Test void testSelf()
	{
		final Table tab = schema.newTable("tab");
		final Column col = tab.newColumn("col", "type");
		fk(col, "fk", "tab");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	@Test void testReorder()
	{
		final Table tab1 = schema.newTable("tab1");
		final Column col1 = tab1.newColumn("col1", "type1");
		fk(col1, "fk1", "tabT");
		final Table tab2 = schema.newTable("tab2");
		final Column col2 = tab2.newColumn("col2", "type2");
		fk(col2, "fk2", "tabT");
		final Table tabT = schema.newTable("tabT");
		final Graph graph = new Graph(schema);

		assertEquals(list(tabT, tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(), setAsList(graph.getConstraintsBroken()));
	}

	@Test void testBreak()
	{
		final Table tab1 = schema.newTable("tab1");
		final Table tab2 = schema.newTable("tab2");
		final Column col1 = tab1.newColumn("col1", "type1");
		final Column col2 = tab2.newColumn("col2", "type2");
		final ForeignKeyConstraint fk1 =
			fk(col1, "fk1", "tab2");
		fk(col2, "fk2", "tab1");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(fk1), setAsList(graph.getConstraintsBroken()));
	}

	@Test void testBreakWeight()
	{
		final Table tab1 = schema.newTable("tab1");
		final Table tab2 = schema.newTable("tab2");
		final Column col1 = tab1.newColumn("col1", "type1");
		final Column col2 = tab2.newColumn("col2", "type2");
		final ForeignKeyConstraint fk1a =
			fk(col1, "fk1a", "tab2");
		final ForeignKeyConstraint fk1b =
			fk(col1, "fk1b", "tab2");
		fk(col2, "fk2", "tab1");
		final Graph graph = new Graph(schema);

		assertEquals(list(tab1, tab2), graph.getTablesOrdered());
		assertEquals(list(fk1a, fk1b), setAsList(graph.getConstraintsBroken())); // TODO should be fk2
	}


	private static ForeignKeyConstraint fk(final Column column, final String name, final String targetTable)
	{
		return column.newForeignKey(name, targetTable, name + "Pk");
	}

	private static <E> List<E> setAsList(final Set<E> set)
	{
		return new ArrayList<>(set);
	}

	private static List<Object> list(final Object... o)
	{
		return Arrays.asList(o);
	}
}
