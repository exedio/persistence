package com.exedio.cope.lib;

public final class ReportColumn extends ReportNode
{
	public final String name;
	public final ReportTable table;
	private final Column column;
	private boolean exists;
	private String existingType;
		
	ReportColumn(final Column column, final ReportTable table)
	{
		this.name = column.id;
		this.table = table;
		this.column = column;
		exists = false;
	}

	ReportColumn(final String name, final String existingType, final ReportTable table)
	{
		this.name = name;
		this.table = table;
		this.column = null;
		this.existingType = existingType;
		exists = true;
	}
	
	void notifyExists(final String existingType)
	{
		if(exists && !this.existingType.equals(existingType))
			throw new RuntimeException(name);

		this.existingType = existingType;
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
		{
			if(column!=null &&
				existingType!=null &&
				!column.databaseType.equals(existingType))
			{
				error = "different type in database: >"+existingType+"<";
				particularColor = COLOR_RED;
			}
			else
				particularColor = COLOR_OK;
		}
				
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
		
	public final String getDatabaseType()
	{
		if(column!=null)
			return column.databaseType;
		else
			return existingType;
	}
		
	public final void create()
	{
		table.report.database.createColumn(column);
	}

	public final void renameTo(final String newName)
	{
		table.report.database.renameColumn(table.name, name, newName);
	}

	public final void drop()
	{
		table.report.database.dropColumn(table.name, name);
	}

}
	
