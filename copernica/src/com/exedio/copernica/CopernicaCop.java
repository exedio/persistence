package com.exedio.copernica;

import java.util.HashMap;
import java.util.Map;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;


abstract class CopernicaCop extends Cop implements RequestCache
{
	static final String LANGUAGE = "l";

	static final String TYPE = "t";
	static final String ORDER_ASCENDING = "oa";
	static final String ORDER_DESCENDING = "od";
	static final String START = "st";
	static final String COUNT = "ct";
	
	final static String ITEM = "i";


	final CopernicaProvider provider;
	final Language language;

	CopernicaCop(final CopernicaProvider provider, final Language language)
	{
		super("copernica.jsp"); // TODO: make the jsp name flexible or retrieve it automatically
		this.language = language;
		this.provider = provider;
		if(language!=null)
			addParameter(LANGUAGE, language.getCopernicaID());
	}
	
	abstract CopernicaCop switchLanguage(Language newLanguage);
	abstract boolean isType(final Type type);
	abstract String getTitle();

	final TypeCop toType(final Type newType)
	{
		return new TypeCop(provider, language, newType);
	}
	
	final ItemCop toItem(final Item newItem)
	{
		return new ItemCop(provider, language, newItem);
	}
	
	static final CopernicaCop getCop(final CopernicaProvider provider, final Map parameterMap)
	{	
		final Model model = provider.getModel();
		final String typeID = getParameter(parameterMap, TYPE);
		final String itemID = getParameter(parameterMap, ITEM);
		final String langID = getParameter(parameterMap, LANGUAGE);
		
		final Language language = (langID!=null) ? provider.findLanguageByID(langID) : null;
		if(typeID!=null)
		{
			return TypeCop.getCop(provider, language, typeID, parameterMap);
		}
		else if(itemID!=null)
		{
			return ItemCop.getCop(provider, language, itemID);
		}
		else
			return new EmptyCop(provider, language);
	}
	
	private String nullName = null;
	//private int nullNameMisses = 0;
	public String getDisplayNameNull()
	{
		if(nullName==null)
		{
			//nullNameMisses++;
			nullName = provider.getDisplayNameNull(language);
		}
		
		return nullName;
	}

	private String onName = null;
	//private int onNameMisses = 0;
	public String getDisplayNameOn()
	{
		if(onName==null)
		{
			//onNameMisses++;
			onName = provider.getDisplayNameOn(language);
		}
		
		return onName;
	}
	
	private String offName = null;
	//private int offNameMisses = 0;
	public String getDisplayNameOff()
	{
		if(offName==null)
		{
			//offNameMisses++;
			offName = provider.getDisplayNameOff(language);
		}
		
		return offName;
	}

	private HashMap itemDisplayNames = null;
	//private int itemDisplayNamesHits = 0;
	//private int itemDisplayNamesMisses = 0;
	
	public String getDisplayName(final Language displayLanguage/* TODO: remove language */, final Item item)
	{
		if((language==null&&displayLanguage==null) || (language!=null&&language.equals(displayLanguage)))
		{
			if(itemDisplayNames==null)
			{
				itemDisplayNames = new HashMap();
			}
			else
			{
				final String cachedResult = (String)itemDisplayNames.get(item);
				if(cachedResult!=null)
				{
					//itemDisplayNamesHits++;
					return cachedResult;
				}
			}

			//itemDisplayNamesMisses++;
			final String result = provider.getDisplayName(this, displayLanguage, item);
			itemDisplayNames.put(item, result);
			return result;
		}
		else
		{
			//itemDisplayNamesMisses++;
			return provider.getDisplayName(this, displayLanguage, item);
		}
	}
	
	// TODO: the same for enumeration values
	
	void log()
	{
		//System.out.println("itemDisplayNames: ("+itemDisplayNamesMisses+"/"+itemDisplayNamesHits+")");
		//System.out.println("nullNameMisses: "+nullNameMisses+", onNameMisses: "+onNameMisses+", offNameMisses: "+offNameMisses);
	}
}
