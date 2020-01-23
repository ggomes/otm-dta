package otmdta;

import api.OTM;
import error.OTMException;
import otmdta.data.ODPair;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OTMManager {

    public OTM otm;
    public float time_horizon;
    public int num_steps;
    public long commodity_id;
    public float sample_dt;
    public Set<ODPair> odpairs;

    public OTMManager(String configfile,boolean validate,boolean jaxb_only,float time_horizon,float sample_dt) throws Exception {
        this.otm = new api.OTM();
        otm.load(configfile,validate,jaxb_only);
        this.time_horizon = time_horizon;
        this.sample_dt = sample_dt;

        // check single commodity
        if(otm.scenario().get_commodities().size()!=1)
            throw new Exception("The scenario must be single-commodity.");

        this.commodity_id = otm.scenario.get_commodities().iterator().next().getId();

        this.num_steps = (int) (time_horizon / sample_dt);

    }

    public void set_odpairs_and_request_output(Set<ODPair> odpairs, float sample_dt){
        this.odpairs = odpairs;

        // request outputs
        for(ODPair odpair : odpairs)
            for(long path_id : odpair.path_ids)
                otm.output.request_path_travel_time(path_id, sample_dt);
    }

    /** Run a simulation
     * 1. Takes the current assignment contained in each of its OD pairs and writes them to OTM
     * 2. Runs the simulation
     * 3. Extracts travel times for each path and write to OD pair
     */
    public void run_simulation() throws OTMException {

        // write assignment to OTM
        for(ODPair odpair : odpairs){
            for(int p=0;p<odpair.num_paths;p++){
                List<Double> list = new ArrayList<>();
                for(int t=0;t<odpair.num_steps;t++)
                    list.add(odpair.asgnmt[p][t]);
                otm.scenario().add_pathfull_demand(odpair.path_ids.get(p),commodity_id,0f,sample_dt,list);
            }
        }

        // Run simulation
        otm.run(0, time_horizon);

        // extract the result and write to OD pair
        for(ODPair odpair : odpairs){
            for(AbstractOutput output : otm.output.get_data()){
                PathTravelTimeWriter o = (PathTravelTimeWriter) output;
                int path_index = odpair.path_id_to_index.get(o.get_path_id());
                List<Double> ltt = o.get_travel_times_sec();
                for(int t=0;t<odpair.num_steps;t++)
                    odpair.tt[path_index][t] = ltt.get(t);
            }
        }

    }

}
