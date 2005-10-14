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



public final class Table extends Node
{
	private final Schema schema;
	final String name;
	final String options;
	private final boolean required;
	private boolean exists;
	private LastAnalyzed lastAnalyzed = null;
	
	private boolean defensive = false;

	private final HashMap columnMap = new HashMap();
	private final ArrayList columnList = new ArrayList();

	private final HashMap constraintMap = new HashMap();
	private final ArrayList constraintList = new ArrayList();

	public Table(final Schema schema, final String name, final String options)
	{
		this(schema, name, options, true);
	}
	
	public Table(final Schema schema, final String name)
	{
		this(schema, name, null, true);
	}
	
	Table(final Schema schema, final String name, final String options, final boolean required)
	{
		super(schema.driver, schema.connectionProvider);
		
		if(schema==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		this.schema = schema;
		this.name = name;
		this.options = options;
		this.required = required;
		this.exists = !required;

		schema.register(this);
	}
	
	public final void makeDefensive()
	{
		defensive = true;
	}
	
	public final String getName()
	{
		return name;
	}

	public final String getOptions()
	{
		return options;
	}

	final void register(final Column column)
	{
		if(columnMap.put(column.name, column)!=null)
			throw new RuntimeException(column.toString());
		columnList.add(column);
	}
	
	final void register(final Constraint constraint)
	{
		if(constraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException(constraint.name);
		constraintList.add(constraint);
	}
	
	final void setLastAnalyzed(final Date lastAnalyzed)
	{
		if(this.lastAnalyzed!=null)
			throw new RuntimeException();

		this.lastAnalyzed = new LastAnalyzed(this, lastAnalyzed);
	}
	
	final void notifyExists()
	{
		exists = true;
	}
	
	final Column notifyExistentColumn(final String columnName, final String existingType)
	{
		Column result = (Column)columnMap.get(columnName);
		if(result==null)
			result = new Column(this, columnName, existingType, false);
		else
			result.notifyExists(existingType);

		return result;
	}
	
	final Constraint notifyExistentCheckConstraint(final String constraintName, final String condition)
	{
		Constraint result = (Constraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new CheckConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);

		return result;
	}
	
	final Constraint notifyExistentPrimaryKeyConstraint(final String constraintName)
	{
		Constraint result = (Constraint)constraintMap.get(constraintName);

		if(result==null)
			result = new PrimaryKeyConstraint(this, constraintName, false, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final Constraint notifyExistentForeignKeyConstraint(final String constraintName)
	{
		Constraint result = (Constraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new ForeignKeyConstraint(this, constraintName, false, null, null, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final Constraint notifyExistentUniqueConstraint(final String constraintName, final String condition)
	{
		Constraint result = (Constraint)constraintMap.get(constraintName);
		
		if(result==null)
			result = new UniqueConstraint(this, constraintName, false, condition);
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
		
	public final LastAnalyzed getLastAnalyzed()
	{
		return lastAnalyzed;
	}
		
	public final Collection getColumns()
	{
		return columnList;
	}
		
	public final Column getColumn(final String columnName)
	{
		return (Column)columnMap.get(columnName);
	}
		
	public final Collection getConstraints()
	{
		return constraintList;
	}
		
	public final Constraint getConstraint(final String constraintName)
	{
		return (Constraint)constraintMap.get(constraintName);
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
			final Column column = (Column)i.next();
			column.finish();
			cumulativeColor = Math.max(cumulativeColor, column.cumulativeColor);
		}

		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final Constraint constraint = (Constraint)i.next();
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
			
			final Column column = (Column)i.next();
			bf.append(protectName(column.name)).
				append(' ').
				append(column.getType());
		}
		
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
			((Constraint)i.next()).createInTable(bf);

		bf.append(')');

		// TODO: may be this should be done using this.options
		driver.appendTableCreateStatement(bf);

		if(options!=null)
		{
			bf.append(' ').
				append(options);
		}
			
		//System.out.println("createTable:"+bf.toString());
		if(defensive)
		{
			try
			{
				executeSQL(bf.toString());
			}
			catch(SQLRuntimeException e)
			{
				// ignore it in defensive mode
			}
		}
		else
			executeSQL(bf.toString());
			
	}
	
	final void createNonForeignKeyConstraints()
	{
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final Constraint constraint = (Constraint)i.next();
			if(constraint instanceof CheckConstraint ||
					constraint instanceof UniqueConstraint ||
					constraint instanceof PrimaryKeyConstraint)
				constraint.create();
		}
	}
	
	final void createForeignKeyConstraints()
	{
		//System.out.println("createForeignKeyConstraints:"+bf);

		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final Constraint constraint = (Constraint)i.next();
			//System.out.println("createForeignKeyConstraints("+column+"):"+bf);
			if(constraint instanceof ForeignKeyConstraint)
				constraint.create();
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

		if(defensive)
		{
			try
			{
				executeSQL(bf.toString());
			}
			catch(SQLRuntimeException e)
			{
				// ignore it in defensive mode
			}
		}
		else
			executeSQL(bf.toString());

	}
	
	final void dropNonForeignKeyConstraints()
	{
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final Constraint constraint = (Constraint)i.next();
			if(constraint instanceof CheckConstraint ||
					constraint instanceof UniqueConstraint ||
					constraint instanceof PrimaryKeyConstraint)
				constraint.drop();
		}
	}
	
	final void dropForeignKeyConstraints() 
	{
		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final Constraint constraint = (Constraint)i.next();
			//System.out.println("dropForeignKeyConstraints("+column+")");
			if(constraint instanceof ForeignKeyConstraint)
				constraint.drop();
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
