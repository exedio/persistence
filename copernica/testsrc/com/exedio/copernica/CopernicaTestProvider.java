package com.exedio.copernica;

import com.exedio.cope.lib.DatabaseLibTest;
import com.exedio.cope.lib.Model;

public class CopernicaTestProvider extends TransientCopernicaProvider
{
	
	private final Model model;
	
	public CopernicaTestProvider()
	{
		this.model = DatabaseLibTest.model;
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

	public Model getModel()
	{
		return model;
	}

	public void initializeExampleSystem()
	{
		DatabaseLibTest.initializeExampleSystem();
	}

}
