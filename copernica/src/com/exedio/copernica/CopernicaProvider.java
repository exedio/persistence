
package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;

public interface CopernicaProvider
{

	public Model getModel();

	/**
	 * @return a collection of {@link Language languages}.
	 */
	public Collection getDisplayLanguages();
	
	/**
	 * @return a collection of {@link Category categories}.
	 */
	public Collection getRootCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getMainAttributes(Type type);

	/**
	 * @return a collection of {@link Section sections}.
	 */
	public Collection getSections(Type type);
	
	public String getDisplayName(Language displayLanguage, Type type);

	public String getDisplayName(Language displayLanguage, Feature feature);
	
	public String getDisplayName(RequestCache cache, Language displayLanguage, Item item);

	public String getDisplayName(Language displayLanguage, EnumerationValue value);

	public String getIconURL(Type type);

	public Language findLanguageByID(String copernicaID);
	
	public User findUserByID(String copernicaID);

	public Category findCategoryByID(String copernicaID);
	
	public Section findSectionByID(String copernicaID);

	public void initializeExampleSystem();
}
