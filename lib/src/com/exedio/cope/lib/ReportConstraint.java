package com.exedio.cope.lib;


public final class ReportConstraint extends ReportNode
{
	public final String name;
	public final ReportTable table;
	boolean required = false;
	boolean exists = false;
		
	ReportConstraint(final String name, final ReportTable table)
	{
		this.name = name;
		this.table = table; 
	}

	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		// TODO: make this dependend on type of constraint:
		// check/not null constraint are yellow only if missing
		// foreign key/unique constraint are red when missing or unused
		if(!exists)
		{
			error = "missing";
			particularColor = COLOR_RED;
		}
		else if(!required)
		{
			error = "not used";
			if(!table.required())
				particularColor = COLOR_YELLOW;
			else
				particularColor = COLOR_RED;
		}
		else
			particularColor = COLOR_OK;
				
		cumulativeColor = particularColor;
	}

	public final boolean required()
	{
		return required;
	}

	public final boolean exists()
	{
		return exists;
	}

}
