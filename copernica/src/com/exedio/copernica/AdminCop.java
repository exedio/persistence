package com.exedio.copernica;

import java.util.Map;

import com.exedio.cope.lib.ReportTable;

final class AdminCop extends Cop
{
	static final String REPORT = "report";

	final boolean report;
	final String reportTable;

	AdminCop(final boolean report, final String reportTable)
	{
		super("admin.jsp");
		this.report = report;
		this.reportTable = reportTable;
		if(!report && reportTable!=null)
			throw new RuntimeException();
		
		if(report)
			addParameter(REPORT, reportTable==null ? "" : reportTable);
	}
	
	final AdminCop toggleReport()
	{
		return new AdminCop(!report, null);
	}
	
	final AdminCop narrowReport(final ReportTable reportTable)
	{
		if(!report)
			throw new RuntimeException();
			
		return new AdminCop(true, reportTable.name);
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
			return new AdminCop(false, null);
		}
		else
		{
			if(reportID.length()==0)
				return new AdminCop(true, null);
			else
				return new AdminCop(true, reportID);
		}
	}

}
