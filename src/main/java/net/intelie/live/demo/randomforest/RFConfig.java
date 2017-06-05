package net.intelie.live.demo.randomforest;

import net.intelie.live.*;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public class RFConfig implements ExtensionConfig {
    private String query;
    private String trainSpan;
    private String testSpan;
    private String predict;
    private int ntrees = 200;
    private int maxNodes = 100;
    private int nodeSize = 5;
    private double mtry = 1 / 3.0;

    @Override
    public String summarize() {
        return query;
    }

    @Override
    public Set<ExtensionRole> roles() {
        return ExtensionRole.start().ok();
    }

    @Override
    public ValidationBuilder validate(ValidationBuilder builder) {
        return builder
                .requiredValue(query, "query")
                .requiredValue(trainSpan, "trainSpan")
                .requiredValue(testSpan, "testSpan")
                .requiredValue(predict, "predict")
                .required(ntrees > 0, "ntrees must be positive")
                .required(nodeSize > 0, "nodeSize must be positive")
                .required(mtry > 0 && mtry < 1, "mtry must be between 0 and 1")
                ;
    }

    public ElementHandle create(PrefixedLive live, ExtensionQualifier qualifier) throws Exception {
        ExecutorService executor = live.system().requestExecutor(1, 1, "");
        TrainerTask trainer = new TrainerTask(live, qualifier.qualifier(), query, trainSpan, testSpan, predict, ntrees, maxNodes, nodeSize, mtry);
        executor.submit(trainer);
        return new ElementHandle.Default(live) {
            @Override
            public ElementState status() {
                if (trainer.getCompletion() < 1)
                    return new ElementState(ElementStatus.VALID_BUT, "Training: " + String.format("%.2f", trainer.getCompletion() * 100) + "%");
                return ElementState.OK;
            }
        };
    }

    public ElementHandle test(PrefixedLive live, ExtensionQualifier qualifier) throws Exception {
        return ElementHandle.OK;
    }
}
