
package com.exedio.cope.lib.junit;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Properties;

public abstract class CopeLibTest extends CopeAssert
{
	private static boolean createdDatabase = false;
	private static boolean registeredDropDatabaseHook = false;
	private static Object lock = new Object();

	public final Model model;
	
	protected CopeLibTest(final Model model)
	{
		this.model = model;
	}

	private final void createDatabase()
	{
		synchronized(lock)
		{
			if(!createdDatabase)
			{
				model.createDatabase();
				createdDatabase = true;
			}
		}
	}
	
	private final void dropDatabase()
	{
		synchronized(lock)
		{
			if(!registeredDropDatabaseHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
					public void run()
					{
						model.dropDatabase();
					}
				}));
				registeredDropDatabaseHook = true;
			}
		}
	}

	protected void setUp() throws Exception
	{
		model.setPropertiesInitially(new Properties());

		super.setUp();
		createDatabase();
		model.checkEmptyDatabase();
	}
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
		model.checkEmptyDatabase();
		dropDatabase();
	}
	
}
