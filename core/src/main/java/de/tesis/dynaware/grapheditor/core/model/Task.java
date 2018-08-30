package de.tesis.dynaware.grapheditor.core.model;

import javafx.scene.shape.Rectangle;

public class Task extends Rectangle {
    int taskNumber;

    public Task(int taskNumber) {
        super();
        this.taskNumber = taskNumber;
    }

    public Task(int taskNumber, double rectangleWidth, double rectangleHeight) {
        super(rectangleWidth, rectangleHeight);
        this.taskNumber = taskNumber;
    }

    public int getTaskNumber() {
        return taskNumber;
    }
}
