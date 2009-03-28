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

package com.exedio.cope.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.History;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.XMLEncoder;

public abstract class Editor implements Filter
{
	static final String UTF8 = CopsServlet.UTF8;
	
	private final Model model;
	
	/**
	 * Subclasses must define a public no-args constructor
	 * providing the model.
	 */
	protected Editor(final Model model)
	{
		if(model==null)
			throw new NullPointerException("model");
		
		this.model = model;
	}
	
	private FilterConfig config = null;
	private boolean draftsEnabled = false;
	private Target defaultTarget = TargetLive.INSTANCE;
	private ConnectToken connectToken = null;
	private final Object connectTokenLock = new Object();
	
	public final void init(final FilterConfig config)
	{
		this.config = config;
		for(final Type<?> type : model.getTypes())
			if(type==DraftItem.TYPE) // DraftItem implies Draft because of the parent field
			{
				draftsEnabled = true;
				defaultTarget = TargetNewDraft.INSTANCE;
				break;
			}
	}
	
	private final void startTransaction(final String name)
	{
		synchronized(connectTokenLock)
		{
			if(connectToken==null)
			{
				connectToken = ServletUtil.connect(model, config, getClass().getName());
				model.reviseIfSupported();
			}
		}
		model.startTransaction(getClass().getName() + '#' + name);
	}
	
	public final void destroy()
	{
		synchronized(connectTokenLock)
		{
			if(connectToken!=null)
			{
				connectToken.returnIt();
				connectToken = null;
			}
		}
	}
	
	protected abstract Session login(String user, String password);
	
	@SuppressWarnings("unused")
	protected String getBorderButtonURL(HttpServletRequest request, HttpServletResponse response, boolean bordersEnabled)
	{
		return null;
	}
	
	protected abstract String getHideButtonURL(HttpServletRequest request, HttpServletResponse response);
	
	@SuppressWarnings("unused")
	protected String getCloseButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	@SuppressWarnings("unused")
	protected String getPreviousPositionButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	public final void doFilter(
			final ServletRequest servletRequest,
			final ServletResponse servletResponse,
			final FilterChain chain)
	throws IOException, ServletException
	{
		if(!(servletRequest instanceof HttpServletRequest))
		{
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		final HttpServletRequest request = (HttpServletRequest)servletRequest;
		
		if(LOGIN_PATH_INFO.equals(request.getPathInfo()))
		{
			servletRequest.setCharacterEncoding(UTF8);
			final HttpServletResponse response = (HttpServletResponse)servletResponse;
			final HttpSession httpSession = request.getSession(true);
			final Object anchor = httpSession.getAttribute(ANCHOR);
			
			if(anchor==null)
				doLogin(request, httpSession, response);
			else
			{
				if(request.getParameter(PREVIEW_OVERVIEW)!=null)
					doPreviewOverview(request, response, (Anchor)anchor);
				else if(request.getParameter(MEDIA_FEATURE)!=null)
					doMedia(request, response, (Anchor)anchor);
				else
					doBar(request, httpSession, response, (Anchor)anchor);
			}
			
			return;
		}

		final HttpSession httpSession = request.getSession(false);
		if(httpSession!=null)
		{
			final Object anchor = httpSession.getAttribute(ANCHOR);
			if(anchor!=null)
			{
				try
				{
					tls.set(new TL(this, request, (HttpServletResponse)servletResponse, (Anchor)anchor));
					chain.doFilter(request, servletResponse);
				}
				finally
				{
					tls.remove();
				}
			}
			else
				chain.doFilter(request, servletResponse);
		}
		else
		{
			chain.doFilter(request, servletResponse);
		}
	}
	
	private static final void redirectHome(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + '/'));
	}
	
	static final String AVOID_COLLISION = "contentEditorBar823658617";
	static final String BAR_REFERER = "referer";
	private static final String BAR_BORDERS_ON  = "borders.on";
	private static final String BAR_BORDERS_OFF = "borders.off";
	static final String BAR_CLOSE = "close";
	static final String BAR_SWITCH_TARGET = "target.switch";
	static final String BAR_SAVE_TARGET   = "target.save";
	
	static final String BAR_FEATURE = "feature";
	static final String BAR_ITEM    = "item";
	static final String BAR_TEXT    = "text";
	static final String BAR_FILE    = "file";
	static final String BAR_ITEM_FROM = "itemPrevious";
	static final String BAR_PUBLISH_NOW = "publishNow";
	
	private static final String BAR_CLOSE_IMAGE       = BAR_CLOSE       + ".x";
	private static final String BAR_BORDERS_ON_IMAGE  = BAR_BORDERS_ON  + ".x";
	private static final String BAR_BORDERS_OFF_IMAGE = BAR_BORDERS_OFF + ".x";
	
	@SuppressWarnings("deprecation")
	private static final boolean isMultipartContent(final HttpServletRequest request)
	{
		return ServletFileUpload.isMultipartContent(request);
	}
	
	static final String PREVIEW_OVERVIEW = "po";
	static final String MODIFICATION_PUBLISH = "modification.publish";
	static final String MODIFICATION_DISCARD = "modification.discard";
	static final String MODIFICATION_PERSIST = "modification.persist";
	static final String MODIFICATION_PERSIST_COMMENT = "modification.persistComment";
	static final String MODIFICATION_IDS = "id";
	static final String SAVE_TO_DRAFT = "draft.saveTo";
	static final String DRAFT_ID     = "draft.id";
	static final String DRAFT_LOAD   = "draft.load";
	static final String DRAFT_DELETE = "draft.delete";
	static final String DRAFT_NEW    = "draft.new";
	static final String DRAFT_COMMENT= "draft.comment";
	static final String TARGET_ID   = "target.id";
	static final String TARGET_OPEN = "target.load";
	
	private final void doPreviewOverview(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Anchor anchor)
	throws IOException
	{
		if(Cop.isPost(request))
		{
			final String[] idA = request.getParameterValues(MODIFICATION_IDS);
			final HashSet<String> ids = idA!=null ? new HashSet<String>(Arrays.asList(idA)) : null;
			if(request.getParameter(MODIFICATION_PUBLISH)!=null)
			{
				try
				{
					startTransaction("publishPreviews");
					for(final Iterator<Modification> i = anchor.modifications.iterator(); i.hasNext(); )
					{
						final Modification p = i.next();
						if(ids!=null && ids.contains(p.getID()))
						{
							p.publish();
							i.remove();
						}
					}
					// TODO maintain history
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(MODIFICATION_PERSIST)!=null)
			{
				try
				{
					startTransaction("persistProposals");
					final Draft parent = new Draft(anchor.user, anchor.sessionName, request.getParameter(MODIFICATION_PERSIST_COMMENT));
					for(final Iterator<Modification> i = anchor.modifications.iterator(); i.hasNext(); )
					{
						final Modification p = i.next();
						if(ids!=null && ids.contains(p.getID()))
						{
							p.saveTo(parent);
							i.remove();
						}
					}
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(SAVE_TO_DRAFT)!=null)
			{
				try
				{
					startTransaction("saveToDraft");
					final Draft parent = (Draft)model.getItem(request.getParameter(DRAFT_ID));
					for(final Iterator<Modification> i = anchor.modifications.iterator(); i.hasNext(); )
					{
						final Modification p = i.next();
						if(ids!=null && ids.contains(p.getID()))
						{
							p.saveTo(parent);
							i.remove();
						}
					}
					model.commit();
				}
				catch(NoSuchIDException e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(MODIFICATION_DISCARD)!=null)
			{
				for(final Iterator<Modification> i = anchor.modifications.iterator(); i.hasNext(); )
				{
					final Modification p = i.next();
					if(ids!=null && ids.contains(p.getID()))
						i.remove();
				}
			}
			else if(request.getParameter(DRAFT_LOAD)!=null)
			{
				try
				{
					startTransaction("loadDraft");
					final Draft draft =
						(Draft)model.getItem(request.getParameter(DRAFT_ID));
					for(final DraftItem i : draft.getItems())
						anchor.modify(
								DraftItem.newValue.get(i),
								(StringField)model.getFeature(DraftItem.feature.get(i)),
								model.getItem(DraftItem.item.get(i)));
				}
				catch(NoSuchIDException e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(DRAFT_DELETE)!=null)
			{
				try
				{
					startTransaction("deleteDraft");
					((Draft)model.getItem(request.getParameter(DRAFT_ID))).deleteCopeItem();
					model.commit();
				}
				catch(NoSuchIDException e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(DRAFT_NEW)!=null)
			{
				try
				{
					startTransaction("newDraft");
					new Draft(anchor.user, anchor.sessionName, request.getParameter(DRAFT_COMMENT));
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(TARGET_OPEN)!=null)
			{
				anchor.setTarget(getTarget(request.getParameter(TARGET_ID)));
			}
		}
		
		final StringBuilder out = new StringBuilder();
		try
		{
			startTransaction("proposal");
			final List<Draft> drafts =
				draftsEnabled
				? Draft.TYPE.search(null, Draft.date, true)
				: null;
			final ArrayList<Target> targets = new ArrayList<Target>();
			targets.add(TargetLive.INSTANCE);
			if(draftsEnabled)
			{
				for(final Draft draft : drafts)
					targets.add(new TargetDraft(draft));
				targets.add(TargetNewDraft.INSTANCE);
			}
			Preview_Jspm.writeOverview(
					out,
					request, response,
					response.encodeURL(LOGIN_URL + '?' + PREVIEW_OVERVIEW + "=t"),
					anchor.getModifications(),
					anchor.getTarget(), targets,
					draftsEnabled, drafts);
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
		
		response.setContentType("text/html; charset="+UTF8);
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Cache-Control", "max-age=0");
		response.addHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
		writeBody(out, response);
	}
	
	private final Target getTarget(final String id)
	{
		if(TargetLive.ID.equals(id))
		{
			return TargetLive.INSTANCE;
		}
		else if(TargetNewDraft.ID.equals(id))
		{
			return TargetNewDraft.INSTANCE;
		}
		else
		{
			try
			{
				startTransaction("findDraft");
				return new TargetDraft((Draft)model.getItem(id));
			}
			catch(NoSuchIDException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
	}
	
	private final void doBar(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response,
			final Anchor anchor)
	throws IOException
	{
		if(!Cop.isPost(request))
		{
			redirectHome(request, response);
			return;
		}
		
		final String referer;
		
		if(isMultipartContent(request))
		{
			final HashMap<String, String> fields = new HashMap<String, String>();
			final HashMap<String, FileItem> files = new HashMap<String, FileItem>();
			final FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(UTF8);
			try
			{
				for(Iterator<?> i = upload.parseRequest(request).iterator(); i.hasNext(); )
				{
					final FileItem item = (FileItem)i.next();
					if(item.isFormField())
						fields.put(item.getFieldName(), item.getString(UTF8));
					else
						files.put(item.getFieldName(), item);
				}
			}
			catch(FileUploadException e)
			{
				throw new RuntimeException(e);
			}
			
			final String featureID = fields.get(BAR_FEATURE);
			if(featureID==null)
				throw new NullPointerException();
			
			final Media feature = (Media)model.getFeature(featureID);
			if(feature==null)
				throw new NullPointerException(featureID);
			
			final String itemID = fields.get(BAR_ITEM);
			if(itemID==null)
				throw new NullPointerException();
			
			final FileItem file = files.get(BAR_FILE);
		
			try
			{
				startTransaction("publishFile(" + featureID + ',' + itemID + ')');
				
				final Item item = model.getItem(itemID);

				if(fields.get(BAR_PUBLISH_NOW)!=null)
				{
					for(final History history : History.getHistories(item.getCopeType()))
					{
						final History.Event event = history.createEvent(item, anchor.getHistoryAuthor(), false);
						event.createFeature(
								feature, feature.getName(),
								feature.isNull(item) ? null : ("file type=" + feature.getContentType(item) + " size=" + feature.getLength(item)),
								"file name=" + file.getName() + " type=" + file.getContentType() + " size=" + file.getSize());
					}
					
					// TODO use more efficient setter with File or byte[]
					feature.set(item, file.getInputStream(), file.getContentType());
				}
				else
				{
					anchor.modify(file, feature, item);
				}
				
				model.commit();
			}
			catch(NoSuchIDException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			
			referer = fields.get(BAR_REFERER);
		}
		else // isMultipartContent
		{
			if(request.getParameter(BAR_BORDERS_ON)!=null || request.getParameter(BAR_BORDERS_ON_IMAGE)!=null)
			{
				anchor.borders = true;
			}
			else if(request.getParameter(BAR_BORDERS_OFF)!=null || request.getParameter(BAR_BORDERS_OFF_IMAGE)!=null)
			{
				anchor.borders = false;
			}
			else if(request.getParameter(BAR_CLOSE)!=null || request.getParameter(BAR_CLOSE_IMAGE)!=null)
			{
				httpSession.removeAttribute(ANCHOR);
			}
			else if(request.getParameter(BAR_SWITCH_TARGET)!=null)
			{
				anchor.setTarget(getTarget(request.getParameter(BAR_SWITCH_TARGET)));
			}
			else if(request.getParameter(BAR_SAVE_TARGET)!=null)
			{
				try
				{
					startTransaction("saveTarget");
					anchor.getTarget().save(anchor);
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
				anchor.notifyPublishedAll();
			}
			else
			{
				final String featureID = request.getParameter(BAR_FEATURE);
				if(featureID==null)
					throw new NullPointerException();
				
				final Feature featureO = model.getFeature(featureID);
				if(featureO==null)
					throw new NullPointerException(featureID);
				
				final String itemID = request.getParameter(BAR_ITEM);
				if(itemID==null)
					throw new NullPointerException();
				
				if(featureO instanceof StringField)
				{
					final StringField feature = (StringField)featureO;
					final String value = request.getParameter(BAR_TEXT);
				
					try
					{
						startTransaction("barText(" + featureID + ',' + itemID + ')');
						
						final Item item = model.getItem(itemID);
	
						if(request.getParameter(BAR_PUBLISH_NOW)!=null)
						{
							String v = value;
							if("".equals(v))
								v = null;
							for(final History history : History.getHistories(item.getCopeType()))
							{
								final History.Event event = history.createEvent(item, anchor.getHistoryAuthor(), false);
								event.createFeature(feature, feature.getName(), feature.get(item), v);
							}
							feature.set(item, v);
							anchor.notifyPublished(feature, item);
						}
						else
						{
							anchor.modify(value, feature, item);
						}
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
				else
				{
					final IntegerField feature = (IntegerField)featureO;
					final String itemIDFrom = request.getParameter(BAR_ITEM_FROM);
					if(itemIDFrom==null)
						throw new NullPointerException();
					
					try
					{
						startTransaction("swapPosition(" + featureID + ',' + itemIDFrom + ',' + itemID + ')');
						
						final Item itemFrom = model.getItem(itemIDFrom);
						final Item itemTo   = model.getItem(itemID);
	
						final Integer positionFrom = feature.get(itemFrom);
						final Integer positionTo   = feature.get(itemTo);
						feature.set(itemFrom, feature.getMinimum());
						feature.set(itemTo,   positionFrom);
						feature.set(itemFrom, positionTo);
						
						for(final History history : History.getHistories(itemFrom.getCopeType()))
						{
							final History.Event event = history.createEvent(itemFrom, anchor.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionFrom, positionTo);
						}
						for(final History history : History.getHistories(itemTo.getCopeType()))
						{
							final History.Event event = history.createEvent(itemTo, anchor.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionTo, positionFrom);
						}
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
			}
			
			referer = request.getParameter(BAR_REFERER);
		}
		
		if(referer!=null)
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + referer));
	}
	
	static final String MEDIA_FEATURE = "mf";
	static final String MEDIA_ITEM = "mi";
	
	private final void doMedia(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Anchor anchor)
	{
		final String featureID = request.getParameter(MEDIA_FEATURE);
		if(featureID==null)
			throw new NullPointerException();
		final Media feature = (Media)model.getFeature(featureID);
		if(feature==null)
			throw new NullPointerException(featureID);
		
		final String itemID = request.getParameter(MEDIA_ITEM);
		if(itemID==null)
			throw new NullPointerException();
		
		final Item item;
		try
		{
			startTransaction("media(" + featureID + ',' + itemID + ')');
			item = model.getItem(itemID);
			model.commit();
		}
		catch(NoSuchIDException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
		
		final FileItem fi = anchor.getModification(feature, item);
		if(fi==null)
			throw new NullPointerException(featureID + '-' + itemID);
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Cache-Control", "max-age=0");
		response.addHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
		response.setContentType(fi.getContentType());
		response.setContentLength((int)fi.getSize());
		
		InputStream in = null;
		ServletOutputStream out = null;
		try
		{
			in  = fi.getInputStream();
			out = response.getOutputStream();
			
			final byte[] b = new byte[20*1024];
			for(int len = in.read(b); len>=0; len = in.read(b))
				out.write(b, 0, len);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(in!=null)
			{
				try
				{
					in.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			if(out!=null)
			{
				try
				{
					out.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	static final String LOGIN_URL = "copeLiveEdit.html";
	public static final String LOGIN_PATH_INFO = '/' + LOGIN_URL;
	static final String LOGIN_SUBMIT   = "login.submit";
	static final String LOGIN_USER     = "login.user";
	static final String LOGIN_PASSWORD = "login.password";
	
	private final void doLogin(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response)
	throws IOException
	{
		assert httpSession!=null;
		response.setContentType("text/html; charset="+UTF8);
		if(Cop.isPost(request) && request.getParameter(LOGIN_SUBMIT)!=null)
		{
			final String user = request.getParameter(LOGIN_USER);
			final String password = request.getParameter(LOGIN_PASSWORD);
			try
			{
				startTransaction("login");
				final Session session = login(user, password);
				if(session!=null)
				{
					httpSession.setAttribute(ANCHOR, new Anchor(defaultTarget, user, session, session.getName()));
					redirectHome(request, response);
				}
				else
				{
					final StringBuilder out = new StringBuilder();
					Login_Jspm.write(out, response, Editor.class.getPackage(), user);
					writeBody(out, response);
				}
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
		{
			final StringBuilder out = new StringBuilder();
			Login_Jspm.write(out, response, Editor.class.getPackage(), null);
			writeBody(out, response);
		}
	}
	
	private static final String ANCHOR = Session.class.getName();
	
	private static final class TL
	{
		final Editor filter;
		final HttpServletRequest request;
		final HttpServletResponse response;
		final Anchor anchor;
		private HashMap<IntegerField, Item> positionItems = null;
		
		TL(
				final Editor filter,
				final HttpServletRequest request,
				final HttpServletResponse response,
				final Anchor anchor)
		{
			this.filter = filter;
			this.request = request;
			this.response = response;
			this.anchor = anchor;
			
			assert filter!=null;
			assert request!=null;
			assert response!=null;
			assert anchor!=null;
		}
		
		Item registerPositionItem(final IntegerField feature, final Item item)
		{
			final Integer next = feature.get(item);
			if(next==null)
				return null;
			
			if(positionItems==null)
				positionItems = new HashMap<IntegerField, Item>();
			
			final Item result = positionItems.put(feature, item);
			if(result==null)
				return null;
			
			final Integer previous = feature.get(result);
			return (previous!=null && previous.intValue()<next.intValue()) ? result : null;
		}
	}
	
	private static final ThreadLocal<TL> tls = new ThreadLocal<TL>();
	
	public static final boolean isLoggedIn()
	{
		return tls.get()!=null;
	}
	
	public static final boolean isBordersEnabled()
	{
		final TL tl = tls.get();
		return tl!=null && tl.anchor.borders;
	}
	
	public static final Session getSession()
	{
		final TL tl = tls.get();
		return tl!=null ? tl.anchor.session : null;
	}
	
	private static final <K> Item getItem(final MapField<K, String> feature, final K key, final Item item)
	{
		return
				feature.getRelationType().searchSingletonStrict(
						feature.getKey().equal(key).and(
						Cope.equalAndCast(feature.getParent(), item)));
	}
	
	public static final <K> String edit(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		checkEdit(feature, item);
		
		return edit(
				tl,
				content,
				(StringField)feature.getValue(),
				getItem(feature, key, item));
	}
	
	public static final String edit(final String content, final StringField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		return edit(tl, content, feature, item);
	}
	
	static final String EDIT_METHOD_LINE = AVOID_COLLISION + "line";
	static final String EDIT_METHOD_FILE = AVOID_COLLISION + "file";
	static final String EDIT_METHOD_AREA = AVOID_COLLISION + "area";
	
	private static final String edit(final TL tl, final String content, final StringField feature, final Item item)
	{
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		if(!tl.anchor.borders)
		{
			final String modification = tl.anchor.getModification(feature, item);
			return (modification!=null) ? modification : content;
		}
		
		final boolean block = feature.getMaximumLength()>StringField.DEFAULT_LENGTH;
		final String savedContent = feature.get(item);
		final String pageContent;
		final String editorContent;
		final boolean modifiable;
		if(content!=null ? content.equals(savedContent) : (savedContent==null))
		{
			modifiable = true;
			final String modification = tl.anchor.getModification(feature, item);
			if(modification!=null)
				pageContent = editorContent = modification;
			else
				pageContent = editorContent = savedContent; // equals content anyway
		}
		else
		{
			modifiable = false;
			pageContent = content;
			editorContent = savedContent;
		}
		
		final String tag = block ? "div" : "span";
		final String editorContentEncoded = XMLEncoder.encode(editorContent);
		final StringBuilder bf = new StringBuilder();
		bf.append('<').
			append(tag).
			append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					"return " + (block ? EDIT_METHOD_AREA : EDIT_METHOD_LINE) + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(block ? editorContentEncoded.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") : editorContentEncoded).
					append("'," + modifiable + ");\"").
			append('>').
			append(pageContent).
			append("</").
			append(tag).
			append('>');
		
		return bf.toString();
	}
	
	public static final String edit(final Media feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null)
			return "";
		
		return edit(tl, feature, item, true);
	}
	
	private static final String edit(final TL tl, final Media feature, final Item item, final boolean modifiable)
	{
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final String modificationURL = modifiable ? tl.anchor.getModificationURL(feature, item, tl.request, tl.response) : null;
		final String onload =
			modificationURL!=null
				? (" onload=\"this.src='" + XMLEncoder.encode(modificationURL) + "';\"")
				: "";
		
		if(!tl.anchor.borders)
			return onload;
		
		final StringBuilder bf = new StringBuilder();
		bf.append(
				" class=\"contentEditorLink\"" +
				onload +
				" onclick=\"" +
					"return " + EDIT_METHOD_FILE + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(XMLEncoder.encode(feature.getURL(item))).
					append("'," + modifiable + ");\"");
		
		return bf.toString();
	}
	
	public static final String edit(final MediaFilter feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		checkEdit(feature, item);
		
		return edit(tl, feature.getSource(), item, false);
	}
	
	public static final String edit(final IntegerField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		return edit(feature, item, tl.filter.getPreviousPositionButtonURL(tl.request, tl.response));
	}
	
	public static final String edit(final IntegerField feature, final Item item, final String buttonURL)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final Item previousItem = tl.registerPositionItem(feature, item);
		if(previousItem==null)
			return "";
		
		final HttpServletRequest request = tl.request;
		return
			"<form action=\"" + action(request, tl.response) + "\" method=\"POST\" class=\"contentEditorPosition\">" +
				"<input type=\"hidden\" name=\"" + BAR_REFERER   + "\" value=\"" + referer(request)         + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_FEATURE   + "\" value=\"" + feature.getID()          + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_ITEM_FROM + "\" value=\"" + previousItem.getCopeID() + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_ITEM      + "\" value=\"" + item.getCopeID()         + "\">" +
				(
					buttonURL!=null
					? ("<input type=\"image\" src=\"" + buttonURL + "\" alt=\"Swap with previous item\">")
					: ("<input type=\"submit\" value=\"Up\">")
				) +
			"</form>";
	}
	
	private static final void checkEdit(final Feature feature, final Item item)
	{
		if(feature==null)
			throw new NullPointerException("feature");
		if(item==null)
			throw new NullPointerException("item");
		if(!feature.getType().isAssignableFrom(item.getCopeType()))
			throw new IllegalArgumentException("item " + item.getCopeID() + " does not belong to type of feature " + feature.getID());
	}
	
	public static final void writeHead(final PrintStream out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final StringBuilder bf = new StringBuilder();
		writeHead(bf);
		out.print(bf);
	}
	
	public static final void writeBar(final PrintStream out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final StringBuilder bf = new StringBuilder();
		writeBar(bf);
		out.print(bf);
	}
	
	public static final void writeHead(final StringBuilder out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		Bar_Jspm.writeHead(out, tl.anchor.borders);
	}
	
	public static final void writeBar(final StringBuilder out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final HttpServletRequest request = tl.request;
		final ArrayList<Target> targets = new ArrayList<Target>();
		targets.add(TargetLive.INSTANCE);
		if(tl.filter.draftsEnabled)
		{
			final List<Draft> drafts = Draft.TYPE.search(null, Draft.date, true);
			for(final Draft draft : drafts)
				targets.add(new TargetDraft(draft));
			targets.add(TargetNewDraft.INSTANCE);
		}
		Bar_Jspm.write(out,
				tl.anchor.getTarget(),
				targets,
				action(request, tl.response),
				referer(request),
				tl.anchor.borders,
				tl.anchor.borders ? BAR_BORDERS_OFF : BAR_BORDERS_ON,
				tl.filter.getBorderButtonURL(request, tl.response, tl.anchor.borders),
				tl.filter.getHideButtonURL (request, tl.response),
				tl.filter.getCloseButtonURL(request, tl.response),
				tl.anchor.getModificationsCount(),
				tl.anchor.sessionName);
	}
	
	private static final String action(final HttpServletRequest request, final HttpServletResponse response)
	{
		return response.encodeURL(request.getContextPath() + request.getServletPath() + LOGIN_PATH_INFO);
	}
	
	private static final String referer(final HttpServletRequest request)
	{
		final String queryString = request.getQueryString();
		return queryString!=null ? (request.getPathInfo() + '?' + request.getQueryString()) : request.getPathInfo();
	}
	
	private static final void writeBody(
			final StringBuilder out,
			final HttpServletResponse response)
		throws IOException
	{
		ServletOutputStream outStream = null;
		try
		{
			outStream = response.getOutputStream();
			final byte[] outBytes = out.toString().getBytes(UTF8);
			outStream.write(outBytes);
		}
		finally
		{
			if(outStream!=null)
				outStream.close();
		}
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #isLoggedIn()} instead
	 */
	@Deprecated
	public static final boolean isActive()
	{
		return isLoggedIn();
	}
	
	/**
	 * @deprecated use {@link #edit(String, MapField, Item, Object)} instead.
	 */
	@Deprecated
	public static final <K> String editBlock(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		return edit(content, feature, item, key);
	}
	
	/**
	 * @deprecated Use {@link #getSession()} instead
	 */
	@Deprecated
	public static final Session getLogin()
	{
		return getSession();
	}
}
