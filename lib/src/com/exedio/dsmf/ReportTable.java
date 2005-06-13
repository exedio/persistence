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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;



public final class ReportTable extends ReportNode
{
	final ReportSchema report;
	final String name;
	private final boolean required;
	private boolean exists;
	private ReportLastAnalyzed lastAnalyzed = null;

	private final HashMap columnMap = new HashMap();
	private final ArrayList columnList = new ArrayList();

	private final HashMap constraintMap = new HashMap();
	private final ArrayList constraintList = new ArrayList();

	public ReportTable(final ReportSchema report, final String name)
	{
		this(report, name, true);
	}
	
	ReportTable(final ReportSchema report, final String name, final boolean required)
	{
		super(report.driver, report.connectionProvider);
		
		if(report==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		this.report = report;
		this.name = name;
		this.required = required;
		this.exists = !required;

		report.register(this);
	}
	
	public final String getName()
	{
		return name;
	}

	final void register(final ReportColumn column)
	{
		if(columnMap.put(column.name, column)!=null)
			throw new RuntimeException(column.toString());
		columnList.add(column);
	}
	
	final void register(final ReportConstraint constraint)
	{
		if(constraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException(constraint.name);
		constraintList.add(constraint);
	}
	
	final void setLastAnalyzed(final Date lastAnalyzed)
	{
		if(this.lastAnalyzed!=null)
			throw new RuntimeException();

		this.lastAnalyzed = new ReportLastAnalyzed(this, lastAnalyzed);
	}
	
	final void notifyExists()
	{
		exists = true;
	}
	
	final ReportColumn notifyExistentColumn(final String columnName, final String existingType)
	{
		ReportColumn result = (ReportColumn)columnMap.get(columnName);
		if(result==null)
			result = new ReportColumn(this, columnName, existingType, false);
		else
			result.notifyExists(existingType);

		return result;
	}
	
	final ReportConstraint notifyExistentCheckConstraint(final String constraintName, final String condition)
	{
		ReportConstraint result = (ReportConstraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new ReportCheckConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);

		return result;
	}
	
	final ReportConstraint notifyExistentPrimaryKeyConstraint(final String constraintName)
	{
		ReportConstraint result = (ReportConstraint)constraintMap.get(constraintName);

		if(result==null)
			result = new ReportPrimaryKeyConstraint(this, constraintName, false, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final ReportConstraint notifyExistentForeignKeyConstraint(final String constraintName)
	{
		ReportConstraint result = (ReportConstraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new ReportForeignKeyConstraint(this, constraintName, false, null, null, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final ReportConstraint notifyExistentUniqueConstraint(final String constraintName, final String condition)
	{
		ReportConstraint result = (ReportConstraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new ReportUniqueConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);
		
		return result;
	}
	
	public final boolean required()
	{
		return required;
	}
	
	public final boolean exists()
	{
		return exists;
	}
		
	public final ReportLastAnalyzed getLastAnalyzed()
	{
		return lastAnalyzed;
	}
		
	public final Collection getColumns()
	{
		return columnList;
	}
		
	public final ReportColumn getColumn(final String columnName)
	{
		return (ReportColumn)columnMap.get(columnName);
	}
		
	public final Collection getConstraints()
	{
		return constraintList;
	}
		
	public final ReportConstraint getConstraint(final String constraintName)
	{
		return (ReportConstraint)constraintMap.get(constraintName);
	}
		
	void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		final String error;
		final int particularColor;
		if(!exists)
		{
			error = "MISSING !!!";
			particularColor = COLOR_ERROR;
		}
		else if(!required)
		{
			error = "not used";
			particularColor = COLOR_WARNING;
		}
		else
		{
			error = null;
			particularColor = COLOR_OK;
		}
				
		this.error = error;
		this.particularColor = particularColor;
		cumulativeColor = particularColor;
			
		if(lastAnalyzed!=null)
		{
			lastAnalyzed.finish();
			cumulativeColor = Math.max(cumulativeColor, lastAnalyzed.cumulativeColor);
		}
			
		for(Iterator i = columnList.iterator(); i.hasNext(); )
		{
			final ReportColumn column = (ReportColumn)i.next();
			column.finish();
			cumulativeColor = Math.max(cumulativeColor, column.cumulativeColor);
		}

		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			constraint.finish();
			cumulativeColor = Math.max(cumulativeColor, constraint.cumulativeColor);
		}
	}
	
	public final void create()
	{
		final StringBuffer bf = new StringBuffer();

		bf.append("create table ").
			append(protectName(name)).
			append('(');

		boolean firstColumn = true;
		for(Iterator i = columnList.iterator(); i.hasNext(); )
		{
			if(firstColumn)
				firstColumn = false;
			else
				bf.append(',');
			
			final ReportColumn column = (ReportColumn)i.next();
			bf.append(protectName(column.name)).
				append(' ').
				append(column.getType());
		}
		
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();

			if(constraint instanceof ReportCheckConstraint)
			{
				final ReportCheckConstraint check = (ReportCheckConstraint)constraint;
				bf.append(",constraint ").
					append(protectName(check.name)).
					append(" check(").
					append(check.requiredCondition).
					append(')');
			}
			else if(constraint instanceof ReportPrimaryKeyConstraint)
			{
				final ReportPrimaryKeyConstraint pk = (ReportPrimaryKeyConstraint)constraint;
				bf.append(",constraint ").
					append(protectName(pk.name)).
					append(" primary key(").
					append(protectName(pk.primaryKeyColumn)).
					append(')');
			}
			else if(constraint instanceof ReportForeignKeyConstraint)
				; // this is done in createForeignKeyConstraints
			else if(constraint instanceof ReportUniqueConstraint)
			{
				final ReportUniqueConstraint unique = (ReportUniqueConstraint)constraint;
				bf.append(",constraint ").
					append(protectName(unique.name)).
					append(" unique").
					append(unique.clause);
			}
			else
				throw new RuntimeException(constraint.getClass().getName());
		}

		bf.append(')');
		driver.appendTableCreateStatement(bf);

		//System.out.println("createTable:"+bf.toString());
		executeSQL(bf.toString());
	}
	
	final void createForeignKeyConstraints()
	{
		//System.out.println("createForeignKeyConstraints:"+bf);

		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			//System.out.println("createForeignKeyConstraints("+column+"):"+bf);
			if(constraint instanceof ReportForeignKeyConstraint)
				((ReportForeignKeyConstraint)constraint).create();
		}
	}
	
	public final void renameTo(final String newName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(protectName(name)).
			append(" rename to ").
			append(protectName(newName));

		//System.out.println("renameTable:"+bf);
		executeSQL(bf.toString());
	}

	public final void drop()
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("drop table ").
			append(protectName(name));

		executeSQL(bf.toString());
	}
	
	final void dropForeignKeyConstraints(final boolean log) 
	{
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			//System.out.println("dropForeignKeyConstraints("+column+")");
			if(constraint instanceof ReportForeignKeyConstraint)
			{
				final ReportForeignKeyConstraint fk = (ReportForeignKeyConstraint)constraint;
				if(log)
					System.err.println("DROPPING FOREIGN KEY CONSTRAINTS "+name+" "+fk.name+"... ");
				fk.drop();
			}
		}
	}
	
	public final void analyze()
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("analyze table ").
			append(protectName(name)).
			append(" compute statistics");

		//System.out.println("analyzeTable:"+bf);
		executeSQL(bf.toString());
	}

	public final String toString()
	{
		return name;
	}
	
}
