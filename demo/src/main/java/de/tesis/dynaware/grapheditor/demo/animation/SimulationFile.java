package de.tesis.dynaware.grapheditor.demo.animation;

import de.tesis.dynaware.grapheditor.demo.customskins.DefaultSkinController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SimulationFile {

    private DefaultSkinController skinController;
    private Vector<String> simulationLines;
    private TreeMap<Double, SimulationSequence> sequences;

    private Set<Integer>    taskSet;
    private Set<Double>     timeSet;

    private String fileName;

    public SimulationFile(DefaultSkinController skinController, String fileName) {
        this.skinController     = skinController;
        this.simulationLines    = new Vector<>();
        this.sequences          = new TreeMap<>();
        this.taskSet            = new HashSet<>();
        this.timeSet            = new HashSet<>();
        this.fileName           = fileName;
    }

    private void addLine(String line) {
        simulationLines.add(line);

        if(isTimeLine(line)) {
            double currentTime = getTime(line);
            timeSet.add(currentTime);
            sequences.putIfAbsent(currentTime, new SimulationSequence());
        } else if (line.contains("TASK-")) {
            int taskNumber = getTaskNumber(line);
            taskSet.add(taskNumber);
        }
    }

    private double getTime(String line) {
        return Double.parseDouble(line.replaceAll("[^0-9.]", ""));
    }


    private boolean isTimeLine(String line) {
        return line.startsWith("time:");
    }

    private boolean isInternalEvent(String line) {
        return !line.startsWith("--NODE[");
    }

    private boolean isExternalEvent(String line) {
        return line.startsWith("--NODE[");
    }

    private String getNodeName(String line) {
        return line.replaceAll(".*(NODE\\[.*\\])\\(.*", "$1");
    }

    private String getConnectorName(String line) {
        return line.replaceAll(".*NODE\\[.*\\]\\((.*) =.*", "$1");
    }

    private String getTargetNodeName(String line) {
        return "NODE".concat(line.replaceAll(".*NODE\\[.*\\]\\(.* = \\{.*, dest: (.*), .*", "$1"));
    }

    private boolean isInputEvent(String line) {
        return  line.matches(".*(NODE\\[.*\\])\\(in_.*");
    }

    private boolean isOutputEvent(String line) {
        return  line.matches(".*(NODE\\[.*\\])\\(out_.*");
    }

    private int getTaskNumber(String line) {
        return Integer.parseInt(line.replaceAll(".*TASK-([0-9]+).*", "$1"));
    }

    public void readSimulationFile() {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                addLine(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void buildSequences() {

        double currentTime = -1;
        for (String line : simulationLines) {
            if (isTimeLine(line)) {
                currentTime = getTime(line);
            } else if (isExternalEvent(line)){
                String nodeName = getNodeName(line);
                String connectorName = getConnectorName(line);
                String targetNodeName = getTargetNodeName(line);
                boolean isInputEvent = isInputEvent(line);

                int taskNumber = getTaskNumber(line);
                SimulationRecord record = new SimulationRecord(currentTime, skinController.find_node_from_name(nodeName), skinController.findConnectorFromName(nodeName, connectorName), taskNumber, skinController.find_node_from_name(targetNodeName), isInputEvent);

                sequences.get(currentTime).addRecord(record);
            }

        }
    }

    public TreeMap<Double, SimulationSequence> getSequences() {
        return sequences;
    }



}
