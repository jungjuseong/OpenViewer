package org.jpedal.examples.viewer;

import org.jpedal.PdfDecoderFX;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.jpedal.examples.viewer.gui.BrainGUI;
import org.jpedal.examples.viewer.gui.GUI;
import org.jpedal.examples.viewer.gui.BrainSearchWindow;
import org.jpedal.examples.viewer.gui.javafx.JavaFXThumbnailPanel;
import org.jpedal.examples.viewer.utils.*;
import org.jpedal.examples.viewer.objects.FXClientExternalHandler;
import org.jpedal.external.Options;
import org.jpedal.objects.acroforms.actions.JavaFXDefaultActionHandler;
import org.jpedal.parser.DecoderOptions;
import org.jpedal.utils.LogWriter;
import org.jpedal.utils.Messages;

// Main Viewer - customized from OpenViewerFX 
public class BrainViewer extends SharedViewer implements ViewerInt{

    public static boolean isOpenFX = OpenViewerFX.class.getResourceAsStream("/org/jpedal/examples/viewer/BrainViewer.class")!= null;
    
    static {
        checkUserJavaVersion();
        isFX = true;
    }

    private Stage stage;
    private String[] args;

    /**
     * Base Constructor to use when starting Viewer as a stand alone Stage.
     * Please ensure you call setupViewer after calling this constructor.
     * 
     * @param stage Is of type Stage
     * @param args Program arguments passed into the Viewer
     */
    public BrainViewer(final Stage stage, final String[] args) {
        this.stage = stage;
        this.args = args;
        init();
    }
    

    /**
     * Sets up the Viewer and Displays it.
     * 
     * Please make sure you call this method
     * after calling any constructor to initialise
     * key components.
     */
    @Override
    public void setupViewer() {
        
        super.setupViewer();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                handleArguments(args);
                if (stage != null) {
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(final WindowEvent t) {
                            currentCommands.executeCommand(Commands.EXIT, null);
                        }
                    });
                }
            }
        });
    }
    
    void init() {
        
        //load locale file
        try {
            Messages.setBundle(ResourceBundle.getBundle("org.jpedal.international.messages"));
        } catch (final Exception e) {
            
            LogWriter.writeLog("Exception " + e + " loading resource bundle.\n"
                    + "Also check you have a file in org.jpedal.international.messages to support Locale=" + java.util.Locale.getDefault());
        }
         
        if(!OpenViewerFX.isOpenFX){
            currentPrinter=new FXPrinter();
        }

        decode_pdf = new PdfDecoderFX();

        thumbnails = new JavaFXThumbnailPanel(decode_pdf);

        currentGUI = new BrainGUI(stage, decode_pdf, commonValues, thumbnails, properties);
        
        decode_pdf.addExternalHandler(new JavaFXDefaultActionHandler(currentGUI), Options.FormsActionHandler);
        decode_pdf.addExternalHandler(new FXClientExternalHandler(), Options.AdditionalHandler);
        
        if(GUI.debugFX) {
            System.out.println("BrainViewer init()");
        }

        searchFrame = new BrainSearchWindow(currentGUI);

        currentCommands = new BrainCommands(commonValues, currentGUI, decode_pdf,
                thumbnails, properties, searchFrame, currentPrinter);
        
                
		//enable error messages which are OFF by default
		DecoderOptions.showErrorMessages=true;
		
		properties.loadProperties();

    }
    
    /**
     * Allows the viewer to handle any JVM/Program arguments.
     *
     * @param args :: Program arguments passed into the Viewer.
     */
    @Override
    public void handleArguments(final String[] args) {

        //Ensure default open is on event thread, otherwise the display is updated as values are changing
        if (Platform.isFxApplicationThread()) {
            if (args !=null && args.length > 0) {
                openDefaultFile(args[0]);

            } else if ((properties.getValue("openLastDocument").toLowerCase().equals("true")) &&
                 (properties.getRecentDocuments() != null
                        && properties.getRecentDocuments().length > 1)) {

                    int lastPageViewed = Integer.parseInt(properties.getValue("lastDocumentPage"));

                    if (lastPageViewed < 0) {
                        lastPageViewed = 1;
                    }

                    openDefaultFileAtPage(properties.getRecentDocuments()[0], lastPageViewed);
                }
            
        } else {

            final Runnable run = new Runnable() {

                @Override
                public void run() {
                    if (args.length > 0) {
                        openDefaultFile(args[0]);

                    } else if ((properties.getValue("openLastDocument").toLowerCase().equals("true")) &&
                         (properties.getRecentDocuments() != null
                                && properties.getRecentDocuments().length > 1)){

                            int lastPageViewed = Integer.parseInt(properties.getValue("lastDocumentPage"));

                            if (lastPageViewed < 0) {
                                lastPageViewed = 1;
                            }

                            openDefaultFileAtPage(properties.getRecentDocuments()[0], lastPageViewed);
                        }
                    }
            };
            Platform.runLater(run);
        }
    }

    /**
     * Releases all resources used by the viewer - <em>Please note this calls System.exit(0);</em>
     * This method has to be called when you explicitly want 
     * to close the viewer.
     */
    public void close(){
        SharedViewer.closeCalled = true;
        currentCommands.executeCommand(Commands.EXIT, null);
    }
    
    
    /**
     * 
     * Not part of the API.
     * 
     * To add Viewer to your own parent node, please use:
     * OpenViewerFX(Parent parentPane, String preferencesPath).
     * 
     * @return The root of your JavaFX application
     */
    public BorderPane getRoot(){
        return ((BrainGUI)currentGUI).getRoot();
    }
    
    protected static void checkUserJavaVersion(){ 
        if (Float.parseFloat(System.getProperty("java.specification.version")) < 1.8f) {
            throw new RuntimeException("To run the JPedal FX Viewer, you must have Java8 or above installed");
         }
    }
}
