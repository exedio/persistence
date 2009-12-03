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

package com.exedio.cope;

import junit.framework.TestCase;

public class FeaturesTest extends TestCase
{
	private Features features;
	private java.lang.reflect.Field annotationSource;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		features = new Features();
		annotationSource = getClass().getDeclaredField("annotationSource");
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		features = null;
		annotationSource = null;
		super.tearDown();
	}
	
	public void testIt()
	{
		final BooleanField zick = new BooleanField();
		features.put("zick", zick);
		final BooleanField zack = new BooleanField();
		features.put("zack", zack);
		final BooleanField zock = new BooleanField();
		features.put("zock", zock, annotationSource);
		
		try
		{
			features.put(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			features.put("zick", null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("feature", e.getMessage());
		}
	}
	
	public void testAnnotation()
	{
		final BooleanField zick = new BooleanField();
		features.put("zick", zick, annotationSource);
		final BooleanField zack = new BooleanField();
		features.put("zack", zack, annotationSource);
		
		try
		{
			features.put("zick", zick, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("annotationSource", e.getMessage());
		}
	}
	
	public void testDuplicateName()
	{
		final BooleanField zick = new BooleanField();
		features.put("zick", zick);
		final BooleanField zack = new BooleanField();
		try
		{
			features.put("zick", zack);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("already contains the name >zick<", e.getMessage());
		}
		
		features.clear();
		features.put("zick", zack);
	}
	
	public void testDuplicateFeature()
	{
		final BooleanField zick = new BooleanField();
		features.put("zick", zick);
		try
		{
			features.put("zack", zick);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("already contains the feature >" + zick.toString() + "<", e.getMessage());
		}
	}
	
	public void testDuplicateFeatureWithAnnotation()
	{
		final BooleanField zick = new BooleanField();
		features.put("zick", zick, annotationSource);
		try
		{
			features.put("zack", zick, annotationSource);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("already contains the feature >" + zick.toString() + "<", e.getMessage());
		}
		
		features.clear();
		features.put("zack", zick, annotationSource);
	}
}
