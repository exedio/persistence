
package com.exedio.cope.lib;

final class ItemColumn extends IntegerColumn
{
	static final int SYNTETIC_PRIMARY_KEY_PRECISION = 10;

	final Class targetTypeClass;
	final String integrityConstraintName;

	ItemColumn(final Type type, final String trimmedName,
					  final boolean notNull,
					  final Class targetTypeClass, final String integrityConstraintName)
	{
		super(type, trimmedName, notNull, SYNTETIC_PRIMARY_KEY_PRECISION);
		if(targetTypeClass==null)
			throw new RuntimeException();
		if(integrityConstraintName==null)
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = integrityConstraintName;
	}

	String getForeignTableNameProtected()
	{
		if(targetTypeClass!=null)
			return Type.getType(targetTypeClass.getName()).protectedName;
		else
			return null; 
	}
	
}
