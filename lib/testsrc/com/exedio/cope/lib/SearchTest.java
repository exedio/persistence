/*
 * Created on 02.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.exedio.cope.lib;

/**
 * @author rw7
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SearchTest extends AbstractLibTest
{
	public void testUnmodifiableSearchResult()
	{
		final ItemWithoutAttributes someItem = new ItemWithoutAttributes();
		final ItemWithManyAttributes item;
		try
		{
			item = new ItemWithManyAttributes("someString", 5, true, someItem);
		}
		catch(NotNullViolationException e)
		{
			throw new SystemException(e);
		}
		item.setSomeNotNullInteger(0);
		assertUnmodifiable(Search.search(item.TYPE, Search.equal(item.someNotNullInteger, 0)));
	}

}
