package org.jpedal.examples.viewer;

import java.util.Map;
import javafx.scene.Cursor;
import org.jpedal.PdfDecoderFX;
import org.jpedal.PdfDecoderInt;

import org.jpedal.examples.viewer.commands.*;
import org.jpedal.examples.viewer.commands.brain.BrainDocInfo;
import org.jpedal.examples.viewer.commands.generic.Snapshot;

import org.jpedal.examples.viewer.commands.javafx.*;

import org.jpedal.examples.viewer.gui.GUI;
import org.jpedal.examples.viewer.gui.generic.GUISearchWindow;
import org.jpedal.display.GUIThumbnailPanel;
import org.jpedal.examples.viewer.utils.PrinterInt;
import org.jpedal.examples.viewer.utils.PropertiesFile;
import org.jpedal.exception.PdfException;
import org.jpedal.external.JPedalActionHandler;
import org.jpedal.external.Options;
import org.jpedal.gui.GUIFactory;

/**
 *
 * @author jungjuseong@gmail.com
 * 
 *         JavaFxCommands 수정 버전
 */
public class BrainCommands extends Commands {

	public final static int ABOUT_BRAIN = 2000;
	public final static int EXPORT_TEXT_COORD = 2100;
	public final static int EXPORT_DOT_PATTERN = 2101;
	
	public final static int PRINT_WITH_DOT_PATTERN = 2102; // print pages with the dot pattern


	public BrainCommands(final Values commonValues, final GUIFactory currentGUI, final PdfDecoderInt decode_pdf,
			final GUIThumbnailPanel thumbnails, final PropertiesFile properties, final GUISearchWindow searchFrame,
			final PrinterInt currentPrinter) {
		super(commonValues, currentGUI, decode_pdf, thumbnails, properties, searchFrame, currentPrinter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object executeCommand(final int ID, Object[] args) {

		// treat null and Object[]{null} as both null
		if (args != null && args.length == 1 && args[0] == null) {
			args = null;
		}

		Object status = null;

		currentGUI.setExecutingCommand(true);

		final Map jpedalActionHandlers = (Map) decode_pdf.getExternalHandler(Options.JPedalActionHandlers);

		if (jpedalActionHandlers != null) {
			final JPedalActionHandler jpedalAction = (JPedalActionHandler) jpedalActionHandlers.get(ID);
			if (jpedalAction != null) {
				jpedalAction.actionPerformed(currentGUI, this);
				return null;
			}
		}

		// Temporary patch to get page navigation working
		if (ID >= FIRSTPAGE && ID <= GOTO) {
			commonValues.setPageCount(decode_pdf.getPageCount());
			commonValues.setCurrentPage(decode_pdf.getPageNumber());
		}

		// Execute FX Commands
		switch (ID) {
		case EXPORT_TEXT_COORD:
			ExtractTextCoord2.execute(args, currentGUI, decode_pdf, commonValues);
			break;
			
		case EXIT:
			JavaFXExit.execute(args, thumbnails, currentGUI, commonValues, decode_pdf, properties);
			break;
		case SNAPSHOT:
			
			extractingAsImage = Snapshot.execute(args, currentGUI, decode_pdf, extractingAsImage); 
			if (extractingAsImage)
				((PdfDecoderFX) decode_pdf).setCursor(Cursor.CROSSHAIR);
			break;
		case EXTRACTASIMAGE:
			JavaFXExtractSelectionAsImage.execute(commonValues, currentGUI, decode_pdf);
			break;
		case EXTRACTTEXT:
			JavaFXExtractText.execute(args, currentGUI, decode_pdf, commonValues);
			break;
		case DESELECTALL:
			DeSelectAll.execute(currentGUI, decode_pdf);
			break;
		case SELECTALL:
			SelectAll.execute(currentGUI, decode_pdf, commonValues);
			break;
		case COPY:
			JavaFXCopy.execute(currentGUI, decode_pdf, commonValues);
			break;
		case FIND:
			Find.execute(args, commonValues, currentGUI, decode_pdf, searchFrame);
			break;
		case PREVIOUSRESULT:
			PreviousResult.execute(args, commonValues, currentGUI, decode_pdf, searchFrame);
			break;
		case NEXTRESULT:
			NextResults.execute(args, commonValues, searchFrame, currentGUI, decode_pdf);
			break;
		case SAVE:
			JavaFXSaveFile.execute(args, currentGUI, commonValues);
			break;
		case PREFERENCES:
			JavaFXPreferences.execute(args, currentGUI);
			break;
		// case UPDATE:
		// Update.execute(args, currentGUI);
		case SCALING:
			JavaFXScaling.execute(args, commonValues, decode_pdf, currentGUI);
			break;
		case ROTATION:
			Rotation.execute(args, currentGUI, commonValues);
			break;
		case PANMODE:
			JavaFXPanMode.execute(args, currentGUI, mouseMode, decode_pdf);
			break;
		case TEXTSELECT:
			JavaFXTextSelect.execute(args, currentGUI, mouseMode, decode_pdf);
			break;
		case MOUSEMODE:
			JavaFXMouseModeCommand.execute(args, currentGUI, mouseMode, decode_pdf);
			break;
		case DOCINFO:
			BrainDocInfo.execute(args, currentGUI, commonValues, decode_pdf);
			break;
		case TIP:
			JavaFXTipOfTheDay.execute(args, currentGUI, properties);
			break;
		case FULLSCREEN:
			JavaFXFullScreen.execute(args, currentGUI);
			break;
		case ABOUT:
			JavaFXInfo.execute(args); // Gets the info box
			break;
		case VISITWEBSITE:
			VisitWebsite.execute(args, currentGUI); // takes user to website
			break;
		case HELP:
			JavaFXHelp.execute(args); // gets the help box
			break;
		case FIRSTPAGE:
			JavaFXPageNavigator.goFirstPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case FBACKPAGE:
			JavaFXPageNavigator.goFBackPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case BACKPAGE:
			JavaFXPageNavigator.goBackPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case FORWARDPAGE:
			JavaFXPageNavigator.goForwardPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case FFORWARDPAGE:
			JavaFXPageNavigator.goFForwardPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case LASTPAGE:
			JavaFXPageNavigator.goLastPage(args, commonValues, decode_pdf, currentGUI);
			break;
		case GOTO:
			JavaFXPageNavigator.goPage(args, currentGUI, commonValues, decode_pdf);
			break;
		case OPENFILE:
			JavaFXOpenFile.executeOpenFile(args, currentGUI, searchFrame, properties, thumbnails, decode_pdf,
					commonValues);
			break;
		case OPENURL:
			JavaFXOpenFile.executeOpenURL(args, commonValues, searchFrame, currentGUI, decode_pdf, properties,
					thumbnails);
			break;
		case RSS:
			JavaFXRSSyndication.execute(args);
			break;

		case EXPORT_DOT_PATTERN:
			//ExportWithDotPattern.execute(args, currentGUI, commonValues);
			break;
			
		case PRINT_WITH_DOT_PATTERN:
			PrintWithDotPattern.execute(args, currentGUI, commonValues);
			break;	
			
		case SETPAGECOLOR:
			SetPageColor.execute(args, decode_pdf);
			break;
		case SETTEXTCOLOR:
			SetTextColor.execute(args, decode_pdf);
			break;
		case CHANGELINEART:
			ChangeLineArt.execute(args, decode_pdf);
			break;
		case SETREPLACEMENTCOLORTHRESHOLD:
			SetReplacementThreshold.execute(args, decode_pdf);
			break;
		default:
			if (GUI.debugFX) {
				System.out.println("Command ID " + ID + " not Implemented Yet for JavaFX");
			}
			break;
		}

		// Mark as executed is not running in thread
		if (!currentGUI.isCommandInThread())
			currentGUI.setExecutingCommand(false);

		return status;

	}

	@Override
	public void openTransferedFile() throws PdfException {

		decode_pdf.flushObjectValues(true);
		JavaFXOpenFile.openFile(commonValues.getSelectedFile(), commonValues, searchFrame, currentGUI, decode_pdf,
				properties, thumbnails);

	}
}
