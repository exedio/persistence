package com.exedio.cope.instrument;

import java.util.List;

final class CopeQualifier
{
	final String name;
	final CopeClass qualifierClass;
	final CopeAttribute keyAttribute;

	final String parent;
	final String key;
	final String qualifyUnique;

	public CopeQualifier(final String name, final CopeClass copeClass, final List initializerArguments)
		throws InjectorParseException
	{
		this.name = name;
		if(initializerArguments.size()!=3)
			throw new InjectorParseException("Qualifier must have 3 arguments, but has "+initializerArguments);
		this.parent = (String)initializerArguments.get(0);
		this.key = (String)initializerArguments.get(1);
		this.qualifyUnique = (String)initializerArguments.get(2);

		final int qualifyUniqueDot = this.parent.indexOf('.');
		if(qualifyUniqueDot<0)
			throw new InjectorParseException("Qualifier must have dot, but is "+this.parent);
		final String qualifierClassString = this.parent.substring(0, qualifyUniqueDot);

		//System.out.println("--------- qualifierClassString: "+qualifierClassString);
		//Sstem.out.println("--------- key: "+key);
		//System.out.println("--------- qualifyUnique: "+qualifyUnique);
		this.qualifierClass = copeClass.javaClass.file.repository.getCopeClass(qualifierClassString);
		//System.out.println("--------- qualifierClass: "+qualifierClass.javaClass.name);
		
		final int keyDot = this.key.indexOf('.');
		if(keyDot<0)
			throw new InjectorParseException("Qualifier must have dot, but is "+this.key);
		final String keyString = this.key.substring(keyDot+1);
		//System.out.println("--------- keyString: "+keyString);
		
		this.keyAttribute = qualifierClass.getCopeAttribute(keyString);
		if(this.keyAttribute==null)
			throw new RuntimeException(keyString);

		copeClass.addQualifier(this);
	}

}
