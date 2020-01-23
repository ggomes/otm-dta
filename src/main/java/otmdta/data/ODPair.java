package otmdta.data;

import api.info.SubnetworkInfo;
import error.OTMException;

import java.util.*;

public class ODPair {

    public final long origin_node_id;
    public final long destination_node_id;
    public final double[] total_demand_vph;   // over time

    public double [][] asgnmt;
    public double [][] tt;

    public List<Double> error;
    public final List<Long> path_ids;
    public final Map<Long,Integer> path_id_to_index;
    public final int num_steps;
    public final int num_paths;

    public ODPair(long origin_node_id, long destination_node_id, List<SubnetworkInfo> subnetworks,double [] total_demand_vph) throws OTMException {
        this.origin_node_id = origin_node_id;
        this.destination_node_id = destination_node_id;

        if(subnetworks.stream().anyMatch(s->!s.is_path))
            throw new OTMException("Not a path!");

        path_ids = new ArrayList<>();
        path_id_to_index = new HashMap<>();
        for(int k=0 ;k<subnetworks.size();k++){
            SubnetworkInfo x = subnetworks.get(k);
            path_ids.add(x.id);
            path_id_to_index.put(x.id,k);
        }
        this.total_demand_vph = total_demand_vph;

        this.num_steps = total_demand_vph.length;
        this.num_paths = path_ids.size();
    }

    public void initialize() {
        asgnmt = new double[num_paths][num_steps];
        tt = new double[num_paths][num_steps];
        error = new ArrayList<>();
        if(num_paths==1)
            asgnmt[0] = total_demand_vph;
        else
            asgnmt = get_random_assignment();
    }

    public double [][] get_assignment(){
        return new double[num_paths][num_steps];
    }

    public double [][] get_random_assignment(){

        double [][] asmt = this.get_assignment();
        Random random = new Random(System.currentTimeMillis());

        double [] temp = new double[num_paths];
        double sum;

        for(int t=0;t<num_steps;t++){

            // sample num_paths random numbers
            sum = 0d;
            for(int p=0;p<num_paths;p++){
                temp[p] = random.nextDouble();
                sum += temp[p];
            }

            // apply proportion to total demand
            for(int p=0;p<num_paths;p++)
                asmt[p][t] = total_demand_vph[t] * temp[p] / sum;
        }

        return asmt;
    }

    public int [] AllOrNothing(double [][] path_travel_times){
        int [] best_path_index = new int[num_steps];
        for(int t=0;t<num_steps;t++){
            int best = 0;
            for(int p=1;p<num_paths;p++){
                if(path_travel_times[p][t]<path_travel_times[best][t])
                    best = p;
            }
            best_path_index[t] = best;
        }
        return best_path_index;
    }

}
