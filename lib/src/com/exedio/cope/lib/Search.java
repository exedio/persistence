
package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualAttributeCondition;
import com.exedio.cope.lib.search.EqualCondition;
import com.exedio.cope.lib.search.GreaterCondition;
import com.exedio.cope.lib.search.GreaterEqualCondition;
import com.exedio.cope.lib.search.JoinCondition;
import com.exedio.cope.lib.search.LessCondition;
import com.exedio.cope.lib.search.LessEqualCondition;
import com.exedio.cope.lib.search.OrCondition;

/**
 * Utility class for searching persistent data.
 * May be subclassed to access methods without class qualifier.
 */
public abstract class Search
{
	Search()
	{}

	static final long pk2id(final int pk)
	{
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException("not a pk");

		final long longPk = (long)pk;
		return
			(pk>=0) ?
				(longPk<<1) : // 2*pk
				-((longPk<<1)|1l); // -(2*pk + 1)
	}

	static final int id2pk(final long id)
			throws NoSuchIDException
	{
		if(id<0)
			throw new NoSuchIDException(id, "must be positive");
		if(id>=4294967296l)
			throw new NoSuchIDException(id, "does not fit in 32 bit");

		final long result =
			((id&1l)>0) ? // odd id ?
				-((id>>>1)+1l) : // -(id/2 +1)
				id>>1; // id/2

		//System.out.println("id2pk: "+id+" -> "+result);
		if(result==(long)Type.NOT_A_PK)
			throw new NoSuchIDException(id, "is a NOT_A_PK");

		return (int)result;
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeItem() active} objects.
	 * @see Item#getID()
	 * @throws NoSuchIDException if there is no item with the given id.
	 */
	public static final Item findByID(final String id)
			throws NoSuchIDException
	{
		final int pos = id.lastIndexOf('.');
		if(pos<=0)
			throw new NoSuchIDException(id, "no dot in id");

		final String typeName = id.substring(0, pos);
		final Type type = Type.getType(typeName);
		if(type==null)
			throw new NoSuchIDException(id, "no such type "+typeName);
		
		final String idString = id.substring(pos+1);

		final long idNumber;
		try
		{
			idNumber = Long.parseLong(idString);
		}
		catch(NumberFormatException e)
		{
			throw new NoSuchIDException(id, e, idString);
		}

		final Item result = type.getItem(id2pk(idNumber));
		// Must be activated to make sure, that an item with
		// such a pk really exists for that type.
		try
		{
			result.activeItem();
		}
		catch(RuntimeException e)
		{
			if("no such pk".equals(e.getMessage()))
				throw new NoSuchIDException(id, "item <"+idNumber+"> does not exist");
			else
				throw new SystemException(e);
		}
		return result;
	}
	
	public static final EqualCondition isNull(final ObjectAttribute attribute)
	{
		return new EqualCondition(attribute);
	}
	
	public static final EqualCondition equal(final StringAttribute attribute, final String value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualAttributeCondition equal(final StringAttribute attribute1, final StringAttribute attribute2)
	{
		return new EqualAttributeCondition(attribute1, attribute2);
	}
	
	public static final EqualCondition equal(final IntegerAttribute attribute, final Integer value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final IntegerAttribute attribute, final int value)
	{
		return new EqualCondition(attribute, new Integer(value));
	}
	
	public static final EqualCondition equal(final LongAttribute attribute, final Long value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final LongAttribute attribute, final long value)
	{
		return new EqualCondition(attribute, new Long(value));
	}
	
	public static final EqualCondition equal(final DoubleAttribute attribute, final Double value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final DoubleAttribute attribute, final double value)
	{
		return new EqualCondition(attribute, new Double(value));
	}
	
	public static final EqualCondition equal(final ItemAttribute attribute, final Item value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final LessCondition less(final StringAttribute attribute, final String value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessCondition less(final IntegerAttribute attribute, final int value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessCondition less(final LongAttribute attribute, final long value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessCondition less(final DoubleAttribute attribute, final double value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessCondition less(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final StringAttribute attribute, final String value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final IntegerAttribute attribute, final int value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final LongAttribute attribute, final long value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final DoubleAttribute attribute, final double value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final StringAttribute attribute, final String value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final IntegerAttribute attribute, final int value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final LongAttribute attribute, final long value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final DoubleAttribute attribute, final double value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final StringAttribute attribute, final String value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final IntegerAttribute attribute, final int value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final LongAttribute attribute, final long value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final DoubleAttribute attribute, final double value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final JoinCondition join(final ItemAttribute attribute)
	{
		return new JoinCondition(attribute);
	}
	
	public static final AndCondition and(final Condition condition1, final Condition condition2)
	{
		return new AndCondition(new Condition[]{condition1, condition2});
	}
	
	public static final OrCondition or(final Condition condition1, final Condition condition2)
	{
		return new OrCondition(new Condition[]{condition1, condition2});
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
		query.check();
		return wrapPrimaryKeys(query.selectType, Database.theInstance.search(query));
	}
	
	/**
	 * TODO: should throw a non-RuntimeException,
	 * if there is more than one item found.
	 * TODO: should have a unique constraint as parameter,
	 * instead of the condition, then the previous todo is obsolete. 
	 */
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
