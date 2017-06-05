package net.intelie.live.demo.randomforest;

import net.intelie.live.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.regression.RandomForest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class TrainerTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerTask.class);

    private final Live live;
    private final String qualifier;
    private final String query;
    private final String trainSpan;
    private final String testSpan;
    private final String mnemonic;
    private final int ntrees;
    private final int maxNodes;
    private final int nodeSize;
    private final double mtry;
    private double completion;

    public TrainerTask(Live live, String qualifier, String query, String trainSpan, String testSpan, String mnemonic, int ntrees, int maxNodes, int nodeSize, double mtry) {
        this.live = live;
        this.qualifier = qualifier;
        this.query = query;
        this.trainSpan = trainSpan;
        this.testSpan = testSpan;
        this.mnemonic = mnemonic;
        this.ntrees = ntrees;
        this.maxNodes = maxNodes;
        this.nodeSize = nodeSize;
        this.mtry = mtry;
    }

    public double getCompletion() {
        return completion;
    }

    public TrainedData train(Data train, Data test) throws Exception {
        System.out.println(train.getFields().size());
        System.out.println(train.getX().length);
        RandomForest forest = new RandomForest(train.getX(), train.getY(), ntrees, maxNodes, nodeSize, (int) (train.getFields().size() * mtry));
        return new TrainedData(forest, train.getFields(), mnemonic, forest.test(test.getX(), test.getY()));
    }

    public void run() {
        try {
            completion = 0.25;

            Data trainData = getData(live, trainSpan, mnemonic, null);
            completion = 0.5;

            Data testData = getData(live, testSpan, mnemonic, trainData.getFields());
            TrainedData trained = train(trainData, testData);

            completion = 1;

            live.pipes().addInstanceModule(new Predictor(trained, qualifier));
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.error("Error", e);
        }
    }

    private Data getData(Live live, String span, String mnemonic, List<String> fields) throws Exception {
        Semaphore semaphore = new Semaphore(0);

        List<Map<String, Object>> events = new ArrayList<>();
        System.out.println("SPAN " + span);
        live.engine().runQueries(new Query(query).span(span).listenWith(
                new QueryListener.Empty() {
                    @Override
                    public void onEvent(QueryEvent event, boolean history) throws Exception {
                        events.addAll(event);
                    }

                    @Override
                    public void onDestroy(DestroyInfo event) throws Exception {
                        semaphore.release();
                    }
                }
        ));
        semaphore.acquireUninterruptibly();


        if (fields == null) {
            Set<String> fieldSet = events.stream()
                    .flatMap(x -> x.keySet().stream())
                    .collect(Collectors.toSet());
            fieldSet.remove(mnemonic);
            fields = new ArrayList<>(fieldSet);
        }

        double[][] x = new double[events.size()][fields.size()];
        double[] y = new double[events.size()];

        for (int i = 0; i < events.size(); i++) {
            Map<String, Object> event = events.get(i);
            for (int j = 0; j < fields.size(); j++) {
                x[i][j] = getAsDouble(event, fields.get(j));
            }

            y[i] = getAsDouble(event, mnemonic);
        }

        return new Data(fields, x, y);
    }

    private double getAsDouble(Map<String, Object> event, String field) {
        Object value = event.get(field);
        return value instanceof Double ? (double) value : 0;
    }
}
