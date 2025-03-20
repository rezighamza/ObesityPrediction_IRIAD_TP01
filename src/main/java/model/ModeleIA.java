package model;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import model.ModeleIAInterface;
import common.Patient;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class ModeleIA extends UnicastRemoteObject implements ModeleIAInterface {
    private static final long serialVersionUID = 1L;
    private Booster model;

    // Model statistics
    private float accuracy;
    private float precision;
    private float recall;
    private float f1Score;

    // Feature information
    private final int NUM_FEATURES = 12; // Reduced number of features after selection
    private final String[] OBESITY_LEVELS = {
            "Insufficient_Weight", "Normal_Weight", "Overweight_Level_I",
            "Overweight_Level_II", "Obesity_Type_I", "Obesity_Type_II", "Obesity_Type_III"
    };

    public ModeleIA() throws RemoteException {
        super();
        this.accuracy = 0;
        this.precision = 0;
        this.recall = 0;
        this.f1Score = 0;
    }

    /**
     * Maps obesity level string to numerical value
     */
    private static float mapObesityLevelToFloat(String obesityLevel) {
        Map<String, Float> map = new HashMap<>();
        map.put("Insufficient_Weight", 0.0f);
        map.put("Normal_Weight", 1.0f);
        map.put("Overweight_Level_I", 2.0f);
        map.put("Overweight_Level_II", 3.0f);
        map.put("Obesity_Type_I", 4.0f);
        map.put("Obesity_Type_II", 5.0f);
        map.put("Obesity_Type_III", 6.0f);

        // Default to normal weight if null or invalid
        if (obesityLevel == null || !map.containsKey(obesityLevel)) {
            return 1.0f;
        }
        return map.get(obesityLevel);
    }

    /**
     * Maps numerical value back to obesity level string
     */
    private String mapFloatToObesityLevel(float value) {
        int index = Math.round(value);
        if (index >= 0 && index < OBESITY_LEVELS.length) {
            return OBESITY_LEVELS[index];
        }
        return "Normal_Weight"; // Default
    }

    /**
     * Convert categorical variables to numerical features with feature selection
     * and derived metrics calculation
     */
    private float[] processPatientFeatures(Patient patient) {
        float[] features = new float[NUM_FEATURES];
        int index = 0;

        // 1. BMI calculation - key obesity indicator
        double heightInMeters = patient.getHeight() / 100.0;
        double bmi = patient.getWeight() / (heightInMeters * heightInMeters);
        features[index++] = (float) bmi;

        // 2. Age - relevant for metabolic rate
        features[index++] = patient.getAge();

        // 3. Gender (encoded) - affects body composition
        features[index++] = "Male".equals(patient.getGender()) ? 1.0f : 0.0f;

        // 4. Waist-to-Height Ratio approximation
        // We don't have waist measurement, but we can approximate it based on BMI
        double waistHeightRatio = (bmi / 30.0) * 0.5; // Approximation based on BMI
        features[index++] = (float) waistHeightRatio;

        // 5. Lifestyle score - combination of exercise and technology usage
        // Higher physical activity and lower tech usage is better
        float lifestyleScore = (patient.getFaf() / 3.0f) - (patient.getTue() / 5.0f);
        features[index++] = lifestyleScore;

        // 6. Diet quality score - combination of vegetable consumption and high caloric food
        float dietScore = (patient.getFcvc() / 3.0f) - ("yes".equalsIgnoreCase(patient.getFavc()) ? 0.5f : 0f);
        features[index++] = dietScore;

        // 7. Meal regularity - based on number of main meals
        features[index++] = patient.getNcp() / 4.0f;

        // 8. Hydration level - water consumption
        features[index++] = (float) (patient.getCh2o() / 3.0);

        // 9. Family history (encoded) - genetic predisposition
        features[index++] = "yes".equalsIgnoreCase(patient.getFamilyHistory()) ? 1.0f : 0.0f;

        // 10. Smoking status (encoded) - affects metabolism
        features[index++] = "yes".equalsIgnoreCase(patient.getSmoke()) ? 1.0f : 0.0f;

        // 11. Food monitoring awareness - tracks calories
        features[index++] = "yes".equalsIgnoreCase(patient.getScc()) ? 1.0f : 0.0f;

        // 12. Snacking behavior score
        float snackingScore = 0.0f;
        if ("no".equalsIgnoreCase(patient.getCaec())) {
            snackingScore = 1.0f; // Best - no snacking
        } else if ("sometimes".equalsIgnoreCase(patient.getCaec())) {
            snackingScore = 0.5f; // Moderate
        } else { // "always" or "frequently"
            snackingScore = 0.0f; // Worst - frequent snacking
        }
        features[index++] = snackingScore;

        return features;
    }

    /**
     * Load patients data into DMatrix format for XGBoost with selected features
     */
    private DMatrix loadPatientsToDMatrix(List<Patient> patients) throws XGBoostError {
        float[][] featuresArray = new float[patients.size()][NUM_FEATURES];
        float[] labels = new float[patients.size()];

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            featuresArray[i] = processPatientFeatures(patient);

            // Add label if available
            if (patient.getObesityLevel() != null) {
                labels[i] = mapObesityLevelToFloat(patient.getObesityLevel());
            }
        }

        // Flatten features array for DMatrix construction
        float[] flatFeatures = new float[patients.size() * NUM_FEATURES];
        int k = 0;
        for (int i = 0; i < patients.size(); i++) {
            for (int j = 0; j < NUM_FEATURES; j++) {
                flatFeatures[k++] = featuresArray[i][j];
            }
        }

        // Create DMatrix with proper dimensions
        DMatrix dMatrix = new DMatrix(flatFeatures, patients.size(), NUM_FEATURES, Float.NaN);
        dMatrix.setLabel(labels);

        return dMatrix;
    }

    @Override
    public void trainModele(List<Patient> patients) throws RemoteException {
        try {
            System.out.println("Starting model training with " + patients.size() + " patients...");

            // Data preprocessing - remove outliers
            List<Patient> cleanedPatients = removeOutliers(patients);
            System.out.println("After outlier removal: " + cleanedPatients.size() + " patients");

            // Split data into training and validation sets (80/20 split)
            int trainSize = (int)(cleanedPatients.size() * 0.8);
            List<Patient> trainPatients = cleanedPatients.subList(0, trainSize);
            List<Patient> validPatients = cleanedPatients.subList(trainSize, cleanedPatients.size());

            // Load patient data
            DMatrix trainMatrix = loadPatientsToDMatrix(trainPatients);
            DMatrix validMatrix = loadPatientsToDMatrix(validPatients);

            // Set model parameters
            Map<String, Object> params = new HashMap<>();
            params.put("eta", 0.03);                    // Lower learning rate for better generalization
            params.put("max_depth", 5);                 // Slightly reduced tree depth to prevent overfitting
            params.put("min_child_weight", 2);          // Increased to reduce overfitting
            params.put("subsample", 0.8);               // Subsample ratio of training instances
            params.put("colsample_bytree", 0.8);        // Subsample ratio of columns
            params.put("gamma", 0.1);                   // Minimum loss reduction for partition
            params.put("lambda", 1.0);                  // L2 regularization
            params.put("alpha", 0.0);                   // L1 regularization
            params.put("objective", "multi:softmax");   // Multiclass classification
            params.put("num_class", 7);                 // Number of obesity levels
            params.put("eval_metric", "mlogloss");      // Multiple evaluation metrics

            // Cross-validation for hyperparameter tuning
            int cv_folds = 5;
            int cv_rounds = 50;
            String[] metrics = new String[]{"merror", "mlogloss"};

            String[] results = XGBoost.crossValidation(trainMatrix, params, cv_rounds, cv_folds, metrics, null, null);

            System.out.println("Cross-validation results:");
            for (String result : results) {
                System.out.println(result);
            }

            // Set watches for early stopping
            Map<String, DMatrix> watches = new HashMap<>();
            watches.put("train", trainMatrix);
            watches.put("eval", validMatrix);

            // Train model with early stopping
            int nround = 1000;        // Maximum number of boosting rounds
            int earlyStoppingRounds = 50;
            model = XGBoost.train(trainMatrix, params, nround, watches, null, null, null, earlyStoppingRounds);

            // Feature importance analysis
            Map<String, Integer> importance = model.getFeatureScore("");
            System.out.println("Feature importance:");
            String[] featureNames = {"BMI", "Age", "Gender", "WaistHeightRatio", "LifestyleScore",
                    "DietScore", "MealRegularity", "Hydration", "FamilyHistory",
                    "Smoking", "CalorieMonitoring", "SnackingBehavior"};
            for (int i = 0; i < featureNames.length; i++) {
                String feature = "f" + i;
                if (importance.containsKey(feature)) {
                    System.out.println(featureNames[i] + ": " + importance.get(feature));
                } else {
                    System.out.println(featureNames[i] + ": 0");
                }
            }

            // Save the model
            model.saveModel("src/main/java/resources/model/xgboost_obesity_model.bin");
            System.out.println("Model saved to xgboost_obesity_model.bin");

            // Evaluate model on validation set
            evaluateModel(validMatrix);

            System.out.println("Model training completed successfully!");
        } catch (XGBoostError e) {
            System.err.println("XGBoost error during training: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error during model training", e);
        } catch (Exception e) {
            System.err.println("Unexpected error during training: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Unexpected error during training", e);
        }
    }

    /**
     * Remove outliers from the dataset
     */
    private List<Patient> removeOutliers(List<Patient> patients) {
        List<Patient> cleanedPatients = new ArrayList<>();

        for (Patient patient : patients) {
            // Calculate BMI for filtering - weight in kg, height in meters
            double bmi = patient.getWeight() / Math.pow(patient.getHeight(), 2);

            // Filter out patients with unrealistic BMI values
            if (bmi > 10 && bmi < 70) {
                // Filter out patients with unrealistic ages
                if (patient.getAge() > 0 && patient.getAge() < 120) {
                    // Filter out patients with unrealistic heights (in meters)
                    if (patient.getHeight() > 0.5 && patient.getHeight() < 2.5) {
                        // Filter out patients with unrealistic weights
                        if (patient.getWeight() > 20 && patient.getWeight() < 300) {
                            cleanedPatients.add(patient);
                        }
                    }
                }
            }
        }
        for (Patient patient : cleanedPatients) {
            System.out.println(patient.getGender() + ", Age: " + patient.getAge() + ", Height: " + patient.getHeight() + ", Weight: " + patient.getWeight());
        }

        return cleanedPatients;
    }

    /**
     * Evaluate model performance on validation data
     */
    private void evaluateModel(DMatrix validMatrix) throws XGBoostError {
        float[][] predictions = model.predict(validMatrix);
        float[] labels = validMatrix.getLabel();

        // Calculate metrics
        int totalSamples = predictions.length;
        int correct = 0;

        // Confusion matrix: [actual][predicted]
        int[][] confusionMatrix = new int[7][7];

        for (int i = 0; i < totalSamples; i++) {
            int predicted = Math.round(predictions[i][0]);
            int actual = Math.round(labels[i]);

            if (predicted == actual) {
                correct++;
            }

            // Update confusion matrix
            if (actual >= 0 && actual < 7 && predicted >= 0 && predicted < 7) {
                confusionMatrix[actual][predicted]++;
            }
        }

        // Calculate accuracy
        this.accuracy = (float) correct / totalSamples;

        // Calculate precision, recall, and F1 score (macro-averaging)
        float totalPrecision = 0;
        float totalRecall = 0;
        int classesWithSamples = 0;

        for (int i = 0; i < 7; i++) {
            int truePositives = confusionMatrix[i][i];

            // Calculate column sum (all predicted as class i)
            int predictedAsI = 0;
            for (int j = 0; j < 7; j++) {
                predictedAsI += confusionMatrix[j][i];
            }

            // Calculate row sum (all actual class i)
            int actualClassI = 0;
            for (int j = 0; j < 7; j++) {
                actualClassI += confusionMatrix[i][j];
            }

            // Calculate precision and recall for this class
            float classPrecision = predictedAsI > 0 ? (float) truePositives / predictedAsI : 0;
            float classRecall = actualClassI > 0 ? (float) truePositives / actualClassI : 0;

            // Only include classes that have samples
            if (actualClassI > 0) {
                totalPrecision += classPrecision;
                totalRecall += classRecall;
                classesWithSamples++;
            }
        }

        // Calculate macro-averaged metrics
        this.precision = classesWithSamples > 0 ? totalPrecision / classesWithSamples : 0;
        this.recall = classesWithSamples > 0 ? totalRecall / classesWithSamples : 0;
        this.f1Score = (precision + recall > 0) ?
                2 * precision * recall / (precision + recall) : 0;

        // Print evaluation metrics
        System.out.println("--- Model Evaluation ---");
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1 Score: " + f1Score);

        // Print confusion matrix
        System.out.println("Confusion Matrix:");
        System.out.println("Actual (rows) vs Predicted (columns)");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println(" | " + OBESITY_LEVELS[i]);
        }
    }

    /**
     * Load an existing model from file
     */
    public boolean loadModel(String modelPath) throws RemoteException {
        try {
            File modelFile = new File(modelPath);
            if (!modelFile.exists()) {
                System.err.println("Model file does not exist: " + modelPath);
                return false;
            }

            model = XGBoost.loadModel(modelPath);
            System.out.println("Model loaded successfully from: " + modelPath);
            return true;
        } catch (XGBoostError e) {
            System.err.println("Error loading model: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error loading model", e);
        }
    }

    @Override
    public String prédire(Patient patient) throws RemoteException {
        try {
            if (model == null) {
                return "Erreur: Modèle non entraîné. Veuillez d'abord entraîner le modèle.";
            }

            // Calculate BMI for logging
            double heightInMeters = patient.getHeight() / 100.0;
            double bmi = patient.getWeight() / (heightInMeters * heightInMeters);
            System.out.println("Patient BMI: " + bmi);

            // Process patient features
            float[] features = processPatientFeatures(patient);

            // Print features for debugging
            String[] featureNames = {"BMI", "Age", "Gender", "WaistHeightRatio", "LifestyleScore",
                    "DietScore", "MealRegularity", "Hydration", "FamilyHistory",
                    "Smoking", "CalorieMonitoring", "SnackingBehavior"};
            System.out.println("Features for prediction:");
            for (int i = 0; i < features.length; i++) {
                System.out.println(featureNames[i] + ": " + features[i]);
            }

            // Create DMatrix for single patient
            DMatrix patientMatrix = new DMatrix(features, 1, NUM_FEATURES, Float.NaN);

            // Get prediction probabilities for all classes
            float[][] predProbs = model.predict(patientMatrix, true);

            if (predProbs.length == 0 || predProbs[0].length == 0) {
                return "Erreur: Prédiction invalide.";
            }

            // Print class probabilities
            System.out.println("Class probabilities:");
            for (int i = 0; i < predProbs[0].length; i++) {
                System.out.println(OBESITY_LEVELS[i] + ": " + (predProbs[0][i] * 100) + "%");
            }

            // Get hard prediction
            float[][] predictions = model.predict(patientMatrix);

            // Get predicted obesity level as string
            String predictedLevel = mapFloatToObesityLevel(predictions[0][0]);

            // Enhanced prediction with confidence information
            int predIdx = Math.round(predictions[0][0]);
            float confidence = predProbs[0][predIdx];

            System.out.println("Predicted obesity level: " + predictedLevel);
            System.out.println("Confidence: " + (confidence * 100) + "%");

            return predictedLevel;

        } catch (XGBoostError e) {
            System.err.println("XGBoost error during prediction: " + e.getMessage());
            e.printStackTrace();
            return "Erreur lors de la prédiction: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Unexpected error during prediction: " + e.getMessage());
            e.printStackTrace();
            return "Erreur inattendue lors de la prédiction: " + e.getMessage();
        }
    }

    // Getters for model metrics
    public float getAccuracy() {
        return accuracy;
    }

    public float getPrecision() {
        return precision;
    }

    public float getRecall() {
        return recall;
    }

    public float getF1Score() {
        return f1Score;
    }
}