package com.exedio.copernica;

import java.util.Map;

final class AdminCop extends Cop
{
	static final String REPORT = "report";

	final boolean report;

	AdminCop(final boolean report)
	{
		super("admin.jsp");
		this.report = report;
		if(report)
			addParameter(REPORT, "true");
	}
	
	final AdminCop toggleReport()
	{
		return new AdminCop(!report);
	}

	static final AdminCop getCop(final Map parameterMap)
	{	
		final String reportID = getParameter(parameterMap, REPORT);
		return new AdminCop(reportID!=null);
	}

}
