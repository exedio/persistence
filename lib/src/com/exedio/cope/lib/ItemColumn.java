
package com.exedio.cope.lib;

final class ItemColumn extends IntegerColumn
{
	static final int SYNTETIC_PRIMARY_KEY_PRECISION = 10;

	final Class targetTypeClass;
	final String integrityConstraintName;

	ItemColumn(final Table table, final String id,
					  final boolean notNull,
					  final Class targetTypeClass, final String integrityConstraintName)
	{
		super(table, id, notNull, SYNTETIC_PRIMARY_KEY_PRECISION, false, null);
		if(targetTypeClass==null)
			throw new RuntimeException();
		if(integrityConstraintName==null)
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = integrityConstraintName;
	}

	/**
	 * Creates a primary key column with a foreign key contraint.
	 */	
	ItemColumn(final Table table, final Class targetTypeClass)
	{
		super(table);
		if(targetTypeClass==null)
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = table.id+"SUP";
	}

	String getForeignTableNameProtected()
	{
		if(targetTypeClass!=null)
			return Type.findByJavaClass(targetTypeClass).table.protectedID;
		else
			return null; 
	}
	
}
