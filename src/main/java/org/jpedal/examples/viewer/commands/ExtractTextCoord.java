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
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
import org.jpedal.utils.Messages;

import net.bookinaction.TextInfoExtractor;
import net.bookinaction.model.StripperParam;

/**
 * Class to Handle the pop-up dialogs created when user right clicks 
 * highlighted text and chooses text extraction.
 */
public class ExtractTextCoord extends GUIExtractText {

    public static void execute(final Object[] args, final GUIFactory currentGUI, final PdfDecoderInt decode_pdf, final Values commonValues) {
        if (args == null) {
        	try {
				extractTextAndCoord(currentGUI, commonValues);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {

        }
    }
    
    final static StripperParam S_Korean = new StripperParam( 7.5f, 1.2f);
    final static StripperParam S_TOEIC = new StripperParam(3.0f, 2.0f);
    
	static Task copyWorker;
    
	public static void extractTextAndCoord(final GUIFactory currentGUI, final Values commonValues) throws IOException {  	
    	
    	if (commonValues.getSelectedFile() == null)
    		return; 
    	
        File file;
        String fileToSave;
        boolean finished = false;
        
        while (!finished) {

            // Create a FileChooser object to save file as selected file type.
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TEXT file (*.txt)", "*.txt"));
            
            File SelectedFile = new File(commonValues.getSelectedFile());
            
            final String fileName = SelectedFile.getName();
            
            if(fileName != null){
                File existDirectory = SelectedFile.getParentFile();
                fileChooser.setInitialDirectory(existDirectory);
            }
            
            String withoutExtensionFile = fileName.split("\\.")[0];
            fileChooser.setInitialFileName(withoutExtensionFile + "-coord");

            /**
             * Begin file-save process.
             */
            file = fileChooser.showSaveDialog((Stage)currentGUI.getFrame());

            if (file != null) {

                fileToSave = file.getAbsolutePath();

                if (!fileToSave.endsWith(".txt")) {
                    fileToSave += ".txt";
                    file = new File(fileToSave);
                }

                if (fileToSave.equals(commonValues.getSelectedFile()))
                    return;                           
              
                final BorderPane border = new BorderPane();
                final FXDialog textExtractionOptions = new FXDialog((Stage)currentGUI.getFrame(), Modality.APPLICATION_MODAL, border, 400, 200);
                textExtractionOptions.setTitle("텍스트 추출 옵션 - " + fileName);

                final HBox progressBox = new HBox();
                final Label progressLabel = new Label("진행 ...");
                final ProgressBar progressBar = new ProgressBar(0);
                
                progressBox.setSpacing(5);
                progressBox.setAlignment(Pos.CENTER);
                progressBox.getChildren().addAll(progressLabel, progressBar);
                border.setCenter(progressBox);
                                
                //Setup bottom Radio Buttons and Bottom Buttons
                final VBox allBottom = new VBox();
                final HBox bottomButtons = new HBox(15);
                final HBox bottomRadioButtons = new HBox();
                final RadioButton extractImageCoord = new RadioButton("이미지 좌표만   ");
                extractImageCoord.setId("imageOnly");

                final RadioButton extractTextCoord = new RadioButton("텍스트 좌표만");
                extractTextCoord.setId("textOnly");

                final RadioButton extractTextAndImageCoord = new RadioButton("텍스트와 이미지 좌표");
                extractTextAndImageCoord.setId("textAndImage");

                //final Button helpButton = new Button("도움말");
                final Button closeButton = new Button("닫기");
                
                closeButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                    @Override
                    public void handle(final javafx.event.ActionEvent e) {
                        textExtractionOptions.close();
                    }
                });
                
                final Button extractButton = new Button("추출하기");
                extractButton.setVisible(true); //currently disabled button until window is coded.
                
                final Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);                
                bottomRadioButtons.getChildren().addAll(extractImageCoord,  extractTextCoord,  extractTextAndImageCoord);
                
                bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
                bottomButtons.getChildren().addAll(closeButton, extractButton);
                allBottom.getChildren().addAll(bottomRadioButtons, bottomButtons);
                
                extractImageCoord.setPadding(new Insets(0,0,0,10));
                extractTextCoord.setPadding(new Insets(0,10,13,0));
                extractTextAndImageCoord.setPadding(new Insets(0,20,26,0));
                
                bottomButtons.setPadding(new Insets(0,5,10,0));

                //Add format buttons to a toggle group, so only one be selected.
                final ToggleGroup formatGroup = new ToggleGroup();
                final ChangeListener<Toggle> updateSelectionListener = new ChangeListener<Toggle>() {
                    @Override
                    public void changed(final ObservableValue<? extends Toggle> ov,
                            final Toggle old_toggle, final Toggle new_toggle) {
                    }
                };

                formatGroup.selectedToggleProperty().addListener(updateSelectionListener);

                //Add extraction mode to a toggle group, so only one be selected.
                final ToggleGroup extractionGroup = new ToggleGroup();
                extractTextAndImageCoord.setSelected(true);
                extractionGroup.getToggles().addAll(extractImageCoord, extractTextCoord, extractTextAndImageCoord);

                extractionGroup.selectedToggleProperty().addListener(updateSelectionListener);
                
                final TextInfoExtractor extractor = new TextInfoExtractor(fileToSave, S_Korean);  
			
                final PDDocument document = PDDocument.load(new File(commonValues.getSelectedFile()));
                final PrintWriter  writer = new PrintWriter(new File(fileToSave));	

                extractButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent t) {
                    	extractButton.setDisable(true);
                        progressBar.setProgress(0);
                        closeButton.setDisable(false);
                        
                        copyWorker = createWorker(extractor, document, writer);
                        
                        progressBar.progressProperty().unbind();
                        progressBar.progressProperty().bind(copyWorker.progressProperty());
                        
                        copyWorker.messageProperty().addListener(new ChangeListener<String>() {
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                System.out.println(newValue);
                            }
                        });
                        new Thread(copyWorker).start();                      

                    }
                });

                border.setBottom(allBottom);
               
                BorderPane.setMargin(bottomRadioButtons, new Insets(10,10,10,10));
                BorderPane.setMargin(bottomButtons, new Insets(10,10,10,10));
                
                textExtractionOptions.show();         
            }
            finished = true;
        }
    }
    
    @SuppressWarnings("rawtypes")
	public static Task createWorker(TextInfoExtractor extractor, final PDDocument document, final PrintWriter writer) {
        return new Task() {
            @Override
            
            protected Object call() throws Exception {

                try {
                    for (int i = 0; i <= document.getNumberOfPages(); i++) {
                    	extractor.getTextPositionFromPage(document, i+1, writer);
                        //updateMessage(String.format("%d page", i+1));
                        updateProgress(i + 1, document.getNumberOfPages());
                    }
                    
                } catch (final Exception e1) {
                   //currentGUI.showMessageDialog(Messages.getMessage("PdfViewerException.NotSaveInternetFile")+' '+e1);
                }
     
                if (document != null) {
                    //document.save(new_file);
                    document.close();
                }
                if (writer != null)
                    writer.close();
                
                return true;
            }
        };
    }
    
}
