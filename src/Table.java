import java.util.ArrayList;
import java.util.List;

public class Table {
    String name;
    ArrayList<String> columns;
    ArrayList<Boolean> indexed;
    ArrayList<ArrayList<Double>>  rows;

    public Table(String name, ArrayList<String> columns, ArrayList<Boolean>indexed, ArrayList<ArrayList<Double>> rows){
        this.name=name;
        this.columns=columns;
        this.indexed=indexed;
        this.rows=rows;
    }

    public void addRow(List<Double> values){
        if(values.size()!=columns.size()){
            System.out.println("Number of values doesn't match number of columns");
        }
        else {
            rows.add((ArrayList<Double>) values);
        }
    }

    public void printTable(){
        int[] width  = new int[columns.size()];

        for(int clmn=0; clmn<columns.size(); clmn++){
            width[clmn] = columns.get(clmn).length();
            for(int row=0;row<rows.size();row++){
                ArrayList<Double> currentRow = rows.get(row);
                Double value = currentRow.get(clmn);
                String text = String.valueOf(value);

                if(text.length()>width[clmn]){
                    width[clmn]=text.length();
                }
            }
        }

        String line = "";
        for(int clmn=0;clmn<columns.size();clmn++){
            line=line + "+";
            for(int i=0;i<width[clmn]+1;i++){
                line=line+"-";
            }
            line=line+"-";
        }
        line=line+"+";
        System.out.println(line);

    }


    public String getName(String name){
        return name;
    }

    public ArrayList<String> getColumns( ArrayList<String> columns){
        return columns;
    }

    public ArrayList<Boolean> getIndexed(ArrayList<Boolean> indexed){
        return indexed;
    }

    public  ArrayList<ArrayList<Double>>  getRows( ArrayList<ArrayList<Double>>  rows){
        return rows;
    }




}
