package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class Report extends ReportNode
{
	final Database database;
	private final HashMap tableMap = new HashMap();
	private final ArrayList tableList = new ArrayList();
	
	Report(final Database database, final List modelTables)
	{
		this.database = database;
		for(Iterator i = modelTables.iterator(); i.hasNext(); )
		{
			final com.exedio.cope.lib.Table modelTable = (com.exedio.cope.lib.Table)i.next();
			final ReportTable reportTable = new ReportTable(this, modelTable);
			if(tableMap.put(modelTable.id, reportTable)!=null)
				throw new RuntimeException();
			tableList.add(reportTable);
	
			for(Iterator j = modelTable.getAllColumns().iterator(); j.hasNext(); )
			{
				final Column column = (com.exedio.cope.lib.Column)j.next();
				final ReportColumn reportColumn = reportTable.notifyRequiredColumn(column);
				
				if(column.primaryKey)
					reportTable.notifyRequiredConstraint(column.getPrimaryKeyConstraintID());
				else if(column.notNull)
					reportTable.notifyRequiredConstraint(column.getNotNullConstraintID());
					
				if(column instanceof StringColumn)
				{
					final StringColumn stringColumn = (StringColumn)column;

					if(stringColumn.minimumLength>0)
						reportTable.notifyRequiredConstraint(stringColumn.getMinimumLengthConstraintID());

					if(stringColumn.maximumLength!=Integer.MAX_VALUE)
						reportTable.notifyRequiredConstraint(stringColumn.getMaximumLengthConstraintID());
				}
				else if(column instanceof IntegerColumn)
				{
					final IntegerColumn intColumn = (IntegerColumn)column;
					if(intColumn.allowedValues!=null)
						reportTable.notifyRequiredConstraint(intColumn.getAllowedValuesConstraintID());

					if(intColumn instanceof ItemColumn)
					{
						final ItemColumn itemColumn = (ItemColumn)intColumn;
						reportTable.notifyRequiredConstraint(itemColumn.integrityConstraintName);
					}
				}
			}
			for(Iterator j = modelTable.getUniqueConstraints().iterator(); j.hasNext(); )
			{
				final UniqueConstraint uniqueConstraint = (UniqueConstraint)j.next();
				reportTable.notifyRequiredConstraint(uniqueConstraint.getID());
			}
		}
	}

	final ReportTable notifyExistentTable(final String tableName)
	{
		ReportTable result = (ReportTable)tableMap.get(tableName);
		if(result==null)
		{
			result = new ReportTable(this, tableName);
			tableMap.put(tableName, result);
			tableList.add(result);
		}
		else
			result.notifyExists();

		return result;
	}
	
	public ReportTable getTable(final String name)
	{
		return (ReportTable)tableMap.get(name);
	}
	
	public Collection getTables()
	{
		return tableList;
	}
	
	void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();
		
		particularColor = COLOR_OK;

		cumulativeColor = particularColor;
		for(Iterator i = tableList.iterator(); i.hasNext(); )
		{
			final ReportTable table = (ReportTable)i.next();
			table.finish();
			cumulativeColor = Math.max(cumulativeColor, table.cumulativeColor);
		}
	}

}
