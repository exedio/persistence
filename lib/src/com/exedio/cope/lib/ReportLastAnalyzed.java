package com.exedio.cope.lib;

import java.util.Date;

public final class ReportLastAnalyzed extends ReportNode
{
	public final Date lastAnalyzed;
	public final ReportTable table;
		
	ReportLastAnalyzed(final Date lastAnalyzed, final ReportTable table)
	{
		this.lastAnalyzed = lastAnalyzed;
		this.table = table;
	}

	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(lastAnalyzed==null)
		{
			error = "not analyzed !!!";
			particularColor = COLOR_RED;
		}
		else
			particularColor = COLOR_OK;

		if(table.table==null)
			particularColor = Math.min(particularColor, COLOR_YELLOW);
				
		cumulativeColor = particularColor;
	}
}
