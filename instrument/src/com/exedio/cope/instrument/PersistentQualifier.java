package com.exedio.cope.instrument;

import java.util.List;

final class PersistentQualifier
{
	final String qualifierClass;
	final String parent;
	final String key;
	final String qualifyUnique;

	public PersistentQualifier(final PersistentClass persistentClass, final List initializerArguments)
		throws InjectorParseException
	{
		if(initializerArguments.size()!=3)
			throw new InjectorParseException("Qualifier must have 3 arguments, but has "+initializerArguments);
		this.parent = (String)initializerArguments.get(0);
		this.key = (String)initializerArguments.get(1);
		this.qualifyUnique = (String)initializerArguments.get(2);
		final int pos = this.parent.indexOf('.');
		if(pos<0)
			throw new InjectorParseException("Qualifier must have dot, but is "+this.parent);
		this.qualifierClass = this.parent.substring(0, pos);
		
		persistentClass.addQualifier(this);
	}

}
