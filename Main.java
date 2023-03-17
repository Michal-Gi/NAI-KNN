import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";


        try {
            java.util.Scanner trainingData = new java.util.Scanner(new java.io.File("C:\\Users\\Michnik tech tips\\IdeaProjects\\NAI-KNN\\Data\\iris.data"));
            java.util.Scanner testData = new java.util.Scanner(new java.io.File("C:\\Users\\Michnik tech tips\\IdeaProjects\\NAI-KNN\\Data\\iris.test.data"));


            //inserting data to the database (from a csv file)
            List<Data> dataDatabase = new ArrayList<>();
            while(trainingData.hasNextLine()){
                String[] splitedIris = trainingData.nextLine().split(",");
                List<Double> parameters = new ArrayList<>();
                for(int i =0; i< splitedIris.length-1; i++)
                    parameters.add(Double.valueOf(splitedIris[i]));

                dataDatabase.add(new Data(parameters, splitedIris[splitedIris.length-1]));
            }

            //for each line of data (read from a csv file), function checks what category the tested object fits and assigns it
            while(testData.hasNextLine()){
                String[] splitedIris = testData.nextLine().split(",");
                List<Double> parameters = new ArrayList<>();
                for(int i =0; i< splitedIris.length-1; i++)
                    parameters.add(Double.parseDouble(splitedIris[i]));
                Data TestSubject = new Data(parameters, splitedIris[splitedIris.length-1]);

                //checks k nearest neighbours and returns predicted category
                String predictedCategory = checkAffiliation(dataDatabase, TestSubject, 2);
                TestSubject.setPredictedCategory(predictedCategory);
                //ckecks if the predicted category is correct
                if(TestSubject.getPredictedCategory().equals(TestSubject.getCategory()))
                    System.out.println(GREEN + TestSubject.getPredictedCategory() + RESET);
                else
                    System.out.println(RED +"expected: "+ TestSubject.getCategory() + ", achieved: " + TestSubject.getPredictedCategory() + RESET);
            }

            // part to manually insert the values of an iris to check if the program learned correctly
//            java.util.Scanner scanner = new java.util.Scanner(System.in);
//            System.out.println("To rerun the K-NN algorithm enter the k value and a pair. To terminate the program enter \"over\"");
//            String input = scanner.nextLine();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    public static String checkAffiliation(List<Data> database, Data testObject, int k){
        List<Data> knn = new ArrayList<Data>();
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
        List<Data> result = new ArrayList<>();
        for(Data d : knn){
           if(result.isEmpty())
               result.add(d);
           else {
               if (result.get(result.size() - 1).getCategory().equals(d.getCategory()))
                   result.add(d);
               else
                   result.remove(result.size() - 1);
           }

        }
        if(result.isEmpty())
            return knn.get(0).getCategory();
        return result.get(0).getCategory();
    }
}
