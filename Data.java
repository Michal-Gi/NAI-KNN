import java.util.List;

public class Data {

    private List<Double> measuredValues;
    private String category, predictedCategory;


    public Data(List<Double> measuredValues, String category) {
        this.measuredValues = measuredValues;
        this.category = category;
        this.predictedCategory = "";
    }

    public void setPredictedCategory(String predictedCategory) {
        this.predictedCategory = predictedCategory;
    }

    public String getPredictedCategory() {
        return predictedCategory;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Returns the distance between this object on the one specified through the parameters. In this case distance is the length of a vector between the two.
     * @param other compared data object
     * @return distance between this object and the other
     */
    public double distance(Data other){
        double res = 0;
        for(int i = 0; i<this.measuredValues.size(); i++){
            res += Math.pow((this.measuredValues.get(i) - other.measuredValues.get(i)), 2);
        }

        return Math.sqrt(res);
    }
}
