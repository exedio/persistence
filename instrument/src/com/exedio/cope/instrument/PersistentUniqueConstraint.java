
package com.exedio.cope.instrument;

public final class PersistentUniqueConstraint
{
	final String name;
	final String camelCaseName;
	final int accessModifier;
	final PersistentAttribute[] persistentAttributes;
	
	/**
	 * For constraints covering more than one attribute.
	 */
	PersistentUniqueConstraint(final JavaAttribute javaAttribute, final PersistentAttribute[] persistentAttributes)
	{
		this.name = javaAttribute.getName();
		this.camelCaseName = javaAttribute.getCamelCaseName();
		this.accessModifier = javaAttribute.accessModifier;
		this.persistentAttributes = persistentAttributes;
	}
	
	/**
	 * For constraints covering exactly one attribute.
	 */
	PersistentUniqueConstraint(final PersistentAttribute persistentAttribute)
	{
		this.name = persistentAttribute.getName();
		this.camelCaseName = persistentAttribute.getCamelCaseName();
		this.accessModifier = persistentAttribute.accessModifier;
		this.persistentAttributes = new PersistentAttribute[]{persistentAttribute};
	}

}
