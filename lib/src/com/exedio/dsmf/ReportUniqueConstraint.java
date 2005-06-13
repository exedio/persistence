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
package com.exedio.dsmf;



public class ReportUniqueConstraint extends ReportConstraint
{
	final String clause;
	
	public ReportUniqueConstraint(final ReportTable table, final String name, final String clause)
	{
		this(table, name, true, clause);
	}

	ReportUniqueConstraint(final ReportTable table, final String name, final boolean required, final String clause)
	{
		super(table, name, required, clause);

		if(clause==null)
			throw new RuntimeException(name);
		
		this.clause = clause;
		//System.out.println("-------------"+name+"-"+clause);
	}
	
	public final String getClause()
	{
		return clause;
	}

}
