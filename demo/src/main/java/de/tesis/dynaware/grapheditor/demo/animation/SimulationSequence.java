package de.tesis.dynaware.grapheditor.demo.animation;

import de.tesis.dynaware.grapheditor.model.GNode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;


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
        //Predicate<SimulationRecord> isInputPredicate = simulationRecord -> simulationRecord.isInputEvent == isInput;

        // CollectionUtils.select( sequence, isInputPredicate);


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
