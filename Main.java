import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
            checkAllData(testData, irisDatabase);
            System.out.println();
            checkAllData(testDataWDBC, wdbcDataDatabase);

            //creating a csv file that contains the accuracy of program for different values of k nearest neighbours
            for(int i = 1; i<10; i++){

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

            } catch (FileNotFoundException e) {
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
        Stack<Data> result = new Stack<>();
        for(int i = knn.size()-1; i >= 0; i--){
           if(result.isEmpty())
               result.add(knn.get(i));
           else {
               if (result.lastElement().getCategory().equals(knn.get(i).getCategory()))
                   result.push(knn.get(i));
               else
                   result.pop();
           }

        }
        if(result.isEmpty())
            return knn.get(0).getCategory();
        return result.get(0).getCategory();
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
    public static void checkAllData(java.util.Scanner testData, List<Data> trainingDatabase){
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
            String predictedCategory = checkAffiliation(trainingDatabase, TestSubject, 3);
            TestSubject.setPredictedCategory(predictedCategory);
            //ckecks if the predicted category is correct
            if(TestSubject.getPredictedCategory().equals(TestSubject.getCategory())){
                correct++;
                System.out.println(GREEN + TestSubject.getPredictedCategory() + RESET);
            }
            else{
                wrong++;
                System.out.println(RED +"expected: "+ TestSubject.getCategory() + ", achieved: " + TestSubject.getPredictedCategory() + RESET);
            }
        }
        if(correct!=0)
            System.out.println("\nThe accuracy of the program is " + (double)(correct-wrong)/correct + "%");
        else
            System.out.println("\nThe accuracy of the program is 0%");
    }
}
