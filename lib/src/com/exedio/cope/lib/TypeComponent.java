
package com.exedio.cope.lib;

// TODO make non-public
public abstract class TypeComponent
{
	boolean initialized = false;
	
	/**
	 * Is called in the constructor of the containing type.
	 */
	public abstract void initialize(final Type type, final String name);
}
