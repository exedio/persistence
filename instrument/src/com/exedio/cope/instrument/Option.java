package com.exedio.cope.instrument;

import java.util.HashMap;


class Option
{
	// never to be instantiated
	private Option()
	{}

	static final int getOption(final String optionString)
			throws InjectorParseException
	{
		if(optionString==null)
			return Option.AUTO;
		else
		{
			final Integer setterOptionObject = (Integer)options.get(optionString);
			if(setterOptionObject==null)
				throw new InjectorParseException("invalid @cope-setter value "+optionString);
			return setterOptionObject.intValue();
		}
	}

	private static final HashMap options = new HashMap();
	
	public static final int NONE = 0;
	public static final int AUTO = 1;
	public static final int PRIVATE = 2;
	public static final int PROTECTED = 3;
	public static final int PACKAGE = 4;
	public static final int PUBLIC = 5;
	
	static
	{
		options.put("none", new Integer(NONE));
		options.put("private", new Integer(PRIVATE));
		options.put("protected", new Integer(PROTECTED));
		options.put("package", new Integer(PACKAGE));
		options.put("public", new Integer(PUBLIC));
	}
	
}
