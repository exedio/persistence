/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.List;

import com.exedio.cope.testmodel.ItemWithSingleUnique;


public class QueryInfoTest extends TestmodelTest
{
	
	public void testExecutionPlan()
	{
		final Transaction transaction = model.getCurrentTransaction();
		final Query query = ItemWithSingleUnique.TYPE.newQuery(ItemWithSingleUnique.uniqueString.equal("zack"));
		transaction.setQueryInfoEnabled(true);
		query.search();
		final List<QueryInfo> infos = transaction.getQueryInfos();
		assertUnmodifiable(infos);
		assertEquals(1, infos.size());
		final QueryInfo root = infos.iterator().next();
		assertUnmodifiable(root.getChilds());
		//root.print(System.out);
		
		final String firstStatementText = root.getText();
		assertTrue(firstStatementText, firstStatementText.startsWith("select "));
		
		final Iterator<QueryInfo> rootChilds = root.getChilds().iterator();
		{
			final QueryInfo timing = rootChilds.next();
			assertTrue(timing.getText(), timing.getText().startsWith("timing "));
			assertContains(timing.getChilds());
		}
		if(!model.getProperties().getDatabaseDontSupportPreparedStatements())
		{
			final QueryInfo parameters = rootChilds.next();
			assertEquals("parameters", parameters.getText());
			final Iterator<QueryInfo> parametersChilds = parameters.getChilds().iterator();
			{
				final QueryInfo parameter = parametersChilds.next();
				assertEquals("1:zack", parameter.getText());
				assertContains(parameter.getChilds());
			}
			assertTrue(!parametersChilds.hasNext());
		}

		switch(dialect)
		{
			case HSQLDB:
				break;
			case MYSQL:
			{
				final QueryInfo plan = rootChilds.next();
				assertEquals("explain plan", plan.getText());
				break;
			}
			case ORACLE:
			{
				final QueryInfo planId = rootChilds.next();
				assertTrue(planId.getText(), planId.getText().startsWith("explain plan statement_id=cope"));
				{
					final Iterator<QueryInfo> planIdChilds = planId.getChilds().iterator();
					{
						final QueryInfo planSelect = planIdChilds.next();
						assertTrue(planSelect.getText(), planSelect.getText().startsWith("SELECT STATEMENT optimizer="));
						{
							final Iterator<QueryInfo> planSelectChilds = planSelect.getChilds().iterator();
							{
								final QueryInfo planTableAccess = planSelectChilds.next();
								assertTrue(planTableAccess.getText(), planTableAccess.getText().startsWith("TABLE ACCESS (BY INDEX ROWID) on UNIQUE_ITEMS[1]"));
								{
									final Iterator<QueryInfo> planTableAccessChilds = planTableAccess.getChilds().iterator();
									{
										final QueryInfo planUnique = planTableAccessChilds.next();
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
				break;
			}
			case POSTGRESQL:
				break;
			default:
				fail(dialect.toString());
		}
		
		assertTrue(!rootChilds.hasNext());

		// test multiple queries
		query.setOrderBy(ItemWithSingleUnique.uniqueString, true);
		query.search();
		final List<QueryInfo> rootOrdered = transaction.getQueryInfos();
		//rootOrdered.print(System.out);
		assertUnmodifiable(rootOrdered);
		final Iterator<QueryInfo> rootOrderedIterator = rootOrdered.iterator();
		final QueryInfo ordered1 = rootOrderedIterator.next();
		assertEquals(firstStatementText, ordered1.getText());
		final QueryInfo ordered2 = rootOrderedIterator.next();
		assertTrue(!firstStatementText.equals(ordered2.getText()));
		assertTrue(ordered2.getText(), ordered2.getText().startsWith("select "));
		assertTrue(!rootOrderedIterator.hasNext());
		
		transaction.setQueryInfoEnabled(false);
		assertNull(transaction.getQueryInfos());
		
		final String statement =
			"select ItemWithSingleUnique.this " +
			"from ItemWithSingleUnique " +
			"where ItemWithSingleUnique.uniqueString='zack' " +
			"order by ItemWithSingleUnique.uniqueString";
		
		transaction.setQueryInfoEnabled(true);
		query.search();
		final List<QueryInfo> cached1Infos = transaction.getQueryInfos();
		assertUnmodifiable(cached1Infos);
		assertEquals(1, cached1Infos.size());
		final QueryInfo cached1 = cached1Infos.iterator().next();
		if(model.getProperties().getQueryCacheLimit()>0)
		{
			assertEquals("query cache hit #1 for " + statement, cached1.getText());
			assertEqualsUnmodifiable(list(), cached1.getChilds());
		}
		else
		{
			assertTrue(cached1.getText(), cached1.getText().startsWith("select "));
		}

		transaction.setQueryInfoEnabled(false);
		assertNull(transaction.getQueryInfos());
		
		transaction.setQueryInfoEnabled(true);
		query.search();
		final List<QueryInfo> cached2Infos = transaction.getQueryInfos();
		assertUnmodifiable(cached2Infos);
		assertEquals(1, cached2Infos.size());
		final QueryInfo cached2 = cached2Infos.iterator().next();
		if(model.getProperties().getQueryCacheLimit()>0)
		{
			assertEquals("query cache hit #2 for " + statement, cached2.getText());
			assertEqualsUnmodifiable(list(), cached2.getChilds());
		}
		else
		{
			assertTrue(cached1.getText(), cached1.getText().startsWith("select "));
		}

		transaction.setQueryInfoEnabled(false);
		assertNull(transaction.getQueryInfos());

		query.search();
		assertNull(transaction.getQueryInfos());
	}
}
