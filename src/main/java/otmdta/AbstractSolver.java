package otmdta;

import api.OTM;
import api.info.ODInfo;
import api.info.Profile1DInfo;
import api.info.SubnetworkInfo;
import error.OTMException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSolver {

    public Set<ODPair> odpairs;
    public Map<ODPair,PathDistribution> previous_assignment;
    public Map<ODPair,PathDistribution> current_assignment;

    public abstract void update_assignment(Map<Long, List<Double>> travel_times);
    public abstract boolean stop_criterion();

    public void initialize(OTM otm,int num_steps) throws OTMException {

        for( ODInfo x : otm.scenario.get_od_info() )
            odpairs.add( new ODPair(x.origin_node_id,x.destination_node_id,x.subnetworks) );

        previous_assignment = new HashMap<>();
        current_assignment = new HashMap<>();
        for(ODPair odpair : odpairs){
            previous_assignment.put(odpair,new PathDistribution(odpair.path_ids,num_steps));
            current_assignment.put(odpair,new PathDistribution(odpair.path_ids,num_steps));
        }

    }

    public Map<ODPair,PathDistribution> get_current_assignment(){
        return current_assignment;
    }

}
