
package com.exedio.cope.lib;

import java.util.Iterator;

import com.exedio.cope.testmodel.ItemWithSingleUnique;


public class StatementInfoTest extends DatabaseLibTest
{
	
	public void testExecutionPlan()
	{
		final Query query = new Query(ItemWithSingleUnique.TYPE, Cope.equal(ItemWithSingleUnique.uniqueString, "zack"));
		query.enableMakeStatementInfo();
		query.search();
		final StatementInfo root = query.getStatementInfo();
		//root.print(System.out);
		
		assertTrue(root.text, root.text.startsWith("select "));
		
		final String database = model.getDatabase().getClass().getName();
		if(database.indexOf("HsqldbDatabase")>=0 || database.indexOf("MysqlDatabase")>=0)
		{
			assertEquals(list(), root.getChilds());
		}
		else if(database.indexOf("OracleDatabase")>=0)
		{
			final Iterator rootChilds = root.getChilds().iterator();
			{
				final StatementInfo planId = (StatementInfo)rootChilds.next();
				assertTrue(planId.text, planId.text.startsWith("execution plan statement_id = cope"));
				{
					final Iterator planIdChilds = planId.getChilds().iterator();
					{
						final StatementInfo planSelect = (StatementInfo)planIdChilds.next();
						assertEquals("SELECT STATEMENT optimizer=CHOOSE", planSelect.text);
						{
							final Iterator planSelectChilds = planSelect.getChilds().iterator();
							{
								final StatementInfo planTableAccess = (StatementInfo)planSelectChilds.next();
								assertEquals("TABLE ACCESS (BY INDEX ROWID) on ItemWithSingleUnique[1]", planTableAccess.text);
								{
									final Iterator planTableAccessChilds = planTableAccess.getChilds().iterator();
									{
										final StatementInfo planUnique = (StatementInfo)planTableAccessChilds.next();
										assertEquals("INDEX (UNIQUE SCAN) on ItemWithSingUni_unStr_Unq[UNIQUE] search_columns=1", planUnique.text);
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
	}
	
}
