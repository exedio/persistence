
package com.exedio.cope.instrument;

final class PersistentUniqueConstraint
{
	final String name;
	final String camelCaseName;
	final int modifier;
	final PersistentAttribute[] persistentAttributes;
	
	/**
	 * For constraints covering more than one attribute.
	 */
	PersistentUniqueConstraint(final JavaAttribute javaAttribute, final PersistentAttribute[] persistentAttributes)
	{
		this.name = javaAttribute.name;
		this.camelCaseName = javaAttribute.getCamelCaseName();
		this.modifier = javaAttribute.modifier;
		this.persistentAttributes = persistentAttributes;
	}
	
	/**
	 * For constraints covering exactly one attribute.
	 */
	PersistentUniqueConstraint(final PersistentAttribute persistentAttribute)
	{
		this.name = persistentAttribute.getName();
		this.camelCaseName = persistentAttribute.getCamelCaseName();
		this.modifier = persistentAttribute.javaAttribute.modifier;
		this.persistentAttributes = new PersistentAttribute[]{persistentAttribute};
	}

}
