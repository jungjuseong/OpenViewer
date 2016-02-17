/*
 * ---------------
 * ExtractText.java
 * ---------------
 */
package org.jpedal.examples.viewer.commands;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.jpedal.PdfDecoderInt;
import org.jpedal.examples.viewer.Values;
import org.jpedal.examples.viewer.commands.generic.GUIExtractText;
import org.jpedal.examples.viewer.gui.javafx.dialog.FXDialog;
import org.jpedal.gui.GUIFactory;

import com.sun.glass.ui.Application;

import net.bookinaction.PageToDotPattern;
import net.bookinaction.TextInfoExtractor;
import net.bookinaction.model.StripperParam;

/**
 * Class to Handle the pop-up dialogs created when user right clicks highlighted
 * text and chooses text extraction.
 */
public class ExtractTextCoord2 extends GUIExtractText {

	public static void execute(Object[] args, GUIFactory gui, PdfDecoderInt decode_pdf, Values commons) {
		try {
			extractTextAndCoord(commons, gui);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	final static StripperParam S_Korean = new StripperParam(7.5f, 1.2f);
	final static StripperParam S_TOEIC = new StripperParam(3.0f, 2.0f);

	static String sourcePdf;
	static Stage currentStage;

	public static void extractTextAndCoord(final Values commons, final GUIFactory gui) throws IOException {

		if (commons.getSelectedFile() == null)
			return;

		sourcePdf = commons.getSelectedFile();
		currentStage = (Stage) gui.getFrame();

		showOptionDialog2(sourcePdf, currentStage);

	}

	@SuppressWarnings("rawtypes")
	static Task worker;
	
	static String coordFileToSave;
	static String renderedPdfFile;
	static String dotPatternSizeInPaper;
	
	static FXDialog optionDialog;
	static Thread t;

	final static String dotPatternSizes[] = new String[] {"A3", "B4", "A4", "B5"};			
	
    @FXML private Button extractButton;    
    @FXML private Button closeButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    @FXML 
    protected void handleTextExtract(ActionEvent event) {    	
    	
		worker = textExtractorWorker();
		worker.messageProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String old, String message) {

				if (message.equals("Done!")) {
					optionDialog.close();
				} else {
					progressLabel.setText(message);
				}
			}
		});
		
		coordFileToSave = chooseOutputFile();
		renderedPdfFile = renameFileExtension(coordFileToSave, ".pdf");
		
		extractButton.setDisable(true);
		//closeButton.setDisable(false);
        
		progressBar.setProgress(0);
		progressBar.progressProperty().unbind();
		progressBar.progressProperty().bind(worker.progressProperty());

		Thread t = new Thread(worker);
		
		t.start();
    }


    @FXML 
    protected void handleClose(ActionEvent event) throws InterruptedException {   
    	//t.sleep(2000);
    	worker.cancel(true);
    	
		optionDialog.close();
    }
    
    @FXML 
    private ComboBox<String> dotPatternSize; // Value injected by FXMLLoader
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert dotPatternSize != null : "fx:id=\"dotPatternSize\" was not injected: check your FXML file ";
        assert extractButton != null : "fx:id=\"extractButton\" was not injected: check your FXML file";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file";

        // Initialize your logic here: all @FXML variables will have been injected
    	dotPatternSize.getItems().clear();
    	dotPatternSize.getItems().addAll("A3","B4","A4","B5");
		
    	dotPatternSize.getSelectionModel().selectedIndexProperty().addListener(new 
    			ChangeListener<Number>() {
    				public void changed(ObservableValue ob, Number value, Number newValue) {
    					dotPatternSizeInPaper = dotPatternSizes[newValue.intValue()];
    					System.out.println("dotpattern size: " + dotPatternSizeInPaper);
    				}
    			});
    	
        //text.setText("List : ");
    }
    
	static void showOptionDialog2(final String fileName, Stage stage) throws IOException {
		
        Parent root = FXMLLoader.load(ExtractTextCoord2.class.getResource("extract_text.fxml"));
        
        optionDialog = new FXDialog(stage, Modality.APPLICATION_MODAL, root, 320, 150);
        
        optionDialog.setTitle("텍스트 좌표 추출하기");
        //stage.setScene(new Scene(root, 320, 120));
        optionDialog.show();
	}
	
	
	static void showOptionDialog(final String fileName, Stage stage) {

		final BorderPane border = new BorderPane();
		final FXDialog optionDialog = new FXDialog(stage, Modality.APPLICATION_MODAL, border, 400, 200);
		optionDialog.setTitle("텍스트 추출 옵션 - " + fileName);

		final HBox progressBox = new HBox();
		final Label progressLabel = new Label("진행 ...");
		final ProgressBar progressBar = new ProgressBar(0);

		progressBox.setSpacing(5);
		progressBox.setAlignment(Pos.CENTER);
		progressBox.getChildren().addAll(progressLabel, progressBar);
		border.setCenter(progressBox);

		// Dot Pattern size
		final Label choiceLabel = new Label("닷패턴크기: ");
		
		final ChoiceBox<String> choiceBox = new ChoiceBox<String>(FXCollections.observableArrayList("A3", "B4", "A4", "B5"));
		choiceBox.setTooltip(new Tooltip("도트 패턴 크기 고르기"));
		choiceBox.setValue("A4");
		
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new 
			ChangeListener<Number>() {
				public void changed(ObservableValue ob, Number value, Number newValue) {
					dotPatternSizeInPaper = dotPatternSizes[newValue.intValue()];
				}
			});
		choiceBox.getSelectionModel().select(2);
		HBox hb = new HBox();
		hb.getChildren().addAll(choiceLabel, choiceBox);
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(20, 0, 30, 20));
		

		// Color Chooser
	    // create a new color chooser sized to the stage.

		// Buttons
		final HBox bottomButtons = new HBox(15);
		final Button closeButton = new Button("닫기");
		closeButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
			@Override
			public void handle(final javafx.event.ActionEvent e) {
				optionDialog.close();
			}
		});

		final Button extractButton = new Button("추출하기");
		extractButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent t) {

				worker = textExtractorWorker();
				worker.messageProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String old, String message) {

						if (message.equals("Done!")) {
							optionDialog.close();
						} else {
							progressLabel.setText(message);
						}
					}
				});
				
				coordFileToSave = chooseOutputFile();
				renderedPdfFile = renameFileExtension(coordFileToSave, ".pdf");
				
				extractButton.setDisable(true);
				closeButton.setDisable(false);

				progressBar.setProgress(0);
				progressBar.progressProperty().unbind();
				progressBar.progressProperty().bind(worker.progressProperty());

				new Thread(worker).start();

			}
		});
		extractButton.setVisible(true); 

		bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
		bottomButtons.getChildren().addAll(closeButton, extractButton);
		
		final VBox allBottom = new VBox();
		allBottom.getChildren().addAll(hb, bottomButtons);

		bottomButtons.setPadding(new Insets(0, 5, 10, 0));

		border.setBottom(allBottom);
		BorderPane.setMargin(bottomButtons, new Insets(10, 10, 10, 10));

		optionDialog.show();
	}

	@SuppressWarnings("rawtypes")
	public static Task textExtractorWorker() {
		return new Task() {
			@Override
			protected Object call() throws Exception {

				PDDocument document = PDDocument.load(new File(sourcePdf));
				PrintWriter writer = new PrintWriter(new File(coordFileToSave));

				try {
					for (int page = 1; page <= document.getNumberOfPages(); page++) {
						
						TextInfoExtractor.getTextPositionFromPage(document, S_Korean, page, writer);
						updateMessage(String.format("%d extract", page));

						PageToDotPattern.renderPage(document, page - 1);
						updateMessage(String.format("%d rendering", page));

						// Now block the thread for a short time, but be sure
						// to check the interrupted exception for cancellation!
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage("Cancelled");
								break;
							}
						}
						
						//updateMessage(String.format("%d page.", page));
						updateProgress(page, document.getNumberOfPages());
					}
				} 
				catch (final Exception e1) {
					e1.printStackTrace();
				}

				if (writer != null)
					writer.close();
				
				String pdfTemp = renderedPdfFile + "_temp.pdf";

				if (document != null) {
					document.save(pdfTemp); // temporary file
					document.close();
				}
				
				updateMessage(String.format("add dot-pattern ..."));

				// add dot-pattern
				try {
					PageToDotPattern.addPatternImage(pdfTemp, renderedPdfFile, dotPatternSizeInPaper);					
					
					File pdf = new File(pdfTemp); // remove file
					pdf.delete();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				updateMessage("Done!");

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

	public static String chooseOutputFile() {

		String fileToSave = null;

		boolean finished = false;
		while (!finished) {

			// Create a FileChooser object to save file as selected file type.
			final FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TEXT file (*.txt)", "*.txt"));

			File SelectedFile = new File(sourcePdf); 

			if (SelectedFile.getName() != null) {
				File existDirectory = SelectedFile.getParentFile();
				fileChooser.setInitialDirectory(existDirectory);
			}

			String withoutExtensionFile = SelectedFile.getName().split("\\.")[0];
			fileChooser.setInitialFileName(withoutExtensionFile + "-coord");

			// Begin file-save process.
			File chosenFile = fileChooser.showSaveDialog(currentStage);

			if (chosenFile != null) {

				fileToSave = chosenFile.getAbsolutePath();

				if (!fileToSave.endsWith(".txt")) {
					fileToSave += ".txt";
					chosenFile = new File(fileToSave);
				}

				return fileToSave;

			}
			finished = true;
		}

		return fileToSave;
	}
	
	public static String renameFileExtension(String source, String newExtension) {
		String target;
		String currentExtension = getFileExtension(source);

		if (currentExtension.equals("")) {
			target = source + "." + newExtension;
		} else {
			target = source.replaceAll("." + currentExtension, newExtension);
		}
		return target;
	}

	private static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
