package de.tesis.dynaware.grapheditor.demo.animation;

import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.demo.customskins.DefaultSkinController;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Map;
import java.util.Vector;

public class AnimationManager {

    private GraphEditorContainer        graphEditorContainer;
    private DefaultSkinController       skinController;
    private SimulationFile              simulationFile;
    private ListView<SimulationRecord>  list;
    private ObservableList<SimulationRecord>      items;


    private static final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
    private static final String HIGHLIGHTED_CONTROL_INNER_BACKGROUND = "derive(palegreen, 50%)";



    public AnimationManager(GraphEditorContainer graphEditorContainer, DefaultSkinController skinController) {
        this.graphEditorContainer = graphEditorContainer;
        this.skinController = skinController;

        ListView<SimulationRecord> list = new ListView<>();
        items = FXCollections.observableArrayList();
        list.setItems(items);


        list.prefHeightProperty().bind(
                graphEditorContainer.heightProperty().divide(4)
        );

        list.prefWidthProperty().bind(
                graphEditorContainer.widthProperty().divide(3)
        );

        list.layoutXProperty().bind( graphEditorContainer.widthProperty().subtract( list.prefWidthProperty() ));
        list.layoutYProperty().bind( graphEditorContainer.heightProperty().subtract( list.prefHeightProperty() ));

        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        list.setCellFactory(new Callback<ListView<SimulationRecord>, ListCell<SimulationRecord>>() {
            @Override
            public ListCell<SimulationRecord> call(ListView<SimulationRecord> param) {
                return new ListCell<SimulationRecord>() {
                    @Override
                    protected void updateItem(SimulationRecord item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
                        } else {
                            setText(item.toString());
                            Color taskColor = createColor(item.getTaskNumber());
                            setStyle("-fx-control-inner-background: " + formatColor(taskColor) + ";");
                        }
                    }
                };
            }
        });

        graphEditorContainer.getChildren().add(list);
    }


    private Color createColor(int x) {
        return Color.hsb(x, 1.0, 1.0);
    }

    // Format color as string for CSS (#rrggbb format, values in hex).
    private String formatColor(Color c) {
        int r = (int) (255 * c.getRed());
        int g = (int) (255 * c.getGreen());
        int b = (int) (255 * c.getBlue());

        return String.format("rgb(" + r + "," + g + ", " + b + ")");
    }

    private ListChangeListener<? super SimulationRecord> getSimulationRecordListChangeListener(ListView<SimulationRecord> list) {
        return  observable -> {

//            if (lockEvent || list.getSelectionModel().getSelectedItem() == null) return;
//
//            int taskNumber = list.getSelectionModel().getSelectedItem().getTaskNumber();
//            list.getSelectionModel().clearSelection();
//
//            for ( int index : getIndexesForTask(items, taskNumber) ) {
//                lockEvent = true;
//                list.getSelectionModel().select(index);
//                lockEvent = false;
//            }

        };
    }

    private Vector<Integer> getIndexesForTask(ObservableList<SimulationRecord> items, int taskNumber) {
        Vector<Integer> toBeSelected = new Vector<>();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getTaskNumber() == taskNumber){
                toBeSelected.add(i);
            }
        }

        return toBeSelected;
    }


    /**
     * Initializes the animation manager for the given model.
     *
     */
    public void initialize() {
        simulationFile = new SimulationFile(skinController, "C:\\Users\\Abdelhak khemiri\\IdeaProjects\\DEVS Modeling\\NOC\\output\\out_test_read");

        simulationFile.readSimulationFile();
        simulationFile.buildSequences();
    }


    public void computeAnimationPath() {

        SequentialTransition transition = new SequentialTransition();

        for ( Map.Entry<Double, SimulationSequence> sequence : simulationFile.getSequences().entrySet()) {

            Double currentTime = sequence.getKey();
            SimulationSequence sequenceOfRecords = sequence.getValue();

            ParallelTransition parallelTransition = skinController.generatePathFromSequence(currentTime, sequenceOfRecords);

            parallelTransition.setOnFinished( event -> {
                for ( SimulationRecord record : sequenceOfRecords.getSequence()) {
                    items.add(record);
                }
            } );

            transition.getChildren().add(parallelTransition);
            transition.setCycleCount(1);

        }

        transition.play();

    }



}
