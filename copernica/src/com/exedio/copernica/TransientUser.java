package com.exedio.copernica;


public class TransientUser implements User
{
	final String id;
	private final String password; 
	private final String name;
	
	public TransientUser(final String id, final String password, final String name)
	{
		this.id = id;
		this.password = password;
		this.name = name;
	}
	
	public String getCopernicaName()
	{
		return name;
	}
	
	public boolean checkPassword(final String actualPassword)
	{
		return this.password.equals(actualPassword);
	}

}
