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

	@Override
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public Type<? extends Item> getType()
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public void toString( StringBuilder bf, Type defaultType )
	{
		bf.append( "count(*)" );
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void check( TC tc, Join join )
	{
		// TODO
		// probably: nothing to do here, since there are no sources
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void append( Statement bf, Join join )
	{
		bf.append( "count(*)" );
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void appendSelect( Statement bf, Join join )
	{
		bf.append( "count(*)" );
	}

}
