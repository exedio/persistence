package com.exedio.copernica;

public interface CopernicaUser
{
	/**
	 * Returns the name of this user to be displayed in the web application.
	 */
	public String getCopernicaName();

	/**
	 * Returns, whether the given password authenticates this user.
	 */
	public boolean checkCopernicaPassword(String actualPassword);

}
