
package com.exedio.cope.lib;

final class ItemColumn extends IntegerColumn
{
	static final int SYNTETIC_PRIMARY_KEY_PRECISION = 10;

	final Class targetTypeClass;
	final String integrityConstraintName;

	ItemColumn(final Type type, final String id,
					  final boolean notNull,
					  final Class targetTypeClass, final String integrityConstraintName)
	{
		super(type, id, notNull, SYNTETIC_PRIMARY_KEY_PRECISION, false, null);
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
			return Type.findByJavaClass(targetTypeClass).protectedID;
		else
			return null; 
	}
	
}
