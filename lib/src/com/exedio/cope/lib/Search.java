
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualCondition;
import java.util.ArrayList;

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
	 * Always returns {@link Item#activeItem() active} objects.
	 * @see Item#getID()
	 * @throws RuntimeException if there is no item with the given id. TODO: use non-RuntimeException
	 */
	public static final Item findByID(final String id)
	{
		final int pos = id.lastIndexOf('.');
		if(pos<=0)
			throw new RuntimeException("no dot");

		final String typeName = id.substring(0, pos);
		final Type type = Type.getType(typeName);
		if(type==null)
			throw new RuntimeException("no type "+typeName);
		
		final String pkString = id.substring(pos+1);
		final int pk;
		try
		{
			pk = Integer.parseInt(pkString);
		}
		catch(NumberFormatException e)
		{
			throw new RuntimeException("not a number "+pkString);
		}

		final Item result = type.getItem(pk);
		// Must be activated to make sure, that an item with
		// such a pk really exists for that type.
		result.activeItem();
		return result;
	}
	
	public static final EqualCondition equal(final StringAttribute attribute, final String value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final IntegerAttribute attribute, final Integer value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final IntegerAttribute attribute, final int value)
	{
		return new EqualCondition(attribute, new Integer(value));
	}
	
	public static final EqualCondition equal(final ItemAttribute attribute, final Item value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final AndCondition and(final Condition condition1, final Condition condition2)
	{
		return new AndCondition(new Condition[]{condition1, condition2});
	}
	
	/**
	 * Converts a collection of primary keys to a collection of items of the given type.
	 * @param pks the collection of primary keys, is expected not to be modified
	 * @return an unmodifiable collection.
	 */
	private static final Collection wrapPrimaryKeys(final Type type, final Collection pks)
	{
		// TODO: dont convert all items at once, but use some kind of wrapper collection
		final ArrayList result = new ArrayList(pks.size());
		for(Iterator i = pks.iterator(); i.hasNext(); )
		{
			final int pk = ((Integer)i.next()).intValue();
			//System.out.println("pk:"+pk);
			result.add(type.getItem(pk));
		}
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Searches for items of the given type, that match the given condition.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.
	 * @param type the type the searched items must match.
	 * @param condition the condition the searched items must match.
	 */
	public static final Collection search(final Type type, final Condition condition)
	{
		return search(new Query(type, condition));
	}
	
	/**
	 * Searches for items matching the given query.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.
	 * @param query the query the searched items must match.
	 */
	public static final Collection search(final Query query)
	{
		//System.out.println("select " + type.getJavaClass().getName() + " where " + condition);
		return wrapPrimaryKeys(query.type, Database.theInstance.search(query));
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
