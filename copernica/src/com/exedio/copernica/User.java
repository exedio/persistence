package com.exedio.copernica;

public interface User
{
	/**
	 * Returns the name of this user to be displayed in the web application.
	 */
	public String getCopernicaName();

	/**
	 * Returns, whether the given password authenticates this user.
	 */
	// TODO: rename to checkCopernicaPassword
	public boolean checkPassword(String actualPassword);

}
