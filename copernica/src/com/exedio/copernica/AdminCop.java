package com.exedio.copernica;

import java.util.Map;

import com.exedio.cope.lib.ReportTable;

final class AdminCop extends Cop
{
	static final String REPORT = "report";
	static final String SHOW = "show";
	static final String SHOW_DROP_BOXES = "drop";

	final boolean report;
	final String reportTable;
	final boolean showDropBoxes;
	final boolean showRenameFields;

	AdminCop(final boolean report, final String reportTable, final boolean showDropBoxes)
	{
		super("admin.jsp");
		this.report = report;
		this.reportTable = reportTable;
		this.showDropBoxes = showDropBoxes;
		this.showRenameFields = showDropBoxes;
		if(!report && reportTable!=null)
			throw new RuntimeException();
		
		if(report)
			addParameter(REPORT, reportTable==null ? "" : reportTable);
		if(showDropBoxes)
			addParameter(SHOW, SHOW_DROP_BOXES);
	}
	
	final AdminCop toggleReport()
	{
		return new AdminCop(!report, null, false);
	}
	
	final AdminCop narrowReport(final ReportTable reportTable)
	{
		if(!report)
			throw new RuntimeException();
			
		return new AdminCop(true, reportTable.name, showDropBoxes);
	}
	
	final AdminCop widenReport()
	{
		if(!report)
			throw new RuntimeException();
			
		return new AdminCop(true, null, showDropBoxes);
	}
	
	final AdminCop toggleDropBoxes()
	{
		if(!report)
			throw new RuntimeException();

		return new AdminCop(true, reportTable, !showDropBoxes);
	}
	
	final boolean isNarrowReport()
	{
		return reportTable!=null;
	}
	
	final boolean skipTable(final ReportTable table)
	{
		return reportTable!=null && !reportTable.equals(table.name);
	}

	static final AdminCop getCop(final Map parameterMap)
	{	
		final String reportID = getParameter(parameterMap, REPORT);
		if(reportID==null)
		{
			return new AdminCop(false, null, false);
		}
		else
		{
			final String showID = getParameter(parameterMap, SHOW);
			final boolean showDropBoxes = showID!=null;
			
			if(reportID.length()==0)
				return new AdminCop(true, null, showDropBoxes);
			else
				return new AdminCop(true, reportID, showDropBoxes);
		}
	}

}
