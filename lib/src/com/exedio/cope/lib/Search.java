
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualCondition;

/**
 * Utility class for searching persistent data.
 * May be subclassed to access methods without class qualifier.
 */
public class Search
{
	/**
	 * Search shall never be instantiated.
	 */
	protected Search()
	{}
	
	/**
	 * Returns the item with the given ID.
	 * Returns null, if no such item exists.
	 * Always returns {@link Item#primaryItem() primary} objects.
	 * @see Item#getID()
	 */
	public static final Item findByID(final String id)
	{
		return null;
	}
	
	public static final EqualCondition equal(final StringAttribute attribute, final String value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final ItemAttribute attribute, final Item value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final AndCondition and(final Condition condition1, final Condition condition2)
	{
		return new AndCondition(new Condition[]{condition1, condition2});
	}
	
	public static final Collection search(final Type type, final Condition condition)
	{
		//System.out.println("select " + type.getJavaClass().getName() + " where " + condition);
		Database.theInstance.search(type, condition);
		return Collections.EMPTY_LIST;
	}
	
	public static final Item searchUnique(final Type type, final Condition condition)
	{
		final Iterator searchResult = search(type, condition).iterator();
		if(searchResult.hasNext())
		{
			final Item result = (Item)searchResult.next();
			if(searchResult.hasNext())
				throw new SystemException(null);
			else
				return result;
		}
		else
			return null;
	}
}
