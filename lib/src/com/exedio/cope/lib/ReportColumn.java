package com.exedio.cope.lib;

public final class ReportColumn extends ReportNode
{
	public final String name;
	public final ReportTable table;
	private final Column column;
	boolean exists;
		
	ReportColumn(final Column column, final ReportTable table)
	{
		this.name = column.id;
		this.table = table;
		this.column = column;
		exists = false;
	}

	ReportColumn(final String name)
	{
		this.name = name;
		this.table = null;
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
		
	public final void create()
	{
		Database.theInstance.createColumn(column);
	}

	public final void drop()
	{
		Database.theInstance.dropColumn(table.name, name);
	}

}
	
