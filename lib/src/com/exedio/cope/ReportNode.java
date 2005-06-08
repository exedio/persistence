/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.exedio.cope;

public abstract class ReportNode
{
	static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_WARNING = 2;
	public static final int COLOR_ERROR = 3;

	protected String error = null;
	protected int particularColor = ReportSchema.COLOR_NOT_YET_CALC;
	protected int cumulativeColor = ReportSchema.COLOR_NOT_YET_CALC;

	abstract void finish();

	public final String getError()
	{
		if(particularColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return error;
	}

	public final int getParticularColor()
	{
		if(particularColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return particularColor;
	}

	public final int getCumulativeColor()
	{
		if(cumulativeColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return cumulativeColor;
	}
}

