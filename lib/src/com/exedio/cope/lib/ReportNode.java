package com.exedio.cope.lib;

public abstract class ReportNode
{
	static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_YELLOW = 2;
	public static final int COLOR_RED = 3;

	protected String error = null;
	protected int particularColor = Report.COLOR_NOT_YET_CALC;
	protected int cumulativeColor = Report.COLOR_NOT_YET_CALC;

	abstract void finish();

	public final String getError()
	{
		if(particularColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return error;
	}

	public final int getParticularColor()
	{
		if(particularColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return particularColor;
	}

	public final int getCumulativeColor()
	{
		if(cumulativeColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return cumulativeColor;
	}
}

