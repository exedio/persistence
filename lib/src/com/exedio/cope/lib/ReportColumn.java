package com.exedio.cope.lib;

public final class ReportColumn extends ReportNode
{
	public final String name;
	private final Column column;
	boolean exists;
		
	ReportColumn(final Column column)
	{
		this.name = column.id;
		this.column = column;
		exists = false;
	}

	ReportColumn(final String name)
	{
		this.name = name;
		this.column = null;
		exists = true;
	}

	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(!exists)
		{
			error = "missing";
			particularColor = COLOR_RED;
		}
		else if(column==null)
		{
			error = "not used";
			particularColor = COLOR_YELLOW;
		}
		else
			particularColor = COLOR_OK;
				
		cumulativeColor = particularColor;
	}
		
	public final boolean required()
	{
		return column!=null;
	}
	
	public final boolean exists()
	{
		return exists;
	}
		
}
	
