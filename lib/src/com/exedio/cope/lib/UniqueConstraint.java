
package com.exedio.cope.lib;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.pattern.Qualifier;
import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualCondition;

public final class UniqueConstraint extends TypeComponent
{
	
	private final ObjectAttribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	private Qualifier qualifier;
	private Type type;
	private String id;
	private String databaseID;

	private UniqueConstraint(final ObjectAttribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		for(int i = 0; i<uniqueAttributes.length; i++)
			if(uniqueAttributes[i]==null)
				throw new RuntimeException(String.valueOf(i));
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute)
	{
		this(new ObjectAttribute[]{uniqueAttribute});
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute, ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2)
	{
		this(new ObjectAttribute[]{uniqueAttribute1, uniqueAttribute2});
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute, ObjectAttribute, ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2, final ObjectAttribute uniqueAttribute3)
	{
		this(new ObjectAttribute[]{uniqueAttribute1, uniqueAttribute2, uniqueAttribute3});
	}
	
	/**
	 * @return a list of {@link ObjectAttribute}s.
	 */
	public final List getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	
	public Qualifier getQualifier()
	{
		return qualifier;
	}
	
	void setQualifier(final Qualifier qualifier)
	{
		if(this.qualifier!=null)
			throw new RuntimeException("unique constraint can have at most one qualifier");

		this.qualifier = qualifier;
	}
	
	public final void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		if(this.type!=null)
			throw new RuntimeException();
		if(this.id!=null)
			throw new RuntimeException();
		if(databaseID!=null)
			throw new RuntimeException();

		this.type = type;
		this.id = name.intern();
	}

	final void materialize(final Database database)
	{
		if(this.type==null)
			throw new RuntimeException();
		if(this.id==null)
			throw new RuntimeException();
		if(this.databaseID!=null)
			throw new RuntimeException();

		this.databaseID = database.trimName(type.getID()+"_"+id+"_Unq").intern();
		database.addUniqueConstraint(databaseID, this);
	}

	public final String getID()
	{
		if(id==null)
			throw new RuntimeException();
			
		return id;
	}
	
	public final Type getType()
	{
		if(type==null)
			throw new RuntimeException();
			
		return type;
	}

	public final String getDatabaseID()
	{
		if(databaseID==null)
			throw new RuntimeException();
			
		return databaseID;
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		//buf.append(super.toString());
		buf.append("unique(");
		buf.append(uniqueAttributes[0].getName());
		for(int i = 1; i<uniqueAttributes.length; i++)
		{
			buf.append(',');
			buf.append(uniqueAttributes[i].getName());
		}
		buf.append(')');
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
	public final Item searchUnique(final Object[] values)
	{
		// TODO: search nativly for unique constraints
		final List attributes = getUniqueAttributes();
		if(attributes.size()!=values.length)
			throw new RuntimeException();

		final Iterator attributeIterator = attributes.iterator();
		final Condition[] conditions = new Condition[attributes.size()];
		for(int j = 0; attributeIterator.hasNext(); j++)
			conditions[j] = new EqualCondition((ObjectAttribute)attributeIterator.next(), values[j]);

		return getType().searchUnique(new AndCondition(conditions));
	}
	
}
