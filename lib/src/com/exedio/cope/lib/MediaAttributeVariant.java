package com.exedio.cope.lib;


public final class MediaAttributeVariant
{
	final MediaAttribute attribute;

	public MediaAttributeVariant(final MediaAttribute attribute)
	{
		this.attribute = attribute;
	}
	
	public MediaAttribute getAttribute()
	{
		return attribute;
	}

	// second initialization phase ---------------------------------------------------

	private Type type;
	private String name;

	final void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		if(this.type!=null)
			throw new RuntimeException();
		if(this.name!=null)
			throw new RuntimeException();

		this.type = type;
		final String prefix = attribute.getName();
		if(name.startsWith(prefix))
			this.name = name.substring(prefix.length());
		else
			this.name = name;
	}
	
	public final Type getType()
	{
		if(this.type==null)
			throw new RuntimeException();

		return type;
	}
	
	public final String getName()
	{
		if(this.type==null)
			throw new RuntimeException();

		return name;
	}
	
}
