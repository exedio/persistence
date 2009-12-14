/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.junit;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.util.ModificationListener;

/**
 * An abstract test case class for tests creating/using some persistent data.
 * @author Ralf Wiebicke
 */
public abstract class CopeModelTest extends CopeAssert
{
	static Model createdSchema = null;
	private static boolean registeredDropSchemaHook = false;
	private static Object lock = new Object();
	
	protected final Model model;
	
	protected CopeModelTest(final Model model)
	{
		this.model = model;
	}
	
	/**
	 * Override this method to provide your own connect properties
	 * to method {@link #setUp()} for connecting.
	 */
	protected ConnectProperties getConnectProperties()
	{
		return new ConnectProperties(ConnectProperties.getSystemPropertySource());
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		synchronized(lock)
		{
			if(createdSchema!=model)
			{
				if(createdSchema!=null)
				{
					createdSchema.dropSchema();
					createdSchema.disconnect();
					createdSchema = null;
				}
				
				model.connect(getConnectProperties());
				model.createSchema();
				createdSchema = model;
			}
		}
		model.startTransaction("tx:" + getClass().getName());
		model.checkEmptySchema();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		model.rollbackIfNotCommitted();
		model.deleteSchema();
		for(final ModificationListener ml : model.getModificationListeners())
			model.removeModificationListener(ml);
		synchronized(lock)
		{
			if(!registeredDropSchemaHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
					public void run()
					{
						if(createdSchema!=null)
						{
							createdSchema.dropSchema();
							createdSchema.disconnect();
							createdSchema = null;
						}
					}
				}));
				registeredDropSchemaHook = true;
			}
		}
		model.flushSequences();
		super.tearDown();
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated
	 * Not needed anymore, since {@link #tearDown()}
	 * deletes the contents of the database.
	 * Does not do anything.
	 */
	@Deprecated
	protected final <I extends Item> I deleteOnTearDown(final I item)
	{
		return item;
	}
}
