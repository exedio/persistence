package com.exedio.copernica;

import com.exedio.cope.lib.DatabaseLibTest;

public class CopernicaTestProvider extends TransientCopernicaProvider
{
	private static final Object[] types = DatabaseLibTest.types;
	
	public CopernicaTestProvider()
	{
		final TransientLanguage de = new TransientLanguage("de");
		final TransientLanguage en = new TransientLanguage("en");

		de.putName(de, "Deutsch");
		de.putName(en, "German");
		en.putName(de, "Englisch");
		en.putName(en, "English");

		setTransientLanguages(
			new TransientLanguage[]{
				de,
				en,
			}
		);
	}

	public void initializeExampleSystem()
	{
		DatabaseLibTest.initializeExampleSystem();
	}

}
