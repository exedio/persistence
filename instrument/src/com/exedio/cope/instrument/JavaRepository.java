package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class JavaRepository
{
	private final ArrayList files = new ArrayList();
	private final HashMap copeClasses = new HashMap();

	void add(final JavaFile file)
	{
		files.add(file);
	}
	
	final List getFiles()
	{
		return files;
	}
	
	void add(final CopeClass copeClass)
	{
		final String name = JavaFile.extractClassName(copeClass.javaClass.name);
		if(copeClasses.put(name, copeClass)!=null)
			throw new RuntimeException(name);
		//System.out.println("--------- put cope class: "+name);
	}
	
	CopeClass getCopeClass(final String className)
	{
		final CopeClass result = (CopeClass)copeClasses.get(className);
		if(result==null)
			throw new RuntimeException("no cope class for "+className);
		return result;
	}

}
