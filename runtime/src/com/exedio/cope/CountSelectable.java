package com.exedio.cope;

/**
 * Use only as select in query using groupBy
 * <p>
 * Grouping functionality is 'beta' - API may change
 */
public class CountSelectable implements Selectable<Integer>
{
	private static final long serialVersionUID = 1l;

	public Class<Integer> getValueClass()
	{
		return Integer.class;
	}

	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	public Type<? extends Item> getType()
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void toString( StringBuilder bf, Type defaultType )
	{
		bf.append( "count(*)" );
	}

	public void check( TC tc, Join join )
	{
		// TODO
		// probably: nothing to do here, since there are no sources
	}

	public void append( Statement bf, Join join )
	{
		bf.append( "count(*)" );
	}

	public void appendSelect( Statement bf, Join join )
	{
		bf.append( "count(*)" );
	}

}
