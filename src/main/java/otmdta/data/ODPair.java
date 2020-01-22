package otmdta.data;

import api.OTM;
import api.info.SubnetworkInfo;
import error.OTMException;

import java.util.*;

public class ODPair {

    public final OTM otm;
    public final long commodity_id;
    public final float sample_dt;

    public final long origin_node_id;
    public final long destination_node_id;
    public final double[] total_demand_vph;   // over time

    public final List<Long> path_ids;
    public final Map<Long,Integer> path_id_to_index;
    public final int num_steps;
    public final int num_paths;

    public ODPair(String configfile, long commodity_id,float sample_dt,long origin_node_id, long destination_node_id, List<SubnetworkInfo> subnetworks,double [] total_demand_vph) throws OTMException {
        this.origin_node_id = origin_node_id;
        this.destination_node_id = destination_node_id;
        this.commodity_id = commodity_id;
        this.sample_dt = sample_dt;
        this.otm = load_otm(configfile,sample_dt);

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

    public void set_assignment(double [][] assgn) throws OTMException {
        for(int p=0;p<num_paths;p++){
            List<Double> list = new ArrayList<>();
            for(int t=0;t<num_steps;t++)
                list.add(assgn[p][t]);
            otm.scenario().add_pathfull_demand(path_ids.get(p),commodity_id,0f,sample_dt,list);
        }
    }

    public void run_simulation(float time_horizon) throws OTMException {
        otm.run(0f,time_horizon);
    }

    public double [][] get_assignment(){
        return new double[path_ids.size()][total_demand_vph.length];
    }

    public double [][] get_random_assignment(){

        Random random = new Random(System.currentTimeMillis());

        double [][] assignment = this.get_assignment();

        int num_steps = total_demand_vph.length;
        int num_paths = path_ids.size();
        double [] props = new double[num_paths];
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
                assignment[p][t] = total_demand_vph[t] * temp[p] / sum;

        }

        return assignment;
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

    private static OTM load_otm(String configfile, float sample_dt) throws OTMException {

        // Load OTM scenario
        OTM otm = new OTM();
        otm.load(configfile,false,false);

        // Output requests .....................
        Set<SubnetworkInfo> paths = otm.scenario.get_subnetworks();
        for(SubnetworkInfo path : paths)
            otm.output.request_path_travel_time(path.id, sample_dt);
        return otm;
    }

}
