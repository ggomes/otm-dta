package otmdta;

import java.util.*;

public class PathDistribution {

    public double [][] props; // props[pathid][time]
                                // sum(props[:][t])=1 for all t.

    // random initialization of the assignment
    public PathDistribution(List<Long> path_ids,int num_steps){

        Random random = new Random(System.currentTimeMillis());
        int num_paths = path_ids.size();
        double [] temp = new double[num_paths];
        double sum = 0d;

        props = new double[num_paths][num_steps];
        for(int t=0;t<num_steps;t++){

            // sample num_paths random numbers
            sum = 0d;
            for(int p=0;p<num_paths;p++){
                temp[p] = random.nextDouble();
                sum += temp[p];
            }

            // normalize and store
            for(int p=0;p<num_paths;p++)
                props[p][t] = temp[p]/sum;
        }

    }
}
