
package com.exedio.cope.instrument;

final class CopeUniqueConstraint
{
	final String name;
	final String camelCaseName;
	final int modifier;
	final CopeAttribute[] copeAttributes;
	
	/**
	 * For constraints covering more than one attribute.
	 */
	CopeUniqueConstraint(final JavaAttribute javaAttribute, final CopeAttribute[] copeAttributes)
	{
		this.name = javaAttribute.name;
		this.camelCaseName = javaAttribute.getCamelCaseName();
		this.modifier = javaAttribute.modifier;
		this.copeAttributes = copeAttributes;
	}
	
	/**
	 * For constraints covering exactly one attribute.
	 */
	CopeUniqueConstraint(final CopeAttribute copeAttribute)
	{
		this.name = copeAttribute.getName();
		this.camelCaseName = copeAttribute.getCamelCaseName();
		this.modifier = copeAttribute.javaAttribute.modifier;
		this.copeAttributes = new CopeAttribute[]{copeAttribute};
	}

}
