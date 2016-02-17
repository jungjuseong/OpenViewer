/*
 * ---------------
 * ExtractText.java
 * ---------------
 */
package org.jpedal.examples.viewer.commands;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
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


	static String sourcePdf;
	static Stage currentStage;

	public static void extractTextAndCoord(final Values commons, final GUIFactory gui) throws IOException {

		if (commons.getSelectedFile() == null)
			return;

		sourcePdf = commons.getSelectedFile();
		currentStage = (Stage) gui.getFrame();

		showOptionDialog2(sourcePdf, currentStage);

	}

	static void showOptionDialog2(final String fileName, Stage stage) throws IOException {
		
        Parent root = FXMLLoader.load(ExtractTextCoord2.class.getResource("extract_text.fxml"));
        
        optionDialog = new FXDialog(stage, Modality.APPLICATION_MODAL, root, 320, 250);
        
        optionDialog.setTitle("텍스트 좌표 추출하기");
        //stage.setScene(new Scene(root, 320, 120));
        optionDialog.show();
	}
	
	@SuppressWarnings("rawtypes")
	static Task worker;
	
	static String outputFolder;
	static String coordFileToSave;
	static String renderedPdfFile;
	static String pdfTemp;
	static String thumbnailFile;
	static String onlyDotPatternFile;

	static String dotPatternSizeInPaper;
	
	static FXDialog optionDialog;
	static Thread workerThread;

	final static String dotPatternSizes[] = new String[] {"A3", "B4", "A4", "B5"};	
	
	static StripperParam stripperParam;
	final static StripperParam S_Korean = new StripperParam(7.5f, 1.2f);
	final static StripperParam S_TOEIC = new StripperParam(3.0f, 2.0f);
	
	final static StripperParam[] stripperParams = {
			S_Korean, S_TOEIC, S_Korean
	};
	
	static boolean testMode = false;
	
    final static String DONE = "Done!";
    final static String CANCELLED = "Cancelled!";
    
    @FXML private Button extractButton;    
    @FXML private Button closeButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label progressDescription;
    
    @FXML private ChoiceBox<String> bookStyle;
    @FXML private CheckBox testModeCheckbox;
    
    @FXML 
    protected void handleTextExtract(ActionEvent event) {    	

		File SelectedFile = new File(sourcePdf); 
		String job_name = SelectedFile.getName().split("\\.")[0];
		outputFolder = SelectedFile.getParentFile() + "\\" + job_name;
		
		//System.out.println("sourcePdf:" + sourcePdf);
		//System.out.println("pdf File:" + SelectedFile.getName());
		//System.out.println("job:" + SelectedFile.getName().split("\\.")[0]);
		System.out.println("output folder:" + outputFolder);
		
		File outputFolderFile = new File(outputFolder);
		
		if (false == outputFolderFile.exists()) {

			if (false == outputFolderFile.mkdirs()) {
				System.out.println(String.format("Fail to make %s folder", outputFolder));
				progressDescription.setText(String.format("Fail to make %s folder",outputFolder));
				
				return;
			}
		}
		
		coordFileToSave = outputFolder + "\\" + job_name + "-coord.txt";		
		renderedPdfFile = outputFolder + "\\" + job_name + "-rendered.pdf"; 
		thumbnailFile = outputFolder + "\\" + job_name + "-thumbnail.png";
		onlyDotPatternFile = outputFolder + "\\" + job_name + "-dotpattern.pdf";

		pdfTemp = outputFolder + "\\_temp.pdf";
		
		extractButton.setDisable(true);
		//closeButton.setDisable(false);
        
		worker = textExtractorWorker();
		
		worker.messageProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String old, String message) {
				if (message.equals(DONE)) 
					optionDialog.close();
				else 
					progressDescription.setText(message);				
			}
		});		
		
		progressBar.setProgress(0);
		progressBar.progressProperty().unbind();
		progressBar.progressProperty().bind(worker.progressProperty());

		workerThread = new Thread(worker);		
		workerThread.start();
    }
    
    private String chooseDirectory(Stage stage, String initialDirectory) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("저장 디렉토리 선택");
        chooser.setInitialDirectory(new File(initialDirectory));
        
        File selectedDirectory = chooser.showDialog(stage);
        
        if (selectedDirectory != null)
          return selectedDirectory.getAbsolutePath();
               
        return null;
    }    

    @FXML 
    protected void handleClose(ActionEvent event) throws InterruptedException {   
    	//t.sleep(2000);
    	//worker.cancel(true);
    	
		optionDialog.close();
    }
    
    @FXML 
    private ChoiceBox<String> dotPatternSize; // Value injected by FXMLLoader
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert dotPatternSize != null : "fx:id=\"dotPatternSize\" was not injected: check your FXML file ";
        assert extractButton != null : "fx:id=\"extractButton\" was not injected: check your FXML file";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file";
        assert bookStyle != null : "fx:id=\"bookStyle\" was not injected: check your FXML file";
        assert testModeCheckbox != null : "fx:id=\"testModeCheckbox\" was not injected: check your FXML file";

        // Initialize your logic here: all @FXML variables will have been injected
    	dotPatternSize.getItems().clear();
    	dotPatternSize.getItems().addAll("A3","B4","A4","B5");
    	dotPatternSize.getSelectionModel().select(2); 
		dotPatternSizeInPaper = dotPatternSize.getValue();

    	dotPatternSize.getSelectionModel().selectedIndexProperty().addListener(new 
    			ChangeListener<Number>() {
    				public void changed(ObservableValue ob, Number value, Number newValue) {
    					dotPatternSizeInPaper = dotPatternSizes[newValue.intValue()];
    					System.out.println("dotpattern size: " + dotPatternSizeInPaper);
    				}
    			});
    
    
    	bookStyle.getItems().clear();
    	bookStyle.getItems().addAll("국어","영어","기타");
    	bookStyle.getSelectionModel().select(0); 
    	stripperParam = stripperParams[0];

    	bookStyle.getSelectionModel().selectedIndexProperty().addListener(new 
    			ChangeListener<Number>() {
    				public void changed(ObservableValue ob, Number value, Number newValue) {
    					stripperParam = stripperParams[newValue.intValue()];
    					System.out.println("bookStyle: " + bookStyle.getValue());
    				}
    			});    	
    }

    @FXML 
    protected void handleTestModeAction(ActionEvent event) throws InterruptedException {
    	if (testModeCheckbox.isSelected()) {
    		testMode = true;
    	}
    	else
    		testMode = false;
    }

	
	@SuppressWarnings("rawtypes")
	public static Task textExtractorWorker() {
		return new Task() {
			@Override
			protected Object call() throws Exception {

				PDDocument document = PDDocument.load(new File(sourcePdf));
				PrintWriter writer = new PrintWriter(new File(coordFileToSave));

				writer.println(String.format("# source-file: %s", sourcePdf));
				writer.println(String.format("# dotpattern: %s", dotPatternSizeInPaper));
				writer.println(String.format("# pages: %d", document.getNumberOfPages()));

				try {
					for (int page = 1; page <= document.getNumberOfPages(); page++) {
						
						TextInfoExtractor.getTextPositionFromPage(document, stripperParam, page, writer, testMode);
						updateMessage(String.format("Extract %d page", page));
						
						// Now block the thread for a short time, but be sure
						// to check the interrupted exception for cancellation!						
						try {
							Thread.sleep(100);
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage(CANCELLED);
								break;
							}
						}	
						
						updateProgress(page, document.getNumberOfPages());
					}
					writer.close();

					// reset progress bar
					updateProgress(0, document.getNumberOfPages());
					updateMessage("Rendering ...");

					for (int page = 0; page < document.getNumberOfPages(); page++) {
						PageToDotPattern.renderPage(document, page);
						updateMessage(String.format("Rendering %d page", page+1));

						// Now block the thread for a short time, but be sure
						// to check the interrupted exception for cancellation!						
						try {
							Thread.sleep(100);
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage(CANCELLED);
								break;
							}
						}						
						//updateMessage(String.format("%d page.", page));
						updateProgress(page, document.getNumberOfPages());
					}
					
					document.save(pdfTemp); // temporary file

				} 
				catch (final Exception e1) {
					e1.printStackTrace();
				}
				
				document.close();				
				
				// add dot-pattern
				updateMessage("Add dot-pattern ...");

				PageToDotPattern.addPatternImage(pdfTemp, renderedPdfFile, dotPatternSizeInPaper);
				
				// make thumbnail image
				PageToDotPattern.makeDocumentThumbnail(sourcePdf, thumbnailFile, 0, 72);
					 
				new File(pdfTemp).delete();

				// remove all text contents and save ...
				updateMessage("Remove contents to make dot-pattern only...");
				PageToDotPattern.makeBlankDocument(sourcePdf, pdfTemp);
				PageToDotPattern.addPatternImage(pdfTemp, onlyDotPatternFile,dotPatternSizeInPaper);				
				
				updateMessage(DONE);
				
				return true;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				updateMessage(DONE);
			}

			@Override
			protected void cancelled() {
				super.cancelled();
				updateMessage(CANCELLED);
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
