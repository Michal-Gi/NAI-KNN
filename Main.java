import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {


        try {
            java.util.Scanner trainingData = new java.util.Scanner(new java.io.File(System.getProperty("user.dir") + "\\Data\\iris.data"));
            java.util.Scanner testData = new java.util.Scanner(new java.io.File(System.getProperty("user.dir") + "\\Data\\iris.test.data"));
            java.util.Scanner trainingDataWDBC = new java.util.Scanner(new java.io.File(System.getProperty("user.dir") + "\\Data\\wdbc.data"));
            java.util.Scanner testDataWDBC = new java.util.Scanner(new java.io.File(System.getProperty("user.dir") + "\\Data\\wdbc.test.data"));

            //inserting data to the database (from a csv file) for both iris and wdbc
            List<Data> irisDatabase = new ArrayList<>();
            prepareDatabase(trainingData, irisDatabase);

            List<Data> wdbcDataDatabase = new ArrayList<>();
            prepareDatabase(trainingDataWDBC, wdbcDataDatabase);

            //for each line of data (read from a csv file), function checks what category the tested object fits and assigns it
            checkAllData(testData, irisDatabase, 3, true);
            System.out.println();
            checkAllData(testDataWDBC, wdbcDataDatabase, 3, true);

            //making sure there is an empty file that can be filled with data for the chart
            FileWriter writer = new FileWriter("accuracyChart.csv", false);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write("");
            bw.close();
            //creating a csv file that contains the accuracy of program for different values of k nearest neighbours
            for(int i = 1; i<101; i++){
                testData = new java.util.Scanner(new java.io.File(System.getProperty("user.dir") + "\\Data\\iris.test.data"));
                checkAllData(testData, irisDatabase, i, false);
            }


            // part to manually insert the values of an iris to check if the program learned correctly
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            boolean terminate = false;
            while(!terminate){
                System.out.println("To rerun the K-NN algorithm enter \"rerun\". To terminate the program enter \"over\"");
                String input = scanner.nextLine();
                if(input.equals("rerun")){

                    System.out.println("enter the desired k parameter of knn algorithm (single integer)");
                    int k = Integer.parseInt(scanner.nextLine());
                    System.out.println("enter category (categories available: iris, wdbc)");
                    input = scanner.nextLine();
                    if(input.equals("iris")){
                        System.out.println("enter 4 doubles representing respective parameters of iris flower and the expected name of the subspecies in a single line and separate every item with a comma");
                        List<Double> parameters = new ArrayList<>();
                        input = scanner.nextLine();
                        String[] splitedIris = input.split(",");
                        for (String iris : splitedIris)
                            parameters.add(Double.valueOf(iris));
                        System.out.println(checkAffiliation(irisDatabase, new Data(parameters, "Iris-setosa"), k));
                    } else if (input.equals("wdbc")) {
                        System.out.println("enter 30 doubles representing respective parameters of a Wisconsin Diagncostic Breast Cancer data set separated with commas in one line");
                        List<Double> parameters = new ArrayList<>();
                        input = scanner.nextLine();
                        String[] splitedWdbc = input.split(",");
                        for (String iris : splitedWdbc)
                            parameters.add(Double.valueOf(iris));
                        System.out.println(checkAffiliation(wdbcDataDatabase, new Data(parameters, "M"), k));
                    } else {
                        System.out.println("incorrect category");
                    }
                }
                if(input.equals("over"))
                    terminate = true;
            }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * checks distance between every object in a database and the given argument and assigns a category to that object.
     * @param database database that was used for training
     * @param testObject object we want to test
     * @param k how many neighbours should be considered in k-nn algorithm
     * @return String category
     */
    public static String checkAffiliation(List<Data> database, Data testObject, int k){
        List<Data> knn = new ArrayList<>();
        for( Data d : database){
            double distance = testObject.distance(d);
            for(int i = 0; i<k; i++){
                if(!knn.isEmpty() && i<knn.size()){
                    if(distance < testObject.distance(knn.get(i))){
                        for(int j = i+1; j<knn.size() && knn.size()<=k; j++){
                            knn.set(j, knn.get(i));
                        }
                        knn.set(i,d);
                        break;
                    }
                } else knn.add(d);
            }
        }

        Map<String, Integer> categoryAmountPair = new HashMap<>();
        for(int i = 0; i < knn.size(); i++){
            if(!categoryAmountPair.containsKey(knn.get(i).getCategory()))
                categoryAmountPair.put(knn.get(i).getCategory(), 1);
            else
                categoryAmountPair.replace(knn.get(i).getCategory(), categoryAmountPair.get(knn.get(i).getCategory()) + 1);
        }
        String result = knn.get(0).getCategory();
        int max = categoryAmountPair.get(result);
        for(String category : categoryAmountPair.keySet()){
            if(categoryAmountPair.get(category) > max){
                result = category;
                max = categoryAmountPair.get(category);
            }
        }

        return result;
    }

    /**
     * function that prepares the training database
     * @param trainingData csv file that contains parameters of data that is being researched. The last value has to be the expected outcome
     * @param database database we are filling with training data
     */
    public static void prepareDatabase(java.util.Scanner trainingData, List<Data> database){
        while(trainingData.hasNextLine()){
            String[] splitedData = trainingData.nextLine().split(",");
            List<Double> parameters = new ArrayList<>();
            for(int i =0; i< splitedData.length-1; i++)
                parameters.add(Double.valueOf(splitedData[i]));
            database.add(new Data(parameters, splitedData[splitedData.length-1]));
        }
    }

    /**
     * checks all specified data from a csv file where the last field is the correct category that is supposed to be assigned. Prints whether every case is correct or not and the total accuracy of the program
     * @param testData Data that is supposed to be checked
     * @param trainingDatabase Data that the new data is compared to
     */
    public static void checkAllData(java.util.Scanner testData, List<Data> trainingDatabase, int k, boolean printResult){
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        int correct = 0, wrong = 0;
        while(testData.hasNextLine()){
            String[] splitedData = testData.nextLine().split(",");
            List<Double> parameters = new ArrayList<>();
            for(int i =0; i< splitedData.length-1; i++)
                parameters.add(Double.parseDouble(splitedData[i]));
            Data TestSubject = new Data(parameters, splitedData[splitedData.length-1]);

            //checks k nearest neighbours and returns predicted category
            String predictedCategory = checkAffiliation(trainingDatabase, TestSubject, k);
            TestSubject.setPredictedCategory(predictedCategory);
            //ckecks if the predicted category is correct
            if(TestSubject.getPredictedCategory().equals(TestSubject.getCategory())){
                correct++;
                if(printResult)
                    System.out.println(GREEN + TestSubject.getPredictedCategory() + RESET);
            }
            else{
                wrong++;
                if(printResult)
                    System.out.println(RED +"expected: "+ TestSubject.getCategory() + ", achieved: " + TestSubject.getPredictedCategory() + RESET);
            }
        }
        double accuracy;
        if(correct!=0){
            accuracy = (double)(correct-wrong)/correct;
            if(printResult)
                System.out.println("\nThe accuracy of the program is " + accuracy + "%");
        }
        else{
            accuracy = 0;
            if(printResult)
                System.out.println("\nThe accuracy of the program is 0%");
        }

        if(!printResult){
            try {
                FileWriter writer = new FileWriter("accuracyChart.csv", true);
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write(accuracy +", "+k);
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
