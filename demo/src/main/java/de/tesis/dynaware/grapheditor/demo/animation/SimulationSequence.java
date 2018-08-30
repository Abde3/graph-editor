package de.tesis.dynaware.grapheditor.demo.animation;


import java.util.Vector;



public class SimulationSequence {

    Vector<SimulationRecord> sequence;

    public SimulationSequence() {
        this.sequence = new Vector<>() ;
    }

    public void addRecord(SimulationRecord record) {
        sequence.add(record);
    }

    public Vector<SimulationRecord> getSequence() {
        return sequence;
    }

    public Vector<SimulationRecord>  getSequence(boolean isInput) {

        Vector<SimulationRecord> result = new Vector<SimulationRecord>();

        for ( SimulationRecord record : sequence) {
            if (record.isInputEvent == isInput) {
                result.add(record);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
