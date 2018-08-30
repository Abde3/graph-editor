package de.tesis.dynaware.grapheditor.demo.customskins;

import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.core.utils.GModelUtils;
import de.tesis.dynaware.grapheditor.demo.animation.SimulationRecord;
import de.tesis.dynaware.grapheditor.demo.animation.SimulationSequence;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.animation.ParallelTransition;
import javafx.geometry.Side;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.skins.defaults.utils.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.demo.selections.SelectionCopier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;


/**
 * Responsible for default-skin specific logic in the graph editor demo.
 */
public class DefaultSkinController implements SkinController {

    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;

    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;

    private static final int MAX_CONNECTOR_COUNT = 5;

    /**
     * Creates a new {@link DefaultSkinController} instance.
     * 
     * @param graphEditor the graph editor on display in this demo
     * @param graphEditorContainer the graph editor container on display in this demo
     */
    public DefaultSkinController(final GraphEditor graphEditor, final GraphEditorContainer graphEditorContainer) {

        this.graphEditor = graphEditor;
        this.graphEditorContainer = graphEditorContainer;
    }

    @Override
    public void activate() {
        // Nothing to do
    }

    @Override
    public void addNode(final double currentZoomFactor) {

        final double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;

        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setY(NODE_INITIAL_Y + windowYOffset);

        final GConnector rightOutput = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(rightOutput);

        final GConnector leftInput = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(leftInput);

        node.setX(NODE_INITIAL_X + windowXOffset);

        rightOutput.setType(DefaultConnectorTypes.RIGHT_OUTPUT);
        leftInput.setType(DefaultConnectorTypes.LEFT_INPUT);

        Commands.addNode(graphEditor.getModel(), node);
    }

    /**
     * Adds a connector of the given type to all nodes that are currently selected.
     *
     * @param position the position of the new connector
     * @param input {@code true} for input, {@code false} for output
     */
    @Override
    public void addConnector(final Side position, final boolean input) {

        final String type = getType(position, input);

        final GModel model = graphEditor.getModel();
        final SkinLookup skinLookup = graphEditor.getSkinLookup();
        final CompoundCommand command = new CompoundCommand();
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        for (final GNode node : model.getNodes()) {

            if (skinLookup.lookupNode(node).isSelected()) {
                if (countConnectors(node, position) < MAX_CONNECTOR_COUNT) {

                    final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
                    connector.setType(type);

                    final EReference connectors = GraphPackage.Literals.GNODE__CONNECTORS;
                    command.append(AddCommand.create(editingDomain, node, connectors, connector));
                }
            }
        }

        if (command.canExecute()) {
            editingDomain.getCommandStack().execute(command);
        }
    }

    @Override
    public void clearConnectors() {
        Commands.clearConnectors(graphEditor.getModel(), graphEditor.getSelectionManager().getSelectedNodes());
    }

    @Override
    public void handlePaste(final SelectionCopier selectionCopier) {
    	selectionCopier.paste(null);
    }

    @Override
    public void handleSelectAll() {
    	graphEditor.getSelectionManager().selectAll();
    }

    /**
     * Counts the number of connectors the given node currently has of the given type.
     *
     * @param node a {@link GNode} instance
     * @param side the {@link Side} the connector is on
     * @return the number of connectors this node has on the given side
     */
    private int countConnectors(final GNode node, final Side side) {

        int count = 0;

        for (final GConnector connector : node.getConnectors()) {
            if (side.equals(DefaultConnectorTypes.getSide(connector.getType()))) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the connector type string corresponding to the given position and input values.
     * 
     * @param position a {@link Side} value
     * @param input {@code true} for input, {@code false} for output
     * @return the connector type corresponding to these values
     */
    private String getType(final Side position, final boolean input) {

        switch (position) {
        case TOP:
            if (input) {
                return DefaultConnectorTypes.TOP_INPUT;
            } else {
                return DefaultConnectorTypes.TOP_OUTPUT;
            }
        case RIGHT:
            if (input) {
                return DefaultConnectorTypes.RIGHT_INPUT;
            } else {
                return DefaultConnectorTypes.RIGHT_OUTPUT;
            }
        case BOTTOM:
            if (input) {
                return DefaultConnectorTypes.BOTTOM_INPUT;
            } else {
                return DefaultConnectorTypes.BOTTOM_OUTPUT;
            }
        case LEFT:
            if (input) {
                return DefaultConnectorTypes.LEFT_INPUT;
            } else {
                return DefaultConnectorTypes.LEFT_OUTPUT;
            }
        }

        return null;
    }



    /**
     * Added
     */

    private GConnection find_connection(GNode start, GNode end) {

        for (GConnector connector: start.getConnectors()) {
            for (GConnection connection: connector.getConnections()) {
                GNode found_end = GModelUtils.getOpposingNode(connector, connection);

                if (found_end.equals(end)) {
                    return connection;
                }
            }
        }

        return null;
    }

    private List<GConnection> find_all_connections(List<GNode> nodeList) {

        ArrayList<GConnection> connectionList = new ArrayList<>();

        for (int i = 0, j = 1; j < nodeList.size(); i+=2, j=i+1) {
            GNode startNode = nodeList.get(i);
            GNode endNode = nodeList.get(j);

            GConnection connection = find_connection(startNode, endNode);

            connectionList.add(connection);
        }

        return connectionList;
    }


    public Vector<Vector<GNode>> find_node_from_id(Vector<AbstractMap.SimpleEntry<String, String>> nodeIdList) {

        Vector<Vector<GNode>> pathList = new Vector<>();
        Vector<GNode> sequence = new Vector<>();

        for (int i = 1, j = 2; j < nodeIdList.size(); i++, j++) {
            System.out.println(nodeIdList.get(i) + " -- " + nodeIdList.get(j));

            if (nodeIdList.get(i).getKey() == "in") continue;

            GNode source = find_node_from_name(nodeIdList.get(i).getValue());
            GNode target = find_node_from_name(nodeIdList.get(j).getValue());

            sequence.add(source);
            sequence.add(target);
        }

        pathList.add(sequence);

        return pathList;
    }


    public GNode find_node_from_name(String nodeName) {

        Predicate condition = node -> ((GNode)node).getId().equals(nodeName);
        Optional resultat = this.graphEditor.getModel().getNodes().stream().filter(condition).findFirst();


        if (resultat.isPresent()) {
            return (GNode) resultat.get();
        } else {
            return null;
        }

    }


    public GConnection findConnection(GConnector connector) {

        GConnection connectionFound = null;

        if (connector.getConnections().size() != 1 ) {
            System.err.println("ERREUR : DEFAULTSKINCONTROLLER : FINDCONNECTION != 1");
        } else {
            connectionFound = connector.getConnections().get(0);
        }

        return connectionFound;

    }


    public GConnector findConnectorFromName(String nodeName, String connectorName) {
        GNode node = find_node_from_name(nodeName);
        EList<GConnector> connectors = node.getConnectors();

        String connectorNameInModel = convertConnectorToXMLName(connectorName);
        Predicate condition = connector -> ((GConnector)connector).getType().equals(connectorNameInModel);

        Optional resultat = connectors.stream().filter(condition).findFirst();

        return (GConnector) resultat.get();
    }

    private String convertConnectorToXMLName(String connectorName) {

        String XMLdirection = "";
        String XMLsens = "";

        if (connectorName.matches(".*in_.*")) {
            XMLsens = "input";
        } else {
            XMLsens = "output";
        }

        if (connectorName.matches(".*EAST.*")) {
            XMLdirection = "right";
        } else if (connectorName.matches(".*NORTH.*")) {
            XMLdirection = "top";
        } else if (connectorName.matches(".*WEST.*")) {
            XMLdirection = "left";
        } else if (connectorName.matches(".*SOUTH.*")) {
            XMLdirection = "bottom";
        } else {
            XMLdirection = "";
        }

        return XMLdirection.concat("-").concat(XMLsens);

    }


    public static Vector<AbstractMap.SimpleEntry<Double, Vector<AbstractMap.SimpleEntry<String, String>>>> readSimulaionSequence(String fileName) {

        Vector<AbstractMap.SimpleEntry<Double, Vector<AbstractMap.SimpleEntry<String, String>>>> sequences= new Vector<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            double currentTime = 0;

            while ((line = br.readLine()) != null) {

                Vector<AbstractMap.SimpleEntry<String, String>> sequence = new Vector<>();

                if (line.startsWith("time:")) {
//                    System.out.println(line);
                    currentTime = Double.parseDouble(line.substring(5));
                    continue;
                }

                do{
                    if(line.startsWith("--NODE")) {

                        if (line.matches(".*in_.*")) {
//                            System.out.println("-" + line);

                            //System.out.println(line.replaceAll("--(.*)\\(.*", "$1"));
                            sequence.add(new AbstractMap.SimpleEntry<String, String>("in",line.replaceAll("--(.*)\\(.*", "$1")));
                        } else if (line.matches(".*out_.*")) {
//                            System.out.println("-->" + line);
                            sequence.add(new AbstractMap.SimpleEntry<String, String>("out",line.replaceAll("--(.*)\\(.*", "$1")));
                        }

                    }
                } while ((line = br.readLine()) != null && !line.startsWith("time:") );

                sequences.add(new AbstractMap.SimpleEntry<>(currentTime, sequence));

                // check why we got out
                if (line != null && line.startsWith("time:")) {
                    System.out.println(line);
                    currentTime = Double.parseDouble(line.substring(5));
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sequences;
    }


    public void generate_path_throw_all_nodes() {

        ParallelTransition transition = new ParallelTransition();

        for (GConnection connection : graphEditor.getModel().getConnections() ) {
            SimpleConnectionSkin skin = (SimpleConnectionSkin)graphEditor.getSkinLookup().lookupConnection(connection);

            transition.getChildren().addAll(skin.parallelTransitions);
            transition.setCycleCount(1);
        }

        transition.play();

    }


    public ParallelTransition generatePathFromSequence(Double currentTime, SimulationSequence sequenceOfRecords) {

//        System.out.println("------------------------------------------------");
//        sequenceOfRecords.getSequence().forEach(simulationRecord -> System.out.println(currentTime + " -- " + simulationRecord.getTaskNumber() + " -- " + simulationRecord.getElement().getId() + " -- " + simulationRecord.isInputEvent()));
//        System.out.println("------------------------------------------------");

        List<AbstractMap.SimpleEntry<GConnection, SimulationRecord>> tasksConnections = new Vector<>();

        System.out.println("------------------------------------------------");


        for (SimulationRecord record : sequenceOfRecords.getSequence(true)) {
            EList<GConnection> connections = record.getInputConnector().getConnections();

            if (connections == null || connections.isEmpty()) {
                continue;
            }

            GConnection connection = connections.get(0);
            System.out.println( record.getTimeElapsed() + " --| " + (connection.getSource().getParent()).getId() + " -- " + ((GNode)connection.getTarget().getParent()).getId());

            tasksConnections.add(new AbstractMap.SimpleEntry<>(connection, record));
        }


        System.out.println("------------------------------------------------");


        ParallelTransition transition = new ParallelTransition();

        for (AbstractMap.SimpleEntry<GConnection, SimulationRecord> entry : tasksConnections) {
            SimpleConnectionSkin connectionSkin = (SimpleConnectionSkin) graphEditor.getSkinLookup().lookupConnection(entry.getKey());
            connectionSkin.createNewAnimationPath(entry.getValue().getTaskNumber());

            transition.getChildren().add(connectionSkin.parallelTransitions.lastElement());
            transition.setCycleCount(1);
        }

        return transition;
    }

}
