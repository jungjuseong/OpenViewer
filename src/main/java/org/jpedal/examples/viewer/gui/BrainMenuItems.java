package org.jpedal.examples.viewer.gui;

import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;


import org.jpedal.examples.viewer.BrainCommands;
import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.Values;
import org.jpedal.examples.viewer.gui.GUI;
import org.jpedal.examples.viewer.gui.generic.GUIButtons;
import org.jpedal.examples.viewer.gui.generic.GUIMenuItems;
import org.jpedal.examples.viewer.gui.javafx.JavaFXCheckBoxMenuItem;
import org.jpedal.examples.viewer.gui.javafx.JavaFXID;

import org.jpedal.examples.viewer.gui.javafx.JavaFXMenuItem;
import org.jpedal.examples.viewer.gui.CommandListener;
import org.jpedal.examples.viewer.utils.PropertiesFile;
import org.jpedal.parser.DecoderOptions;
import org.jpedal.utils.Messages;

/**
 * This class controls everything to-do with Menu Items, it holds the objects
 * and their corresponding methods.
 *
 * To initialize the object/class call init()
 */
public class BrainMenuItems extends GUIMenuItems {

	/** holds all menu entries (File, View, Help) */
	private MenuBar currentMenu = new MenuBar();

	private Menu fileMenu;
	private Menu openMenu;
	private MenuItem open;
	private MenuItem openUrl;
	private MenuItem save;
	private MenuItem reSaveAsForms;
	private MenuItem find;
	private MenuItem documentProperties;
	private MenuItem signPDF;
	private MenuItem print;
	private MenuItem printWithDotPattern;

	// private MenuItem recentDocuments;
	private MenuItem exit;
	private Menu editMenu;
	private MenuItem copy;
	private MenuItem selectAll;
	private MenuItem deselectAll;
	private MenuItem preferences;
	private Menu viewMenu;
	private Menu goToMenu;
	private MenuItem firstPage;
	private MenuItem backPage;
	private MenuItem forwardPage;
	private MenuItem lastPage;
	private MenuItem goTo;

	private CheckMenuItem textSelect;
	private CheckMenuItem panMode;
	private MenuItem fullscreen;

	private Menu helpMenu;
	private MenuItem visitWebsite;
	private MenuItem tipOfTheDay;

	// private MenuItem about;

	private MenuItem aboutBrain;

	private MenuItem helpForum;

	private Menu exportMenu;
	private MenuItem exportTextCoord;
	private MenuItem exportPatternImage;

	private Menu pageToolsMenu;

	private MenuItem rotatePages;
	private MenuItem deletePages;
	private MenuItem addPage;
	private MenuItem addHeaderFooter;
	private MenuItem stampText;
	private MenuItem stampImage;
	private MenuItem crop;

	public BrainMenuItems(final PropertiesFile properties) {
		super(properties);
	}

	private Menu getMenu(final int ID) {
		switch (ID) {
		case Commands.FILEMENU:
			return fileMenu;
		case Commands.EDITMENU:
			return editMenu;
		case Commands.OPENMENU:
			return openMenu;
		case Commands.VIEWMENU:
			return viewMenu;
		case Commands.GOTOMENU:
			return goToMenu;
		case Commands.HELP:
			return helpMenu;
		}
		return null;
	}

	private MenuItem getMenuItem(final int ID) {
		switch (ID) {
		case Commands.FILEMENU:
			return fileMenu;
		case Commands.OPENMENU:
			return openMenu;
		case Commands.OPENFILE:
			return open;
		case Commands.OPENURL:
			return openUrl;
		case Commands.SAVE:
			return save;
		case Commands.RESAVEASFORM:
			return reSaveAsForms;
		case Commands.FIND:
			return find;
		case Commands.DOCINFO:
			return documentProperties;
		case Commands.SIGN:
			return signPDF;
		case Commands.PRINT:
			return print;
			
		case BrainCommands.PRINT_WITH_DOT_PATTERN:
			return printWithDotPattern;
			
		case Commands.EXIT:
			return exit;
		case Commands.EDITMENU:
			return editMenu;
		case Commands.COPY:
			return copy;
		case Commands.SELECTALL:
			return selectAll;
		case Commands.DESELECTALL:
			return deselectAll;
		case Commands.PREFERENCES:
			return preferences;
		case Commands.VIEWMENU:
			return viewMenu;
		case Commands.GOTOMENU:
			return goToMenu;
		case Commands.FIRSTPAGE:
			return firstPage;
		case Commands.BACKPAGE:
			return backPage;
		case Commands.FORWARDPAGE:
			return forwardPage;
		case Commands.LASTPAGE:
			return lastPage;
		case Commands.GOTO:
			return goTo;

		case Commands.TEXTSELECT:
			return textSelect;
		case Commands.PANMODE:
			return panMode;
		case Commands.FULLSCREEN:
			return fullscreen;

		case BrainCommands.EXPORT_TEXT_COORD:
			return exportTextCoord;
			
		case BrainCommands.EXPORT_PATTERN_IMAGE:
			return exportPatternImage;

		case Commands.PAGETOOLSMENU:
			return pageToolsMenu;
		case Commands.ROTATE:
			return rotatePages;
		case Commands.DELETE:
			return deletePages;
		case Commands.ADD:
			return addPage;
		case Commands.ADDHEADERFOOTER:
			return addHeaderFooter;
		case Commands.STAMPTEXT:
			return stampText;
		case Commands.STAMPIMAGE:
			return stampImage;
		case Commands.CROP:
			return crop;
			
		case Commands.HELP:
			return helpMenu;
		case Commands.VISITWEBSITE:
			return visitWebsite;
		case Commands.TIP:
			return tipOfTheDay;
		case Commands.HELPFORUM:
			return helpForum;

		case BrainCommands.ABOUT_BRAIN:
			return aboutBrain;

		}

		return null;

	}

	public MenuBar getCurrentMenuFX() {
		return currentMenu;
	}

	@Override
	public void dispose() {
		if (currentMenu != null) 
			currentMenu.getMenus().removeAll(currentMenu.getMenus());
		
		currentMenu = null;
	}

	@Override
	public void setCheckMenuItemSelected(final int ID, final boolean b) {

		switch (ID) {
		case Commands.TEXTSELECT:
			textSelect.setSelected(b);
			break;
		case Commands.PANMODE:
			panMode.setSelected(b);
			break;
		default:
			break;
		}

	}

	@Override
	public void setBackNavigationItemsEnabled(final boolean enabled) {

		backPage.setDisable(!enabled);
		firstPage.setDisable(!enabled);
	}

	@Override
	public void setForwardNavigationItemsEnabled(final boolean enabled) {
		forwardPage.setDisable(!enabled);
		lastPage.setDisable(!enabled);
	}

	@Override
	public void setGoToNavigationItemEnabled(final boolean enabled) {
		goTo.setDisable(!enabled);
	}

	protected void addMenuItem(final Menu parentMenu, final String text, final String toolTip, final int ID) {

		boolean isCheckBox = false; // default value
		if (ID == Commands.PANMODE || ID == Commands.TEXTSELECT)
			isCheckBox = true;		

		final JavaFXID menuItem;
		if (isCheckBox) {
			menuItem = new JavaFXCheckBoxMenuItem(text);
			parentMenu.getItems().add((CheckMenuItem) menuItem);
		} else {
			menuItem = new JavaFXMenuItem(text);
			parentMenu.getItems().add((MenuItem) menuItem);
		}

		if (!toolTip.isEmpty()) 
			menuItem.setToolTipText(toolTip);
		
		menuItem.setID(ID);
		setKeyAccelerators(ID, (MenuItem) menuItem);

		// add listener
		menuItem.setOnAction((EventHandler) currentCommandListener.getCommandListener());

		switch (ID) {
		case Commands.OPENFILE:
			open = (MenuItem) menuItem;
			break;
		case Commands.OPENURL:
			openUrl = (MenuItem) menuItem;
			break;
		case Commands.SAVE:
			save = (MenuItem) menuItem;
			break;
		case Commands.FIND:
			find = (MenuItem) menuItem;
			break;
		case Commands.DOCINFO:
			documentProperties = (MenuItem) menuItem;
			break;
		case Commands.PREFERENCES:
			preferences = (MenuItem) menuItem;
			break;
		case Commands.SIGN:
			signPDF = (MenuItem) menuItem;
			break;
		case Commands.PRINT:
			print = (MenuItem) menuItem;
			break;
		
		case BrainCommands.PRINT_WITH_DOT_PATTERN:
			printWithDotPattern = (MenuItem) menuItem;
			break;
			
		case Commands.EXIT:
			exit = (MenuItem) menuItem;
			// set name to exit so fest can find it
			exit.setId("exit");
			break;
		case Commands.COPY:
			copy = (MenuItem) menuItem;
			break;
		case Commands.SELECTALL:
			selectAll = (MenuItem) menuItem;
			break;
		case Commands.DESELECTALL:
			deselectAll = (MenuItem) menuItem;
			break;
		case Commands.FIRSTPAGE:
			firstPage = (MenuItem) menuItem;
			break;
		case Commands.BACKPAGE:
			backPage = (MenuItem) menuItem;
			break;
		case Commands.FORWARDPAGE:
			forwardPage = (MenuItem) menuItem;
			break;
		case Commands.LASTPAGE:
			lastPage = (MenuItem) menuItem;
			break;
		case Commands.GOTO:
			goTo = (MenuItem) menuItem;
			break;
		case Commands.FULLSCREEN:
			fullscreen = (MenuItem) menuItem;
			break;
		case Commands.MOUSEMODE:
			fullscreen = (MenuItem) menuItem;
			break;
		case Commands.PANMODE:
			panMode = (CheckMenuItem) menuItem;
			panMode.setSelected(false);
			break;
		case Commands.TEXTSELECT:
			textSelect = (CheckMenuItem) menuItem;
			textSelect.setSelected(true);
			break;
		case BrainCommands.EXPORT_TEXT_COORD:
			exportTextCoord = (MenuItem) menuItem;
			break;
		case BrainCommands.EXPORT_PATTERN_IMAGE:
			exportPatternImage = (MenuItem) menuItem;
			break;
		case Commands.ROTATE:
			rotatePages = (MenuItem) menuItem;
			break;
		case Commands.DELETE:
			deletePages = (MenuItem) menuItem;
			break;
		case Commands.ADD:
			addPage = (MenuItem) menuItem;
			break;
		case Commands.ADDHEADERFOOTER:
			addHeaderFooter = (MenuItem) menuItem;
			break;
		case Commands.STAMPTEXT:
			stampText = (MenuItem) menuItem;
			break;
		case Commands.STAMPIMAGE:
			stampImage = (MenuItem) menuItem;
			break;
		case Commands.SETCROP:
			crop = (MenuItem) menuItem;
			break;
		case Commands.VISITWEBSITE:
			visitWebsite = (MenuItem) menuItem;
			break;
		case Commands.TIP:
			tipOfTheDay = (MenuItem) menuItem;
			break;
		case BrainCommands.ABOUT_BRAIN:
			aboutBrain = (MenuItem) menuItem;
			break;
		case Commands.HELP:
			helpForum = (MenuItem) menuItem;
			break;
		default:
		}

		disableUnimplementedItems(ID, false);

	}


	@Override
	public void setMenusForDisplayMode(final int commandIDForDislayMode, final int mouseMode) {

	}

	/**
	 * add MenuItem to main menu
	 */
	protected void addToMainMenu(final Menu fileMenuList) {
		currentMenu.getMenus().add(fileMenuList);
		currentMenu.setUseSystemMenuBar(true);
	}

	/**
	 * create items on drop down menus
	 */
	@SuppressWarnings("unused")
	@Override
	public void createMainMenu(final boolean includeAll, final CommandListener currentCommandListener,
			final boolean isSingle, final Values commonValues, final Commands currentCommands,
			final GUIButtons buttons) {

		this.currentCommandListener = currentCommandListener;

		String addSeparator;

		fileMenu = new Menu("파일");

		addToMainMenu(fileMenu);

		// add open options
		addMenuItem(fileMenu, "열기",	Messages.getMessage("PdfViewerFileMenuTooltip.open"), Commands.OPENFILE);

		addSeparator = properties.getValue("Save") + properties.getValue("Resaveasforms") + properties.getValue("Find");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			fileMenu.getItems().add(new SeparatorMenuItem());
		
		addMenuItem(fileMenu, "저장하기", Messages.getMessage("PdfViewerFileMenuTooltip.save"), Commands.SAVE);

		// not set if I just run from jar as no IText....
		if (includeAll) 
			addMenuItem(fileMenu, Messages.getMessage("PdfViewerFileMenuResaveForms.text"),
					Messages.getMessage("PdfViewerFileMenuTooltip.saveForms"), Commands.SAVEFORM);
		

		addSeparator = properties.getValue("Documentproperties");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			fileMenu.getItems().add(new SeparatorMenuItem());
		
		addMenuItem(fileMenu, "PDF 문서 속성",
				Messages.getMessage("PdfViewerFileMenuTooltip.props"), Commands.DOCINFO);

		addMenuItem(fileMenu, "환경설정", Messages.getMessage("PdfViewerEditMenuTooltip.Preferences"), Commands.PREFERENCES);

		if (commonValues.isEncrypOnClasspath()) 
			addMenuItem(fileMenu, "사인하기",Messages.getMessage("PdfViewerFileMenuTooltip.sign"), Commands.SIGN);
		 else 
			addMenuItem(fileMenu, "사인하기",Messages.getMessage("PdfViewerFileMenuSignPDF.NotPath"), Commands.SIGN);		

		addSeparator = properties.getValue("Print");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			fileMenu.getItems().add(new SeparatorMenuItem());
		
		addMenuItem(fileMenu, "인쇄하기", Messages.getMessage("PdfViewerFileMenuTooltip.print"), Commands.PRINT);
		addMenuItem(fileMenu, "닷패턴과 인쇄하기", Messages.getMessage("PdfViewerFileMenuTooltip.print"), BrainCommands.PRINT_WITH_DOT_PATTERN);

		addSeparator = properties.getValue("Recentdocuments");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			fileMenu.getItems().add(new SeparatorMenuItem());
			currentCommands.recentDocumentsOption();
		

		addSeparator = properties.getValue("Exit");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			fileMenu.getItems().add(new SeparatorMenuItem());
		
		addMenuItem(fileMenu, "끝내기",Messages.getMessage("PdfViewerFileMenuTooltip.exit"), Commands.EXIT);

		// EDIT MENU
		editMenu = new Menu("편집");
		addToMainMenu(editMenu);

		addMenuItem(editMenu, "복사",	Messages.getMessage("PdfViewerEditMenuTooltip.Copy"), Commands.COPY);
		addMenuItem(editMenu, "모두 선택", 	Messages.getMessage("PdfViewerEditMenuTooltip.Selectall"), Commands.SELECTALL);
		addMenuItem(editMenu, "선택 해제",	Messages.getMessage("PdfViewerEditMenuTooltip.Deselectall"), Commands.DESELECTALL);
		
		addSeparator = properties.getValue("PdfViewerEditMenuTooltip.Deselectall");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			editMenu.getItems().add(new SeparatorMenuItem());
		
		addMenuItem(editMenu, "단어 찾기",	Messages.getMessage("PdfViewerFileMenuTooltip.find"), Commands.FIND);

		viewMenu = new Menu("페이지 보기");
		addToMainMenu(viewMenu);

		addMenuItem(viewMenu, "처음 페이지로", "", Commands.FIRSTPAGE);
		addMenuItem(viewMenu, "이전 페이지로", "", Commands.BACKPAGE);
		addMenuItem(viewMenu, "다음 페이지로", "", Commands.FORWARDPAGE);
		addMenuItem(viewMenu, "마지막 페이지로", "", Commands.LASTPAGE);
		addMenuItem(viewMenu, "지정한 페이지로", "", Commands.GOTO);

		addSeparator = properties.getValue("Previousdocument") + properties.getValue("Nextdocument");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true")) 
			goToMenu.getItems().add(new SeparatorMenuItem());
		

		if (properties.getValue("panMode").equals("true") || properties.getValue("textSelect").equals("true")) {
			viewMenu.getItems().add(new SeparatorMenuItem());
			if (properties.getValue("panMode").equals("true")) 
				addMenuItem(viewMenu, "화면이동 모드",	Messages.getMessage("PdfViewerViewMenuTooltip.panMode"), Commands.PANMODE);			

			if (properties.getValue("textSelect").equals("true"))
				addMenuItem(viewMenu, "텍스트 선택 모드", 	Messages.getMessage("PdfViewerViewMenuTooltip.textSelect"), Commands.TEXTSELECT);
			
			// viewMenu.getItems().add(new SeparatorMenuItem());
		}

		addSeparator = properties.getValue("Fullscreen");
		if (!addSeparator.isEmpty() && addSeparator.equalsIgnoreCase("true"))
			viewMenu.getItems().add(new SeparatorMenuItem());
		

		// full page mode
		addMenuItem(viewMenu, Messages.getMessage("PdfViewerViewMenuFullScreenMode.text"),
				Messages.getMessage("PdfViewerViewMenuTooltip.fullScreenMode"), Commands.FULLSCREEN);

		/**
		 * add export menus
		 **/
		exportMenu = new Menu("작업");
		addToMainMenu(exportMenu);

		/**
		 * external/itext menu option example adding new option to Export menu
		 */
		/**
		 * external/itext menu option example adding new option to Export menu
		 * Tooltip text can be externalised in
		 * Messages.getMessage("PdfViewerTooltip.NEWFUNCTION") and text added
		 * into files in res package
		 */

		addMenuItem(exportMenu, "텍스트와 좌표 추출", "", BrainCommands.EXPORT_TEXT_COORD);
		addMenuItem(exportMenu, "패턴 이미지로 내보내기", "", BrainCommands.EXPORT_PATTERN_IMAGE);

		/**
		 * items options if IText available
		 */
		pageToolsMenu = new Menu(Messages.getMessage("PdfViewerPageToolsMenu.text"));
		addToMainMenu(pageToolsMenu);

		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuRotate.text"), "", Commands.ROTATE);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuDelete.text"), "", Commands.DELETE);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuAddPage.text"), "", Commands.ADD);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuAddHeaderFooter.text"), "",
				Commands.ADDHEADERFOOTER);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuStampText.text"), "", Commands.STAMPTEXT);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuStampImage.text"), "",
				Commands.STAMPIMAGE);
		addMenuItem(pageToolsMenu, Messages.getMessage("PdfViewerPageToolsMenuSetCrop.text"), "", Commands.SETCROP);

		if (includeAll && GUI.debugFX) {

			// menu option for debugging viewport
			final Menu debugViewport = new Menu("Debug");
			addToMainMenu(debugViewport);

			// toggle viewport border
			addMenuItem(debugViewport, "Toggle viewport border", "", Commands.TOGGLE);

			// setViewport option button
			addMenuItem(debugViewport, "Set viewport", "", Commands.SET);

			// reset vieport option button
			addMenuItem(debugViewport, "Reset viewport", "Resets the viewport to the default value", Commands.RESET);

			// switch for testing hardware acceleration
			addMenuItem(debugViewport, "Enable acceleration", "Enable Hardware diplay acceleration for screen",
					Commands.ACCELERATIONON);

			// switch for testing hardware acceleration
			addMenuItem(debugViewport, "Disable acceleration", "Disable Hardware diplay acceleration for screen",
					Commands.ACCELERATIONOFF);

			addMenuItem(debugViewport, "Show form NAMES", "displays a list of all the forms names",
					Commands.SHOWFORMNAMES);

			// delete properties file on exit
			addMenuItem(debugViewport, "Wipe properties on exit", "deletes the properties file on exit",
					Commands.DELETEPROPERTIESONEXIT);

			addMenuItem(debugViewport, "Always show mouse coords",
					"prevents anything other than mouse coordinates displaying in the multibox",
					Commands.ALWAYSSHOWMOUSE);

		}

		helpMenu = new Menu(Messages.getMessage("PdfViewerHelpMenu.text"));
		addToMainMenu(helpMenu);

		addMenuItem(helpMenu, Messages.getMessage("PdfViewerHelpMenu.VisitWebsite"), "", Commands.VISITWEBSITE);
		addMenuItem(helpMenu, Messages.getMessage("PdfViewerHelpMenuTip.text"), "", Commands.TIP);
		// addMenuItem(helpMenu,Messages.getMessage("PdfViewerHelpMenuUpdates.text"),"",Commands.UPDATE);
		addMenuItem(helpMenu, Messages.getMessage("PdfViewerHelpMenuabout.text"),
				Messages.getMessage("PdfViewerHelpMenuTooltip.about"), Commands.ABOUT);

		if (includeExtraMenus)
			addMenuItem(helpMenu, Messages.getMessage("PdfViewerHelpMenuTutorial.text"),
					"Visit http://www.idrsolutions.com/java-pdf-library-support/", Commands.HELP);
		

	}

	/** setup keyboard shortcuts */
	static void setKeyAccelerators(final int ID, final MenuItem menuItem) {
		switch (ID) {

		case Commands.FIND:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+F"));
			break;
		case Commands.SAVE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
			break;
		case Commands.PRINT:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
			break;
		case Commands.EXIT:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
			break;
		case Commands.DOCINFO:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
			break;
		case Commands.OPENFILE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
			break;
		case Commands.OPENURL:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+U"));
			break;
		case Commands.PREVIOUSDOCUMENT:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+LEFT+SHIFT"));
			break;
		case Commands.NEXTDOCUMENT:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+RIGHT+SHIFT"));
			break;
		case Commands.FIRSTPAGE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+HOME"));
			break;
		case Commands.BACKPAGE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+UP"));
			break;
		case Commands.FORWARDPAGE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+DOWN"));
			break;
		case Commands.LASTPAGE:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+END"));
			break;
		case Commands.GOTO:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+N+SHIFT"));
			break;
		case Commands.BITMAP:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+B"));
			break;
		case Commands.COPY:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
			break;
		case Commands.SELECTALL:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+A"));
			break;
		case Commands.DESELECTALL:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+A+SHIFT"));
			break;
		case Commands.PREFERENCES:
			menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+K"));
			break;
		}
	}

	@Override
	public void ensureNoSeperators() {
		ensureNoSeperators(Commands.FILEMENU);
		ensureNoSeperators(Commands.EDITMENU);
		ensureNoSeperators(Commands.VIEWMENU);
		ensureNoSeperators(Commands.GOTOMENU);
	}

	@Override
	public void ensureNoSeperators(int type) {
		for (int k = 0; k != ((Menu) getMenuItem(type)).getItems().size(); k++) {
			if (((Menu) getMenuItem(type)).getItems().get(k).isVisible()) {
				if (((Menu) getMenuItem(type)).getItems().get(k) instanceof SeparatorMenuItem) 
					((Menu) getMenuItem(type)).getItems().remove(k);
				
				break;
			}
		}
	}

	@Override
	public boolean isMenuItemExist(final int ID) {
		return getMenuItem(ID) != null;
	}

	@Override
	public void setMenuItem(final int ID, final boolean enabled, final boolean visible) {

		if (ID == Commands.CURRENTMENU && currentMenu != null) {
			currentMenu.setDisable(!enabled);
			currentMenu.setVisible(visible);
		} 
		else if (getMenuItem(ID) != null) {
			getMenuItem(ID).setDisable(!enabled);
			getMenuItem(ID).setVisible(visible);
		}
		
		if (ID == Commands.FULLSCREEN && DecoderOptions.isRunningOnMac) 
			fullscreen.setDisable(true);
		
	}

	@Override
	public void addToMenu(final Object menuItem, final int parentMenuID) {
		getMenu(parentMenuID).getItems().add((MenuItem) menuItem);
	}

	/**
	 * Temporary Method to Disable unimplemented Viewer Items. Edit and Remove
	 * the items from this method as we implement features.
	 * 
	 */
	public void disableUnimplementedItems(final int ID, final boolean disableViewModes) {

		final boolean debug = !GUI.debugFX;

		final int ALL = -10;

		if (ID != ALL) {
			if (!disableViewModes) {
				switch (ID) {

				case Commands.FIND:
					find.setDisable(debug);
					break;

				case Commands.SIGN:
					signPDF.setDisable(debug);
					break;
				//case Commands.PRINT:
				//	print.setDisable(debug);
				//	break;

				}
			}

		} 
		else {
			signPDF.setDisable(debug);
			//print.setDisable(debug);
		}
	}

}