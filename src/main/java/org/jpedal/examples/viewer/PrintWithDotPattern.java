package org.jpedal.examples.viewer;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.jpedal.examples.viewer.gui.javafx.dialog.FXDialog;
import org.jpedal.gui.GUIFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.bookinaction.PageToDotPattern;

public class PrintWithDotPattern {

	public static void execute(final Object[] args, final GUIFactory gui, final Values commons) {

		final BorderPane border = new BorderPane();
		final FXDialog RenderOptionDialog = new FXDialog((Stage) gui.getFrame(), Modality.APPLICATION_MODAL, border,
				400, 200);

		RenderOptionDialog.setTitle("패턴 프린트 옵션 - ");

		final HBox progressBox = new HBox();
		final Label progressLabel = new Label("진행 ...");
		final ProgressBar progressBar = new ProgressBar(0);

		progressBox.setSpacing(5);
		progressBox.setAlignment(Pos.CENTER);
		progressBox.getChildren().addAll(progressLabel, progressBar);
		border.setCenter(progressBox);

		// Setup bottom Radio Buttons and Bottom Buttons
		final VBox allBottom = new VBox();
		final HBox bottomButtons = new HBox(15);
		final HBox bottomRadioButtons = new HBox();

		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		final RadioButton removeBlack = new RadioButton("검정색 빼기 ");
		removeBlack.setUserData("removeBlack");

		final RadioButton originalColor = new RadioButton("원래대로");
		originalColor.setUserData("originalColor");

		bottomRadioButtons.getChildren().addAll(removeBlack, originalColor);
		removeBlack.setPadding(new Insets(0, 0, 0, 10));
		originalColor.setPadding(new Insets(0, 10, 13, 0));

		bottomButtons.setPadding(new Insets(0, 5, 10, 0));

		// Add extraction mode to a toggle group, so only one be selected.
		final ToggleGroup colorGroup = new ToggleGroup();
		removeBlack.setSelected(true);
		colorGroup.getToggles().addAll(removeBlack, originalColor);
		
		
		final Button startButton = new Button("시작");
		startButton.setVisible(true);
		
		bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
		bottomButtons.getChildren().addAll(startButton);
		allBottom.getChildren().addAll(bottomRadioButtons, bottomButtons);

		border.setBottom(allBottom);

		BorderPane.setMargin(bottomRadioButtons, new Insets(10, 10, 10, 10));
		BorderPane.setMargin(bottomButtons, new Insets(10, 10, 10, 10));
		
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {

				boolean isRemoveBlack = colorGroup.getSelectedToggle().getUserData().toString().equals("removeBlack");

				if (isRemoveBlack) {
					Task renderWorker = null;

					startButton.setDisable(true);
					progressBar.setProgress(0);

					String tempFile = commons.getInputDir() + "__tmp.pdf";

					renderWorker = createWorker(commons.getSelectedFile(), tempFile);
					progressBar.progressProperty().bind(renderWorker.progressProperty());

					renderWorker.messageProperty().addListener(new ChangeListener<String>() {
						public void changed(ObservableValue<? extends String> observable, String old, String message) {

							if (message.equals("Done!")) {
								RenderOptionDialog.close();
								printFile(commons.getInputDir() + "__tmp.pdf");
							} 
							else {
								progressLabel.setText(message);
							}
						}
					});
					new Thread(renderWorker).start();
				} 
				else {					
					RenderOptionDialog.close();
					printFile(commons.getSelectedFile());
				}

			}
		});



		RenderOptionDialog.show();
		


	}


	private static void printFile(String filename) {
	
		PDDocument document = null;
		String fileForPrint = System.getProperty("user.dir") + "/_temp.pdf";
		try {
			
			PageToDotPattern.addPatternImage(filename, fileForPrint, "A4");

			document = PDDocument.load(new File(fileForPrint));
			printWithDialog(document);
			document.close();
			
		} catch (IOException | PrinterException e1) {
			e1.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Task createWorker(String job_file, String render_pdf) {
		return new Task() {
			@Override
			protected Boolean call() throws Exception {
				PDDocument document = null;

				try {
					document = PDDocument.load(new File(job_file));

					for (int page = 0; page < document.getNumberOfPages(); page++) {
						if (isCancelled()) {
							updateMessage("Cancelled");
							break;
						}
						PageToDotPattern.renderPage(document, page);
						updateMessage((page + 1) + "/" + document.getNumberOfPages() + " page");
						updateProgress(page + 1, document.getNumberOfPages());

						// Now block the thread for a short time, but be sure
						// to check the interrupted exception for cancellation!
						try {
							Thread.sleep(100);
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage("Cancelled");
								break;
							}
						}

					}
					document.save(render_pdf);

				} finally {
					document.close();
				}
				return true;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				updateMessage("Done!");
			}

			@Override
			protected void cancelled() {
				super.cancelled();
				updateMessage("Cancelled!");
			}
		};
	}

	/**
	 * Prints the document at its actual size. This is the recommended way to
	 * print.
	 */
	private static void print(PDDocument document) throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));
		job.print();
	}

	/**
	 * Prints using custom PrintRequestAttribute values.
	 */
	private static void printWithAttributes(PDDocument document) throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));

		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new PageRanges(1, 1)); // pages 1 to 1

		job.print(attr);
	}

	/**
	 * Prints with a print preview dialog.
	 */
	private static void printWithDialog(PDDocument document) throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));

		if (job.printDialog()) {
			job.print();
		}
	}

	/**
	 * Prints with a print preview dialog and custom PrintRequestAttribute
	 * values.
	 */
	private static void printWithDialogAndAttributes(PDDocument document) throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));

		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new PageRanges(3, 3)); // pages 1 to 1

		if (job.printDialog(attr)) {
			job.print(attr);
		}
	}

	/**
	 * Prints using a custom page size and custom margins.
	 */
	private static void printWithPaper(PDDocument document) throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));

		// define custom paper
		Paper paper = new Paper();
		paper.setSize(306, 396); // 1/72 inch
		paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());

		// custom page format
		PageFormat pageFormat = new PageFormat();
		pageFormat.setPaper(paper);

		// override the page format
		Book book = new Book();
		// append all pages
		book.append(new PDFPrintable(document), pageFormat, document.getNumberOfPages());
		job.setPageable(book);

		job.print();
	}
}