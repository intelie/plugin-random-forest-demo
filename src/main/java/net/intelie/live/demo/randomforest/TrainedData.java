package net.intelie.live.demo.randomforest;

import smile.regression.RandomForest;

import java.util.List;
import java.util.Map;

public class TrainedData {
    private final RandomForest forest;
    private final List<String> fields;
    private final String mnemonic;
    private double[] mse;

    public TrainedData(RandomForest forest, List<String> fields, String mnemonic, double[] mse) {
        this.forest = forest;
        this.fields = fields;
        this.mnemonic = mnemonic;
        this.mse = mse;
    }

    public double predict(Map<String, Object> event) {
        double[] values = new double[fields.size()];

        for (int i = 0; i < fields.size(); i++)
            values[i] = getAsDouble(event, fields.get(i));

        return forest.predict(values);
    }

    public double error() {
        return forest.error();
    }

    private double getAsDouble(Map<String, Object> event, String field) {
        Object value = event.get(field);
        return value instanceof Double ? (double) value : 0;
    }
}
