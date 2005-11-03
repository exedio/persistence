/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
		final Query query = new Query(ItemWithSingleUnique.TYPE, ItemWithSingleUnique.uniqueString.equal("zack"));
		query.enableMakeStatementInfo();
		query.search();
		final StatementInfo root = query.getStatementInfo();
		assertUnmodifiable(root.getChilds());
		//root.print(System.out);
		
		final String firstStatementText = root.getText();
		assertTrue(firstStatementText, firstStatementText.startsWith("select "));
		
		final String database = model.getDatabase().getClass().getName();
		final String timePrefix = "timing ";
		if(database.indexOf("HsqldbDatabase")>=0)
		{
			final Iterator rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = (StatementInfo)rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
			}
			assertTrue(!rootChilds.hasNext());
		}
		else if(database.indexOf("MysqlDatabase")>=0)
		{
			final Iterator rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = (StatementInfo)rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
			}
			{
				final StatementInfo plan = (StatementInfo)rootChilds.next();
				assertEquals("explain plan", plan.getText());
			}
			assertTrue(!rootChilds.hasNext());
		}
		else if(database.indexOf("OracleDatabase")>=0)
		{
			final Iterator rootChilds = root.getChilds().iterator();
			{
				final StatementInfo time = (StatementInfo)rootChilds.next();
				assertTrue(time.getText(), time.getText().startsWith(timePrefix));
				final StatementInfo planId = (StatementInfo)rootChilds.next();
				assertTrue(planId.getText(), planId.getText().startsWith("explain plan statement_id=cope"));
				{
					final Iterator planIdChilds = planId.getChilds().iterator();
					{
						final StatementInfo planSelect = (StatementInfo)planIdChilds.next();
						assertEquals("SELECT STATEMENT optimizer=CHOOSE", planSelect.getText());
						{
							final Iterator planSelectChilds = planSelect.getChilds().iterator();
							{
								final StatementInfo planTableAccess = (StatementInfo)planSelectChilds.next();
								assertEquals("TABLE ACCESS (BY INDEX ROWID) on UNIQUE_ITEMS[1]", planTableAccess.getText());
								{
									final Iterator planTableAccessChilds = planTableAccess.getChilds().iterator();
									{
										final StatementInfo planUnique = (StatementInfo)planTableAccessChilds.next();
										assertEquals("INDEX (UNIQUE SCAN) on IX_ITEMWSU_US[UNIQUE] search_columns=1", planUnique.getText());
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
		final Iterator rootOrderedIterator = rootOrdered.getChilds().iterator();
		final StatementInfo ordered1 = (StatementInfo)rootOrderedIterator.next();
		assertEquals(firstStatementText, ordered1.getText());
		final StatementInfo ordered2 = (StatementInfo)rootOrderedIterator.next();
		assertTrue(!firstStatementText.equals(ordered2.getText()));
		assertTrue(ordered2.getText(), ordered2.getText().startsWith("select "));
		assertTrue(!rootOrderedIterator.hasNext());
		
		query.clearStatementInfo();
		assertNull(query.getStatementInfo());
	}
	
}
