package otmdta.data;

import api.info.Profile1DInfo;
import error.OTMException;
import otmdta.Utils;
import utils.OTMUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ODMatrix {
    public float dt;
    public int num_steps;
    public int num_pairs;
    public List<ODPair> odpairs;
    public Double[][] total_demand_vph;

    public ODMatrix(float dt, int num_steps, int num_pairs){
        this.dt = dt;
        this.num_steps = num_steps;
        this.num_pairs = num_pairs;
        this.odpairs = new ArrayList<>();
        this.total_demand_vph = new Double[num_pairs][num_steps];
    }

    public void add_pair(ODPair pair, Profile1DInfo total_demand) throws OTMException {
        if(odpairs.size()==num_pairs)
            throw new OTMException("Full.");
        if(num_steps!=total_demand.getValues().size())
            throw new OTMException("Wrong size");
        int index = odpairs.size();
        odpairs.add(pair);
        for(int i=0;i<num_steps;i++)
            total_demand_vph[index][i] = total_demand.get_value(i);
    }

    public Map<Long,List<Double>> assignment2demands(Assignment assignment){
        Map<Long,List<Double>> X = new HashMap<>();
        for(int i=0;i<odpairs.size();i++){
            ODPair odpair = odpairs.get(i);
            Double[] demand_vph = total_demand_vph[i];
            double [][] dist = assignment.pair2dist.get(odpair);
            for(int p=0;p<odpair.path_ids.size();p++)
                X.put( odpair.path_ids.get(p) , Utils.prod(dist[p],demand_vph));
        }
        return X;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0;i<num_pairs;i++)
            str += String.format("(%d,%d) [%s]\n",odpairs.get(i).origin_node_id,odpairs.get(i).destination_node_id,
                    OTMUtils.format_delim(total_demand_vph[i],","));
        return str;
    }


}
