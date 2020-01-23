package otmdta;

import api.OTM;
import api.info.ODInfo;
import otmdta.data.ODPair;

import java.util.*;

public class VIProblem {



    // OD demands
    public int num_steps;
    public Set<ODPair> odpairs;

    public VIProblem() throws Exception {

        // Build OD pairs
        List<ODInfo> odinfos = otm.scenario().get_od_info();

        if(odinfos==null || odinfos.isEmpty())
            throw new Exception("No OD info.");


        this.odpairs = new HashSet<>();
        for(int index=0;index<odinfos.size();index++){
            ODInfo odinfo = odinfos.get(index);
            double [] total_demand_vph = new double[num_steps];
            int num_given_demand_samples = odinfo.total_demand.num_values();
            float dem_dt = odinfo.total_demand.getDt();
            for(int step=0;step<num_steps;step++) {
                float time = step * sample_dt;
                int k = Math.min( (int)(time / dem_dt) , num_given_demand_samples-1);
                total_demand_vph[step] = odinfo.total_demand.get_value(k) * 3600d;
            }
            odpairs.add(new ODPair(odinfo.origin_node_id,odinfo.destination_node_id,odinfo.subnetworks,total_demand_vph));
        }
    }

}
