package net.intelie.live.demo.randomforest;

import java.util.List;

public class Data {
    private final List<String> fields;
    private final double[][] x;
    private final double[] y;

    public Data(List<String> fields, double[][] x, double[] y) {
        this.fields = fields;
        this.x = x;
        this.y = y;
    }

    public int size() {
        return x.length;
    }

    public List<String> getFields() {
        return fields;
    }

    public double[][] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }
}
