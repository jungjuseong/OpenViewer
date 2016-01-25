package org.jpedal.examples.viewer.gui;

import java.io.File;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.jpedal.PdfDecoderFX;
import org.jpedal.display.Display;
import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.Values;
import org.jpedal.examples.viewer.commands.generic.GUICopy;
import org.jpedal.examples.viewer.gui.GUI;
import org.jpedal.examples.viewer.gui.MouseSelector;
import org.jpedal.examples.viewer.gui.javafx.JavaFXMouseFunctionality;
import org.jpedal.examples.viewer.gui.javafx.dialog.FXOptionDialog;
import org.jpedal.exception.PdfException;
import org.jpedal.external.Options;
import org.jpedal.grouping.SearchType;
import org.jpedal.gui.GUIFactory;
import org.jpedal.io.Speech;
import org.jpedal.objects.PdfPageData;
import org.jpedal.parser.DecoderOptions;
import org.jpedal.text.TextLines;
import org.jpedal.utils.Messages;

public class BrainMouseSelector extends MouseSelector implements JavaFXMouseFunctionality {

	private final PdfDecoderFX decode_pdf;
	private final GUIFactory currentGUI;
	private final Values commonValues;
	private final Commands currentCommands;
	private PdfPageData page_data;

	public static final boolean activateMultipageHighlight = true;

	// Variables to keep track of multiple clicks
	private int clickCount;
	private long lastTime = -1;

	// Find current highlighted page
	private boolean startHighlighting;

	// ID of objects found during selection
	public int id = -1;
	public int lastId = -1;

	// used to track changes when dragging rectangle around
	private int old_m_x2 = -1, old_m_y2 = -1;

	// Right click options
	MenuItem copy;
	MenuItem selectAll, deselectall;
	MenuItem extractText, extractImage;
	Image snapshotIcon;
	MenuItem snapShot;
	MenuItem find;
	MenuItem speakHighlighted;

	public BrainMouseSelector(final PdfDecoderFX decode_pdf, final GUIFactory currentGUI, final Values commonValues,
			final Commands currentCommands) {

		this.decode_pdf = decode_pdf;
		this.currentGUI = currentGUI;
		this.commonValues = commonValues;
		this.currentCommands = currentCommands;
		this.page_data = decode_pdf.getPdfPageData();
	}

	ContextMenu contextMenu;

	private void createRightClickMenu() {

		// Setup Items and Menus.
		contextMenu = new ContextMenu();

		copy = new MenuItem(Messages.getMessage("PdfRightClick.copy"));
		selectAll = new MenuItem(Messages.getMessage("PdfRightClick.selectAll"));
		deselectall = new MenuItem(Messages.getMessage("PdfRightClick.deselectAll"));

		extractText = new MenuItem(Messages.getMessage("PdfRightClick.extractText"));
		extractImage = new MenuItem(Messages.getMessage("PdfRightClick.extractImage"));

		snapshotIcon = new Image("/org/jpedal/examples/viewer/res/snapshot_menu.gif");
		snapShot = new MenuItem(Messages.getMessage("PdfRightClick.snapshot"), new ImageView(snapshotIcon));

		find = new MenuItem(Messages.getMessage("PdfRightClick.find"));

		// Setup Menu Item Listeners.
		copy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				currentCommands.executeCommand(Commands.COPY, null);
			}
		});

		selectAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				currentCommands.executeCommand(Commands.SELECTALL, null);
			}
		});

		deselectall.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				currentCommands.executeCommand(Commands.DESELECTALL, null);
			}
		});

		extractText.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				currentCommands.executeCommand(Commands.EXTRACTTEXT, null);
			}
		});

		extractImage.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				if ((decode_pdf.getPages().getHighlightedImage() != null) && (decode_pdf.getDisplayView() == 1)) {
					final FileChooser jf = new FileChooser();
					final FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG (*jpg)",	"*.jpg");
					final FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG (*PNG)",	"*.png");
					final FileChooser.ExtensionFilter tifFilter = new FileChooser.ExtensionFilter("Tiff (*TIFF)", "*.tiff");
					jf.getExtensionFilters().addAll(jpgFilter, pngFilter, tifFilter);

					final File file = jf.showSaveDialog((Stage) currentGUI.getFrame());
					String fileExt = file.getName();
					fileExt = fileExt.substring(fileExt.indexOf('.') + 1, fileExt.length());
					decode_pdf.getDynamicRenderer().saveImage(id, file.getAbsolutePath(), fileExt);

				}
			}

		});

		snapShot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				currentCommands.executeCommand(Commands.SNAPSHOT, null);
			}
		});

		find.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {
				// ensure coordinates in right order
				final int[] coords = decode_pdf.getPages().getCursorBoxOnScreenAsArray();
				if (coords == null) {
					if (GUI.showMessages)
						currentGUI.showMessageDialog("선택한 글자가 없습니다.\n글자를 선택하세요.", "선택한 글자가 없음",
								FXOptionDialog.ERROR_MESSAGE);

					return;
				}

				String textToFind = currentGUI.showInputDialog(Messages.getMessage("PdfViewerMessage.GetUserInput"));

				// if cancel return to menu.
				if (textToFind == null || textToFind.length() < 1)
					return;

				// Modify coordinates to ensure fully encompassing highlighted text
				final int yMod = 2;

				int tx1 = coords[0];
				int tx2 = coords[0] + coords[2];
				int ty1 = coords[1] - yMod;
				int ty2 = coords[1] + coords[3] + yMod;

				if (ty1 < ty2) {
					final int temp = ty2;
					ty2 = ty1;
					ty1 = temp;
				}

				if (tx1 > tx2) {
					final int temp = tx2;
					tx2 = tx1;
					tx1 = temp;
				}

				page_data = decode_pdf.getPdfPageData();
				final int cropX = page_data.getCropBoxX(commonValues.getCurrentPage());
				final int cropY = page_data.getCropBoxY(commonValues.getCurrentPage());
				final int mediaW = page_data.getMediaBoxWidth(commonValues.getCurrentPage());
				final int mediaH = page_data.getMediaBoxHeight(commonValues.getCurrentPage());

				if (tx1 < cropX)
					tx1 = cropX;

				if (tx1 > mediaW - cropX)
					tx1 = mediaW - cropX;

				if (tx2 < cropX)
					tx2 = cropX;

				if (tx2 > mediaW - cropX)
					tx2 = mediaW - cropX;

				if (ty1 < cropY)
					ty1 = cropY;

				if (ty1 > mediaH - cropY)
					ty1 = mediaH - cropY;

				if (ty2 < cropY)
					ty2 = cropY;

				if (ty2 > mediaH - cropY)
					ty2 = mediaH - cropY;

				int searchType = SearchType.DEFAULT;

				final int caseSensitiveOption = currentGUI.showConfirmDialog(
						Messages.getMessage("PdfViewercase.message"), null, FXOptionDialog.YES_NO_OPTION);

				if (caseSensitiveOption == FXOptionDialog.YES_OPTION)
					searchType |= SearchType.CASE_SENSITIVE;

				final int findAllOption = currentGUI.showConfirmDialog(Messages.getMessage("PdfViewerfindAll.message"),
						null, FXOptionDialog.YES_NO_OPTION);

				if (findAllOption == FXOptionDialog.NO_OPTION)
					searchType |= SearchType.FIND_FIRST_OCCURANCE_ONLY;

				final int hyphenOption = currentGUI.showConfirmDialog(
						Messages.getMessage("PdfViewerfindHyphen.message"), null, FXOptionDialog.YES_NO_OPTION);

				if (hyphenOption == FXOptionDialog.YES_OPTION)
					searchType |= SearchType.MUTLI_LINE_RESULTS;

				if (textToFind != null) {
					try {
						final float[] co_ords;

						co_ords = decode_pdf.getGroupingObject().findText(tx1, ty1, tx2, ty2,
								new String[] { textToFind }, searchType);

						if (co_ords.length >= 2) {
							if (co_ords.length < 3) {
								currentGUI.showMessageDialog(Messages.getMessage("PdfViewerMessage.Found") + ' '
										+ co_ords[0] + ',' + co_ords[1]);
							} else {
								final StringBuilder displayCoords = new StringBuilder();
								String coordsMessage = Messages.getMessage("PdfViewerMessage.FoundAt");
								for (int i = 0; i < co_ords.length; i += 5) {
									displayCoords.append(coordsMessage).append(' ');
									displayCoords.append(co_ords[i]);
									displayCoords.append(',');
									displayCoords.append(co_ords[i + 1]);

									displayCoords.append('\n');
									if (co_ords[i + 4] == -101)
										coordsMessage = Messages.getMessage("PdfViewerMessage.FoundAtHyphen");
									else 
										coordsMessage = Messages.getMessage("PdfViewerMessage.FoundAt");		

								}
								currentGUI.showMessageDialog(displayCoords.toString());
							}
						} else {
							currentGUI.showMessageDialog(Messages.getMessage("PdfViewerMessage.NotFound"));
						}

					} catch (final PdfException e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		// * Add Items to Main Menu.
		if (decode_pdf != null && decode_pdf.isOpen()) {
			contextMenu.getItems().addAll(extractText, extractImage, snapShot);
			contextMenu.getItems().addAll(new SeparatorMenuItem(), selectAll, deselectall);
			contextMenu.getItems().addAll(new SeparatorMenuItem(), find);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		final long currentTime = new Date().getTime();

		if (lastTime + 500 < currentTime)
			clickCount = 0;

		lastTime = currentTime;

		if (isOtherKey(e)) {
			// Single mode actions
			if (clickCount != 4)
				clickCount++;

			final int pagenumber = decode_pdf.getPageNumber();
			this.page_data = decode_pdf.getPdfPageData();
			final int crx = page_data.getCropBoxX(pagenumber);
			final int cry = page_data.getCropBoxY(pagenumber);
			commonValues.m_x1 = (int) e.getX() + crx;
			commonValues.m_y1 = (int) e.getY() + cry;

			id = decode_pdf.getDynamicRenderer().isInsideImage(commonValues.m_x1, commonValues.m_y1);

			if (lastId != id && id != -1) {
				final int[] imageArea = decode_pdf.getDynamicRenderer().getAreaAsArray(id);

				if (imageArea != null) {
					int h = imageArea[3];
					int w = imageArea[2];

					int x = imageArea[0];
					int y = imageArea[1];
					decode_pdf.getDynamicRenderer().setneedsHorizontalInvert(false);
					decode_pdf.getDynamicRenderer().setneedsVerticalInvert(false);
					// Check for negative values
					if (w < 0) {
						decode_pdf.getDynamicRenderer().setneedsHorizontalInvert(true);
						w = -w;
						x -= w;
					}
					if (h < 0) {
						decode_pdf.getDynamicRenderer().setneedsVerticalInvert(true);
						h = -h;
						y -= h;
					}

					decode_pdf.getPages().setHighlightedImage(new int[] { x, y, w, h });
				}
				lastId = id;
			} else {
				decode_pdf.getPages().setHighlightedImage(null);
				lastId = -1;
			}

			if (id == -1 && clickCount > 1) {
				switch (clickCount) {
				case 1: // single click adds caret to page
					/**
					 * Does nothing yet. IF above prevents this case from ever
					 * happening Add Caret code here and add shift click code
					 * for selection. Also remember to comment out
					 * "if(clickCount>1)" from around this switch to activate
					 */
					break;
				case 2: // double click selects line
					final int[][] lineAreas = decode_pdf.getTextLines()
							.getLineAreasAs2DArray(commonValues.getCurrentPage());

					if (lineAreas != null) { // Null is page has no lines
						final int[] point = { commonValues.m_x1, commonValues.m_y1, 1, 1 };
						for (int i = 0; i != lineAreas.length; i++) {
							if (TextLines.intersects(point, lineAreas[i])) {
								decode_pdf.updateCursorBoxOnScreen(lineAreas[i],
										DecoderOptions.highlightColor.getRGB());
								decode_pdf.getTextLines().addHighlights(new int[][] { lineAreas[i] }, false,
										commonValues.getCurrentPage());
							}
						}
					}
					break;
				case 3: // triple click selects paragraph
					final int[] para = decode_pdf.getTextLines().setFoundParagraphAsArray(commonValues.m_x1,
							commonValues.m_y1, commonValues.getCurrentPage());
					if (para != null)
						decode_pdf.updateCursorBoxOnScreen(para, DecoderOptions.highlightColor.getRGB());

					break;
				case 4: // quad click selects page
					currentCommands.executeCommand(Commands.SELECTALL, null);
					break;
				}
			}
			decode_pdf.repaintPane(commonValues.getCurrentPage());
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		// Stub
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		// Stub
	}

	@Override
	public void mousePressed(final MouseEvent e) {

		if (isOtherKey(e)) {
			// remove any outline and reset variables used to track change
			decode_pdf.updateCursorBoxOnScreen(null, 0);
			decode_pdf.getPages().setHighlightedImage(null);
			decode_pdf.getTextLines().clearHighlights();

			// Remove focus from form is if anywhere on pdf panel is clicked
			// mouse dragged
			decode_pdf.requestFocus();

			final int pagenumber = decode_pdf.getPageNumber();
			this.page_data = decode_pdf.getPdfPageData();

			final int crx = page_data.getCropBoxX(pagenumber);
			final int cry = page_data.getCropBoxY(pagenumber);

			commonValues.m_x1 = (int) e.getX() + crx;
			commonValues.m_y1 = (int) e.getY() + cry;

			final int[][] rects = decode_pdf.getTextLines().getHighlightedAreasAs2DArray(commonValues.getCurrentPage());
			if (rects != null && rects.length > 0)
				decode_pdf.getPages().refreshDisplay();
		}

	}

	@Override
	public void mouseReleased(final MouseEvent e) {

		if (contextMenu != null && contextMenu.isShowing())
			contextMenu.hide();

		boolean allowRightButtonClick = currentGUI.getProperties().getValue("allowRightClick").toLowerCase()
				.equals("true");

		if ((e.getButton().equals(MouseButton.SECONDARY)) && allowRightButtonClick) {

			if (contextMenu == null)
				createRightClickMenu();

			contextMenu.show(((BrainGUI) currentGUI).getRoot(), e.getScreenX(), e.getScreenY());

			if (decode_pdf.getPages().getHighlightedImage() == null)
				extractImage.setDisable(true);
			else
				extractImage.setDisable(false);

			if (decode_pdf.getTextLines().getHighlightedAreasAs2DArray(commonValues.getCurrentPage()) == null) {
				extractText.setDisable(true);
				find.setDisable(true);
				// speakHighlighted.setDisable(true);
				// copy.setDisable(true);
			} else {
				extractText.setDisable(false);
				find.setDisable(false);
				// speakHighlighted.setDisable(false);
				// copy.setDisable(false);
			}

		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (isOtherKey(e)) {

			if (!startHighlighting)
				startHighlighting = true;

			// Adjust to center of decode_pdf
			final int pagenumber = decode_pdf.getPageNumber();
			final int crx = decode_pdf.getPdfPageData().getCropBoxX(pagenumber);
			final int cry = decode_pdf.getPdfPageData().getCropBoxY(pagenumber);
			commonValues.m_x2 = (int) e.getX() + crx;
			commonValues.m_y2 = (int) e.getY() + cry;

			if (commonValues.isPDF()) {
				decode_pdf.setCursor(Cursor.TEXT);
				generateNewCursorBox();
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {

	}

	/**
	 * Find and updates coords for the current page
	 * 
	 * @param x
	 *            :: The x coordinate of the cursors location in display area
	 *            coordinates
	 * @param y
	 *            :: The y coordinate of the cursors location in display area
	 *            coordinates
	 * @param page
	 *            :: The page we are currently on
	 * @return Point object of the cursor location in page coordinates
	 */
	public Point getCoordsOnPage(final double x, final double y, final int page) {

		// Update cursor position if over page
		throw new UnsupportedOperationException(
				"We do not currently use this method for JavaFX as it appears the X and Y coords\n"
						+ " can be retrieved from the bottom left in JavaFX compared to the top left in Swing");
	}

	/**
	 * Checks to see whether the primary mouse button or any other key that is
	 * not the secondary mouse button or the middle mouse button is pressed, if
	 * it is then return true, otherwise return false.
	 * 
	 * @param e
	 * @return
	 */
	private static boolean isOtherKey(final MouseEvent e) {

		return e.getButton().equals(MouseButton.PRIMARY) || e.getButton().equals(MouseButton.NONE);
	}

	/**
	 * generate new cursorBox and highlight extractable text, if hardware
	 * acceleration off and extraction on<br>
	 * and update current cursor box displayed on screen
	 */
	protected void generateNewCursorBox() {

		// redraw rect of dragged box on screen if it has changed significantly
		if ((old_m_x2 != -1) || (old_m_y2 != -1) || (Math.abs(commonValues.m_x2 - old_m_x2) > 5)
				|| (Math.abs(commonValues.m_y2 - old_m_y2) > 5)) {

			// allow for user to go up
			int top_x = commonValues.m_x1;
			if (commonValues.m_x1 > commonValues.m_x2)
				top_x = commonValues.m_x2;

			int top_y = commonValues.m_y1;
			if (commonValues.m_y1 > commonValues.m_y2)
				top_y = commonValues.m_y2;

			final int w = Math.abs(commonValues.m_x2 - commonValues.m_x1);
			final int h = Math.abs(commonValues.m_y2 - commonValues.m_y1);

			// add an outline rectangle to the display
			final int[] currentRectangle = { top_x, top_y, w, h };

			// tell JPedal to highlight text in this area (you can add other
			// areas to array)
			decode_pdf.updateCursorBoxOnScreen(currentRectangle, DecoderOptions.highlightColor.getRGB());
			if (!currentCommands.extractingAsImage) {
				final int[] r = { commonValues.m_x1, commonValues.m_y1, commonValues.m_x2 - commonValues.m_x1,
						commonValues.m_y2 - commonValues.m_y1 };

				decode_pdf.getTextLines().addHighlights(new int[][] { r }, false, commonValues.getCurrentPage());
			}
			// reset tracking
			old_m_x2 = commonValues.m_x2;
			old_m_y2 = commonValues.m_y2;

		}
		decode_pdf.repaintPane(commonValues.getCurrentPage());
	}
}