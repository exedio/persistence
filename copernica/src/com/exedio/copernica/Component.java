
package com.exedio.copernica;

public interface Component
{
	/**
	 * Returns a string unique for all instances.
	 * Is used by the web application as a persistent qualifier for
	 * building urls and form values.
	 */
	public String getCopernicaID();
	
	/**
	 * Returns the name of this component to be displayed to the user.
	 */
	public String getCopernicaName(CopernicaLanguage displayLanguage);

	/**
	 * Returns the url of an icon suitable to display with or instead of
	 * the display name.
	 */
	public String getCopernicaIconURL();

}
