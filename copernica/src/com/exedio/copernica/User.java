package com.exedio.copernica;

public interface User
{
	/**
	 * Returns a string unique for all users.
	 * Is used by the web application as a persistent qualifier for
	 * building urls and form values.
	 */
	public String getCopernicaID();
	
	/**
	 * Returns the name of this user to be displayed in the web application.
	 */
	public String getCopernicaName();

	/**
	 * Returns, whether the given password authenticates this user.
	 */
	public boolean checkPassword(String actualPassword);

}
