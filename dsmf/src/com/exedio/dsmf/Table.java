/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public final class Table extends Node
{
	private final Schema schema;
	final String name;
	final String options;
	private final boolean required;
	private boolean exists;
	private LastAnalyzed lastAnalyzed = null;
	
	private boolean defensive = false;

	private final HashMap<String, Column> columnMap = new HashMap<String, Column>();
	private final ArrayList<Column> columnList = new ArrayList<Column>();

	private final HashMap<String, Constraint> constraintMap = new HashMap<String, Constraint>();
	private final ArrayList<Constraint> constraintList = new ArrayList<Constraint>();

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
			throw new RuntimeException("duplicate column name in table " + name + ": " + column.name);
		columnList.add(column);
	}
	
	final void register(final Constraint constraint)
	{
		if(constraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException("duplicate constraint name in table " + name + ": " + constraint.name);
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
		Column result = columnMap.get(columnName);
		if(result==null)
			result = new Column(this, columnName, existingType, false);
		else
			result.notifyExists(existingType);

		return result;
	}
	
	final Constraint notifyExistentCheckConstraint(final String constraintName, final String condition)
	{
		Constraint result = constraintMap.get(constraintName);
		
		if(result==null)
			result = new CheckConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);

		return result;
	}
	
	final Constraint notifyExistentPrimaryKeyConstraint(final String constraintName)
	{
		Constraint result = constraintMap.get(constraintName);

		if(result==null)
			result = new PrimaryKeyConstraint(this, constraintName, false, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final Constraint notifyExistentForeignKeyConstraint(final String constraintName)
	{
		Constraint result = constraintMap.get(constraintName);
		
		if(result==null)
			result = new ForeignKeyConstraint(this, constraintName, false, null, null, null);
		else
			result.notifyExists();
		
		return result;
	}
	
	final Constraint notifyExistentUniqueConstraint(final String constraintName, final String condition)
	{
		Constraint result = constraintMap.get(constraintName);
		
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
		
	public final Collection<Column> getColumns()
	{
		return columnList;
	}
		
	public final Column getColumn(final String columnName)
	{
		return columnMap.get(columnName);
	}
		
	public final Collection<Constraint> getConstraints()
	{
		return constraintList;
	}
		
	public final Constraint getConstraint(final String constraintName)
	{
		return constraintMap.get(constraintName);
	}
		
	@Override
	void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		final String error;
		final Color particularColor;
		if(!exists)
		{
			error = "MISSING !!!";
			particularColor = Color.ERROR;
		}
		else if(!required)
		{
			error = "not used";
			particularColor = Color.WARNING;
		}
		else
		{
			error = null;
			particularColor = Color.OK;
		}
				
		this.error = error;
		this.particularColor = particularColor;
		cumulativeColor = particularColor;
			
		if(lastAnalyzed!=null)
		{
			lastAnalyzed.finish();
			cumulativeColor = cumulativeColor.max(lastAnalyzed.cumulativeColor);
		}
			
		for(final Column column : columnList)
		{
			column.finish();
			cumulativeColor = cumulativeColor.max(column.cumulativeColor);
		}

		for(final Constraint constraint : constraintList)
		{
			constraint.finish();
			cumulativeColor = cumulativeColor.max(constraint.cumulativeColor);
		}
	}
	
	public final void create()
	{
		create(null);
	}
	
	public final void create(final StatementListener listener)
	{
		final StringBuffer bf = new StringBuffer();

		bf.append("create table ").
			append(protectName(name)).
			append('(');

		boolean firstColumn = true;
		for(final Column column : columnList)
		{
			if(firstColumn)
				firstColumn = false;
			else
				bf.append(',');
			
			bf.append(protectName(column.name)).
				append(' ').
				append(column.getType());
		}
		
		for(final Constraint c : constraintList)
		{
			if(!c.secondPhase)
				c.createInTable(bf);
		}

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
				executeSQL(bf.toString(), listener);
			}
			catch(SQLRuntimeException e)
			{
				// ignore it in defensive mode
			}
		}
		else
			executeSQL(bf.toString(), listener);
			
	}
	
	public final void drop()
	{
		drop(null);
	}
	
	public final void drop(final StatementListener listener)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("drop table ").
			append(protectName(name));

		if(defensive)
		{
			try
			{
				executeSQL(bf.toString(), listener);
			}
			catch(SQLRuntimeException e)
			{
				// ignore it in defensive mode
			}
		}
		else
			executeSQL(bf.toString(), listener);

	}
	
	final void createConstraints(final int mask, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(constraint.isSupported() && constraint.matchesMask(mask) && constraint.secondPhase==secondPhase)
				constraint.create(listener);
		}
	}
	
	final void dropConstraints(final int mask, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(constraint.isSupported() && constraint.matchesMask(mask) && constraint.secondPhase==secondPhase)
				constraint.drop(listener);
		}
	}
	
	final void tearDownConstraints(final int mask, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(constraint.matchesMask(mask) && constraint.secondPhase==secondPhase)
			{
				try
				{
					constraint.drop(listener);
				}
				catch(SQLRuntimeException e2)
				{
					// ignored in teardown
					//System.err.println("failed:"+e2.getMessage());
				}
			}
		}
	}
	
	public final void renameTo(final String newName)
	{
		renameTo(newName, null);
	}
	
	public final void renameTo(final String newName, final StatementListener listener)
	{
		executeSQL(driver.renameTable(protectName(name), protectName(newName)), listener);
	}

	public final void analyze(final StatementListener listener)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("analyze table ").
			append(protectName(name)).
			append(" compute statistics");

		//System.out.println("analyzeTable:"+bf);
		executeSQL(bf.toString(), listener);
	}
	
	public final void checkUnsupportedConstraints()
	{
		for(final Constraint c : getConstraints())
			if(!c.isSupported())
			{
				final int count = c.check();
				if(count!=0)
					throw new RuntimeException("constraint violated for " + c + " on " + count + " tuples.");
			}
	}

	@Override
	public final String toString()
	{
		return name;
	}
	
}
