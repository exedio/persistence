package com.exedio.copernica;

import com.exedio.cope.lib.DatabaseLibTest;

public class CopernicaTestProvider extends TransientCopernicaProvider
{
	private static final Object[] types = DatabaseLibTest.types;
	
	public CopernicaTestProvider()
	{
		setTransientLanguages(
			new TransientLanguage[]{
				new TransientLanguage("de"),
				new TransientLanguage("en"),
			}
		);
	}

	public void initializeExampleSystem()
	{
		DatabaseLibTest.initializeExampleSystem();
	}

}
