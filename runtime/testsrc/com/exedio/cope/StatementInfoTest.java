/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.Iterator;

import com.exedio.cope.testmodel.ItemWithSingleUnique;


public class StatementInfoTest extends TestmodelTest
{
	
	public void testExecutionPlan()
	{
		final Query query = ItemWithSingleUnique.TYPE.newQuery(ItemWithSingleUnique.uniqueString.equal("zack"));
		query.enableMakeStatementInfo();
		query.search();
		final StatementInfo root = query.getStatementInfo();
		assertUnmodifiable(root.getChilds());
		//root.print(System.out);
		
		final String firstStatementText = root.getText();
		assertTrue(firstStatementText, firstStatementText.startsWith("select "));
		
		final String database = model.getDatabase().getClass().getName();
		final String timePrefix = "timing ";
		if(database.endsWith("HsqldbDatabase"))
		{
			final Iterator<StatementInfo> rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
			}
			assertTrue(!rootChilds.hasNext());
		}
		else if(database.endsWith("MysqlDatabase"))
		{
			final Iterator<StatementInfo> rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
			}
			{
				final StatementInfo plan = rootChilds.next();
				assertEquals("explain plan", plan.getText());
			}
			assertTrue(!rootChilds.hasNext());
		}
		else if(database.endsWith("OracleDatabase"))
		{
			final Iterator<StatementInfo> rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
				final StatementInfo planId = rootChilds.next();
				assertTrue(planId.getText(), planId.getText().startsWith("explain plan statement_id=cope"));
				{
					final Iterator<StatementInfo> planIdChilds = planId.getChilds().iterator();
					{
						final StatementInfo planSelect = planIdChilds.next();
						assertTrue(planSelect.getText(), planSelect.getText().startsWith("SELECT STATEMENT optimizer="));
						{
							final Iterator<StatementInfo> planSelectChilds = planSelect.getChilds().iterator();
							{
								final StatementInfo planTableAccess = planSelectChilds.next();
								assertTrue(planTableAccess.getText(), planTableAccess.getText().startsWith("TABLE ACCESS (BY INDEX ROWID) on UNIQUE_ITEMS[1]"));
								{
									final Iterator<StatementInfo> planTableAccessChilds = planTableAccess.getChilds().iterator();
									{
										final StatementInfo planUnique = planTableAccessChilds.next();
										assertTrue(planUnique.getText(), planUnique.getText().startsWith("INDEX (UNIQUE SCAN) on IX_ITEMWSU_US"));
										assertEquals(list(), planUnique.getChilds());
									}
									assertTrue(!planTableAccessChilds.hasNext());
								}
							}
							assertTrue(!planSelectChilds.hasNext());
						}
					}
					assertTrue(!planIdChilds.hasNext());
				}
			}
			assertTrue(!rootChilds.hasNext());
		}
		else
			fail(database);
		

		// test multiple queries
		query.setOrderBy(ItemWithSingleUnique.uniqueString, true);
		query.search();
		final StatementInfo rootOrdered = query.getStatementInfo();
		//rootOrdered.print(System.out);
		assertEquals("--- multiple statements ---", rootOrdered.getText());
		final Iterator<StatementInfo> rootOrderedIterator = rootOrdered.getChilds().iterator();
		final StatementInfo ordered1 = rootOrderedIterator.next();
		assertEquals(firstStatementText, ordered1.getText());
		final StatementInfo ordered2 = rootOrderedIterator.next();
		assertTrue(!firstStatementText.equals(ordered2.getText()));
		assertTrue(ordered2.getText(), ordered2.getText().startsWith("select "));
		assertTrue(!rootOrderedIterator.hasNext());
		
		query.clearStatementInfo();
		assertNull(query.getStatementInfo());
		
		query.search();
		final StatementInfo cached1 = query.getStatementInfo();
		if(model.getProperties().getCacheQueryLimit()>0)
		{
			assertEquals("from query cache, hit #1", cached1.getText());
			assertEqualsUnmodifiable(list(), cached1.getChilds());
		}
		else
		{
			assertTrue(cached1.getText(), cached1.getText().startsWith("select "));
		}

		query.clearStatementInfo();
		assertNull(query.getStatementInfo());
		
		query.search();
		final StatementInfo cached2 = query.getStatementInfo();
		if(model.getProperties().getCacheQueryLimit()>0)
		{
			assertEquals("from query cache, hit #2", cached2.getText());
			assertEqualsUnmodifiable(list(), cached2.getChilds());
		}
		else
		{
			assertTrue(cached1.getText(), cached1.getText().startsWith("select "));
		}
	}
	
}
