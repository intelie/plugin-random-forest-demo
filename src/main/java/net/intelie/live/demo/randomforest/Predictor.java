package net.intelie.live.demo.randomforest;

import com.google.common.base.Strings;
import net.intelie.pipes.*;
import net.intelie.pipes.util.Escapes;

import java.util.Map;

public class Predictor implements CustomizeFunction {
    private final TrainedData trained;
    private final String qualifier;
    private final String functionName;

    public Predictor(TrainedData trained, String qualifier) {
        if (Strings.isNullOrEmpty(qualifier)) qualifier = "default";

        this.trained = trained;
        this.qualifier = qualifier;
        this.functionName = Escapes.safeIdentifier(qualifier);
    }

    @Export("predict")
    public Double predict(Object obj) {
        if (!(obj instanceof Map)) return null;
        return trained.predict((Map) obj);
    }

    @Export("error")
    @DoNotOptimize
    public Double error() {
        return trained.error();
    }

    @Override
    public String name(Function original) {
        String suffix = original.name();
        suffix = "predict".equals(suffix) ? "" : "." + suffix;
        return "predict.randomforest." + functionName + suffix;
    }

    @Override
    public String description(Function original) {
        return "RandomForestPredictor";
    }

    @Override
    public HelpData help(Function original) {
        return new HelpData(
                "function",
                name(original),
                name(original) + "(object x)",
                "Predicts next time series value using Random Forest model: " + qualifier,
                null,
                null,
                null,
                null
        );
    }
}
