package org.jpedal.examples.viewer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jpedal.examples.viewer.Commands;
import org.jpedal.external.JPedalActionHandler;
import org.jpedal.external.Options;
import org.jpedal.gui.GUIFactory;
import org.jpedal.parser.DecoderOptions;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * startup our Viewer as Application and allow user to access Viewer
 */
public class BrainStartup extends Application {
	private ProgressBar loadProgress;
	private Label progressText;

	BrainViewer viewer;

	private Pane splashLayout;
	private final Stage splashStage = new Stage();
	private static final int SPLASH_WIDTH = 600;
	private static final int SPLASH_HEIGHT = 200;

	public static void main(String[] args) {
		DecoderOptions.javaVersion = Float.parseFloat(System.getProperty("java.specification.version"));
		checkUserJavaVersion();

		launch(args);
	}

	private static void checkUserJavaVersion() {
		if (Float.parseFloat(System.getProperty("java.specification.version")) < 1.8f) 
			throw new RuntimeException("이 프로그램은 Java 8 버전의 설치가 필요 합니다");
		
	}

	@Override
	// Initialize the stage for the SplashScreen
	public void init() {
		final String imgPath = "/net/bookinaction/viewer/res/OSFXSplash.png";
		final String barColour = ("-fx-accent: green;");
		final ImageView splash = new ImageView(getClass().getResource(imgPath).toExternalForm());

		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH);
		loadProgress.setStyle(barColour);

		progressText = new Label("All modules are loaded");
		progressText.setAlignment(Pos.CENTER);

		splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash, loadProgress, progressText);
		splashLayout.setEffect(new DropShadow());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage initStage) {
		final Task<ObservableList<String>> loadModsTask = new Task() {
			@Override
			protected ObservableList<String> call() throws InterruptedException {
				final ObservableList<String> loadMods = FXCollections.observableArrayList();
				final ObservableList<String> friends = FXCollections.observableArrayList("네트워크 모듈", "유저 모둘", "UI",
						"유저 콘트롤");

				updateMessage("로딩 . . .");
				for (int i = 0; i < friends.size(); i++) {
					Thread.sleep(900);
					updateProgress(i + 1, friends.size());
					final String nextFriend = friends.get(i);
					loadMods.add(nextFriend);
					updateMessage("로딩 . . .  " + nextFriend);
				}
				Thread.sleep(500);
				updateMessage("모듈 로드 완료.");

				return loadMods;
			}

		};

		showSplash(loadModsTask);

		loadModsTask.setOnSucceeded(new EventHandler() {
			@Override
			public void handle(final Event event) {
				startNew(initStage);
			}
		});
		new Thread(loadModsTask).start();
	}

	// starting the Viewer Stage
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void startNew(final Stage stage) {

		final List<String> args = this.getParameters().getUnnamed();

		viewer = new BrainViewer(stage, args.toArray(new String[args.size()]));
		viewer.setupViewer();

		//
		/** create a new JPedalActionHandler implementation */
		final JPedalActionHandler aboutAction = new JPedalActionHandler() {
			@Override
			public void actionPerformed(final GUIFactory currentGUI, final Commands commands) {
				currentGUI.showMessageDialog("PDF문서에서 텍스트 토큰과 이미지를 추출하는 프로그램입니다", "About bookinaction", JOptionPane.INFORMATION_MESSAGE);
			}
		};
		
		/**
		 * add the implementation to a Map, with its corresponding command, in
		 * this case Commands.HELP
		 */
		final Map actions = new HashMap();
		actions.put(Commands.ABOUT, aboutAction);

		/** pass the map into the external handler */
		viewer.addExternalHandler(actions, Options.JPedalActionHandler);

		/** display the Viewer */
		System.out.println("displayViewer()");
	}

	// Starting the splash screen
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void showSplash(final Task task) {
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());

		task.stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(final ObservableValue<? extends Worker.State> observableValue,
					final Worker.State oldState, final Worker.State newState) {
				if (newState == Worker.State.SUCCEEDED) {
					loadProgress.progressProperty().unbind();
					loadProgress.setProgress(1);
					splashStage.toFront();

					final FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
					fadeSplash.setFromValue(1.0);
					fadeSplash.setToValue(0.0);
					fadeSplash.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent actionEvent) {
							splashStage.hide();
						}
					});
					fadeSplash.play();
				}
			}

		});

		final Scene splashScene = new Scene(splashLayout);
		splashStage.initStyle(StageStyle.UNDECORATED);

		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		splashStage.setScene(splashScene);
		splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		splashStage.show();
	}

	public BrainViewer getViewer() {
		return viewer;
	}
}
