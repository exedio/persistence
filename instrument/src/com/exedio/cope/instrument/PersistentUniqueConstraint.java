
package com.exedio.cope.instrument;

final class PersistentUniqueConstraint
{
	final String name;
	final String camelCaseName;
	final int modifier;
	final CopeAttribute[] persistentAttributes;
	
	/**
	 * For constraints covering more than one attribute.
	 */
	PersistentUniqueConstraint(final JavaAttribute javaAttribute, final CopeAttribute[] persistentAttributes)
	{
		this.name = javaAttribute.name;
		this.camelCaseName = javaAttribute.getCamelCaseName();
		this.modifier = javaAttribute.modifier;
		this.persistentAttributes = persistentAttributes;
	}
	
	/**
	 * For constraints covering exactly one attribute.
	 */
	PersistentUniqueConstraint(final CopeAttribute persistentAttribute)
	{
		this.name = persistentAttribute.getName();
		this.camelCaseName = persistentAttribute.getCamelCaseName();
		this.modifier = persistentAttribute.javaAttribute.modifier;
		this.persistentAttributes = new CopeAttribute[]{persistentAttribute};
	}

}
