
package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import bak.pcj.IntIterator;
import bak.pcj.list.IntArrayList;

import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualAttributeCondition;
import com.exedio.cope.lib.search.EqualCondition;
import com.exedio.cope.lib.search.GreaterCondition;
import com.exedio.cope.lib.search.GreaterEqualCondition;
import com.exedio.cope.lib.search.JoinCondition;
import com.exedio.cope.lib.search.LessCondition;
import com.exedio.cope.lib.search.LessEqualCondition;
import com.exedio.cope.lib.search.NotEqualCondition;
import com.exedio.cope.lib.search.OrCondition;

/**
 * Utility class for searching persistent data.
 * May be subclassed to access methods without class qualifier.
 */
public abstract class Search
{
	Search()
	{}

	public static final EqualCondition isNull(final ObjectAttribute attribute)
	{
		return new EqualCondition(attribute);
	}
	
	public static final NotEqualCondition isNotNull(final ObjectAttribute attribute)
	{
		return new NotEqualCondition(attribute);
	}
	
	public static final EqualCondition equal(final StringFunction function, final String value)
	{
		return new EqualCondition(function, value);
	}
	
	public static final EqualAttributeCondition equal(final StringAttribute attribute1, final StringAttribute attribute2)
	{
		return new EqualAttributeCondition(attribute1, attribute2);
	}
	
	public static final EqualCondition equal(final IntegerFunction function, final Integer value)
	{
		return new EqualCondition(function, value);
	}
	
	public static final EqualCondition equal(final IntegerFunction function, final int value)
	{
		return new EqualCondition(function, new Integer(value));
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
	
	public static final EqualCondition equal(final BooleanAttribute attribute, final Boolean value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final BooleanAttribute attribute, final boolean value)
	{
		return new EqualCondition(attribute, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public static final EqualCondition equal(final DateAttribute attribute, final Date value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final EqualCondition equal(final ItemAttribute attribute, final Item value)
	{
		return new EqualCondition(attribute, value);
	}
	
	public static final NotEqualCondition notEqual(final StringFunction function, final String value)
	{
		return new NotEqualCondition(function, value);
	}
	
	public static final NotEqualCondition notEqual(final IntegerFunction function, final Integer value)
	{
		return new NotEqualCondition(function, value);
	}
	
	public static final NotEqualCondition notEqual(final IntegerFunction function, final int value)
	{
		return new NotEqualCondition(function, new Integer(value));
	}
	
	public static final NotEqualCondition notEqual(final LongAttribute attribute, final Long value)
	{
		return new NotEqualCondition(attribute, value);
	}
	
	public static final NotEqualCondition notEqual(final LongAttribute attribute, final long value)
	{
		return new NotEqualCondition(attribute, new Long(value));
	}
	
	public static final NotEqualCondition notEqual(final BooleanAttribute attribute, final Boolean value)
	{
		return new NotEqualCondition(attribute, value);
	}
	
	public static final NotEqualCondition notEqual(final BooleanAttribute attribute, final boolean value)
	{
		return new NotEqualCondition(attribute, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public static final NotEqualCondition notEqual(final DoubleAttribute attribute, final Double value)
	{
		return new NotEqualCondition(attribute, value);
	}
	
	public static final NotEqualCondition notEqual(final DoubleAttribute attribute, final double value)
	{
		return new NotEqualCondition(attribute, new Double(value));
	}
	
	public static final NotEqualCondition notEqual(final DateAttribute attribute, final Date value)
	{
		return new NotEqualCondition(attribute, value);
	}
	
	public static final NotEqualCondition notEqual(final ItemAttribute attribute, final Item value)
	{
		return new NotEqualCondition(attribute, value);
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
	
	public static final LessCondition less(final DateAttribute attribute, final Date value)
	{
		return new LessCondition(attribute, value);
	}
	
	public static final LessCondition less(final EnumAttribute attribute, final EnumValue value)
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
	
	public static final LessEqualCondition lessOrEqual(final DateAttribute attribute, final Date value)
	{
		return new LessEqualCondition(attribute, value);
	}
	
	public static final LessEqualCondition lessOrEqual(final EnumAttribute attribute, final EnumValue value)
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
	
	public static final GreaterCondition greater(final DateAttribute attribute, final Date value)
	{
		return new GreaterCondition(attribute, value);
	}
	
	public static final GreaterCondition greater(final EnumAttribute attribute, final EnumValue value)
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
	
	public static final GreaterEqualCondition greaterOrEqual(final DateAttribute attribute, final Date value)
	{
		return new GreaterEqualCondition(attribute, value);
	}
	
	public static final GreaterEqualCondition greaterOrEqual(final EnumAttribute attribute, final EnumValue value)
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
	private static final Collection wrapPrimaryKeys(final Type type, final IntArrayList pks)
	{
		// TODO: dont convert all items at once, but use some kind of wrapper collection
		final ArrayList result = new ArrayList(pks.size());
		for(IntIterator i = pks.iterator(); i.hasNext(); )
		{
			final int pk = i.next();
			//System.out.println("pk:"+pk);
			result.add(type.getItem(pk));
		}
		return Collections.unmodifiableList(result);
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
		final Type selectType = query.selectType;
		return wrapPrimaryKeys(selectType, selectType.getModel().getDatabase().search(query));
	}
	
}
