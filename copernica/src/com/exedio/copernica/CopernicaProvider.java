
package com.exedio.copernica;

import java.util.Collection;

import javax.servlet.ServletConfig;

import com.exedio.cope.lib.EnumValue;
import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;

public interface CopernicaProvider
{

	public Model getModel();

	/**
	 * Is called once after the constructor.
	 */
	public void initialize(ServletConfig config);
	
	/**
	 * @return a collection of {@link CopernicaLanguage languages}.
	 */
	public Collection getDisplayLanguages();
	
	/**
	 * @return a collection of {@link CopernicaCategory categories}.
	 */
	public Collection getRootCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getMainAttributes(Type type);

	/**
	 * @return a collection of {@link CopernicaSection sections}.
	 */
	public Collection getSections(Type type);
	
	public String getDisplayNameNull(CopernicaLanguage displayLanguage);

	public String getDisplayNameOn(CopernicaLanguage displayLanguage);
	
	public String getDisplayNameOff(CopernicaLanguage displayLanguage);

	public String getDisplayName(CopernicaLanguage displayLanguage, Type type);

	public String getDisplayName(CopernicaLanguage displayLanguage, Feature feature);
	
	public String getDisplayName(RequestCache cache, CopernicaLanguage displayLanguage, Item item);

	public String getDisplayName(CopernicaLanguage displayLanguage, EnumValue value);

	public String getIconURL(Type type);

	public CopernicaLanguage findLanguageByID(String copernicaID);
	
	public CopernicaUser findUserByID(String copernicaID);

	public CopernicaCategory findCategoryByID(String copernicaID);
	
	public CopernicaSection findSectionByID(String copernicaID);

}
