package de.tesis.dynaware.grapheditor.demo.animation;

import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;

public class SimulationRecord {

    double      timeElapsed;
    int         taskNumber;
    GNode       taskTargetNode;
    GNode       element;
    GConnector  inputConnector;
    boolean     isInputEvent;

    public SimulationRecord(double timeElapsed, GNode element, GConnector inputConnector, int taskNumber, GNode taskTargetNode, boolean isInputEvent) {
        this.timeElapsed = timeElapsed;
        this.taskNumber = taskNumber;
        this.taskTargetNode = taskTargetNode;
        this.element = element;
        this.inputConnector = inputConnector;
        this.isInputEvent = isInputEvent;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public GNode getTaskTargetNode() {
        return taskTargetNode;
    }

    public GNode getElement() {
        return element;
    }

    public GConnector getInputConnector() {
        return inputConnector;
    }

    public boolean isInputEvent() {
        return  isInputEvent;
    }

    @Override
    public String toString() {
        return (isInputEvent() ? ("INPUT@") : ("OUTPUT@")) + "(Time:" + getTimeElapsed() +", Task-" + taskNumber + ", To:" + getTaskTargetNode().getId() + " ): " + getElement().getId();
    }
}