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


public final class ReportConstraint extends ReportNode
{
	public static final int TYPE_CHECK = 0;
	public static final int TYPE_PRIMARY_KEY = 1;
	public static final int TYPE_FOREIGN_KEY = 2;
	public static final int TYPE_UNIQUE = 3;

	public final String name;
	public final int type;
	public final ReportTable table;
	private boolean required = false;
	public final String requiredCondition;
	private boolean exists = false;
	private String existingCondition;
		
	ReportConstraint(final String name, final int type, final ReportTable table)
	{
		this.name = name;
		this.type = type;
		this.table = table; 
		this.requiredCondition = null;
	}

	ReportConstraint(final String name, final int type, final ReportTable table, final String requiredCondition)
	{
		this.name = name;
		this.type = type;
		this.table = table;
		this.requiredCondition = requiredCondition;
	}

	final void notifyRequired()
	{
		if(required)
			throw new RuntimeException(name);

		required = true;
	}

	final void notifyExists()
	{
		exists = true;
	}

	final void notifyExistsCondition(final String condition)
	{
		exists = true;
		this.existingCondition = condition;
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
			particularColor = COLOR_ERROR;
		}
		else if(!required)
		{
			error = "not used";
			if(!table.required())
				particularColor = COLOR_WARNING;
			else
				particularColor = COLOR_ERROR;
		}
		else
		{
			if(requiredCondition!=null && existingCondition!=null &&
				!requiredCondition.equals(existingCondition))
			{
				error = "different condition in database: >"+existingCondition+"<";
				particularColor = COLOR_ERROR;
			}
			else if(requiredCondition==null && existingCondition!=null)
			{
				error = "surplus condition in database: >"+existingCondition+"<";
				particularColor = COLOR_ERROR;
			}
			else if(requiredCondition!=null && existingCondition==null)
			{
				error = "missing condition in database";
				particularColor = COLOR_ERROR;
			}
			else
				particularColor = COLOR_OK;
		}
				
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
	
	public final String toString()
	{
		return name;
	}

}
