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

import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class QueryInfoTest extends TestWithEnvironment
{
	public QueryInfoTest()
	{
		super(SchemaTest.MODEL);
	}

	@Test public void testExecutionPlan()
	{
		final Transaction transaction = model.currentTransaction();
		final Query<?> query = SchemaItem.TYPE.newQuery(SchemaItem.uniqueString.equal("zack"));
		transaction.setQueryInfoEnabled(true);
		query.search();
		final List<QueryInfo> infos = transaction.getQueryInfos();
		assertUnmodifiable(infos);
		assertEquals(1, infos.size());
		final QueryInfo root = infos.iterator().next();
		assertUnmodifiable(root.getChilds());
		//root.print(System.out);

		assertEquals(query.toString(), root.getText());

		final Iterator<QueryInfo> rootChilds = root.getChilds().iterator();
		final QueryInfo statementInfo = rootChilds.next();
		assertTrue(statementInfo.getText(), statementInfo.getText().startsWith("SELECT "));
		if(!model.getConnectProperties().isSupportDisabledForPreparedStatements())
		{
			final Iterator<QueryInfo> statementInfoChilds = statementInfo.getChilds().iterator();
			{
				final QueryInfo statementInfoChild = statementInfoChilds.next();
				assertEquals("zack", statementInfoChild.getText());
				assertContains(statementInfoChild.getChilds());
			}
			assertTrue(!statementInfoChilds.hasNext());
		}
		else
		{
			assertContains(statementInfo.getChilds());
		}
		{
			final QueryInfo timing = rootChilds.next();
			assertTrue(timing.getText(), timing.getText().startsWith("time elapsed "));
			final Iterator<QueryInfo> timingInfoChilds = timing.getChilds().iterator();
			{
				final QueryInfo timingPrepare = timingInfoChilds.next();
				assertTrue(timingPrepare.getText(), timingPrepare.getText().startsWith("prepare "));
				assertContains(timingPrepare.getChilds());
			}
			{
				final QueryInfo timingExecute = timingInfoChilds.next();
				assertTrue(timingExecute.getText(), timingExecute.getText().startsWith("execute "));
				assertContains(timingExecute.getChilds());
			}
			{
				final QueryInfo timingReadResult = timingInfoChilds.next();
				assertTrue(timingReadResult.getText(), timingReadResult.getText().startsWith("result "));
				assertContains(timingReadResult.getChilds());
			}
			{
				final QueryInfo timingClose = timingInfoChilds.next();
				assertTrue(timingClose.getText(), timingClose.getText().startsWith("close "));
				assertContains(timingClose.getChilds());
			}
			assertTrue(!timingInfoChilds.hasNext());
		}

		switch(dialect)
		{
			case mysql:
			{
				final QueryInfo plan = rootChilds.next();
				assertEquals("explain plan", plan.getText());
				break;
			}
			case oracle:
			{
				final QueryInfo planId = rootChilds.next();
				assertTrue(planId.getText(), planId.getText().startsWith("explain plan statement_id=cope"));
				{
					final Iterator<QueryInfo> planIdChilds = planId.getChilds().iterator();
					{
						final QueryInfo planSelect = planIdChilds.next();
						assertTrue(planSelect.getText(), planSelect.getText().startsWith("SELECT STATEMENT plan_id="));
						{
							final Iterator<QueryInfo> planSelectChilds = planSelect.getChilds().iterator();
							{
								final QueryInfo planTableAccess = planSelectChilds.next();
								assertTrue(planTableAccess.getText(), planTableAccess.getText().startsWith("TABLE ACCESS (BY INDEX ROWID) on Main[1]"));
								{
									final Iterator<QueryInfo> planTableAccessChilds = planTableAccess.getChilds().iterator();
									{
										final QueryInfo planUnique = planTableAccessChilds.next();
										assertTrue(planUnique.getText(), planUnique.getText().startsWith("INDEX (UNIQUE SCAN) on Main_uniqueString_Unq"));
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
			case hsqldb:
			case postgresql:
				assertFalse(rootChilds.hasNext());
				break;
			default:
				fail(dialect.toString());
		}

		assertTrue(!rootChilds.hasNext());

		// test multiple queries
		final String query1String = query.toString();
		query.setOrderBy(SchemaItem.uniqueString, true);
		final String query2String = query.toString();
		query.search();
		final List<QueryInfo> rootOrdered = transaction.getQueryInfos();
		//rootOrdered.print(System.out);
		assertUnmodifiable(rootOrdered);
		final Iterator<QueryInfo> rootOrderedIterator = rootOrdered.iterator();
		final QueryInfo ordered1 = rootOrderedIterator.next();
		assertEquals(query1String, ordered1.getText());
		final QueryInfo ordered2 = rootOrderedIterator.next();
		assertEquals(query2String, ordered2.getText());
		assertTrue(!rootOrderedIterator.hasNext());

		transaction.setQueryInfoEnabled(false);
		assertNull(transaction.getQueryInfos());

		final String statement =
			"select this " +
			"from Main " +
			"where uniqueString='zack' " +
			"order by uniqueString";

		transaction.setQueryInfoEnabled(true);
		query.search();
		final List<QueryInfo> cached1Infos = transaction.getQueryInfos();
		assertUnmodifiable(cached1Infos);
		assertEquals(1, cached1Infos.size());
		final QueryInfo cached1 = cached1Infos.iterator().next();
		if(model.getConnectProperties().getQueryCacheLimit()>0)
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
		if(model.getConnectProperties().getQueryCacheLimit()>0)
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
