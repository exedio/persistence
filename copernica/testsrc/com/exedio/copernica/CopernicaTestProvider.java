package com.exedio.copernica;

import com.exedio.cope.lib.DatabaseLibTest;
import com.exedio.cope.lib.Model;

public class CopernicaTestProvider extends TransientCopernicaProvider
{
	
	private final Model model;
	
	public CopernicaTestProvider()
	{
		this.model = DatabaseLibTest.model;
		final TransientLanguage de = new TransientLanguage("de", "leer", "ein", "aus");
		final TransientLanguage en = new TransientLanguage("en", "empty", "on", "off");

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
		
		final TransientUser admin = new TransientUser("admin", "nimda", "Sir Administrator");
		final TransientUser user = new TransientUser("user", "resu", "Mister User");
		setTransientUsers(
			new TransientUser[]{
				admin,
				user,
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
