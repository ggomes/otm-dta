package otmdta;

import api.OTM;
import api.info.ODInfo;
import otmdta.data.ODPair;

import java.util.*;

public class VIProblem {

    public final String configfile;
    public final float time_horizon;      // in seconds
    public final float sample_dt;         // in seconds

    // OD demands
    public int num_steps;
    public Set<ODPair> odpairs;

    public VIProblem(String configfile,float time_horizon,float sample_dt) throws Exception {

        this.configfile = configfile;
        this.time_horizon = time_horizon;
        this.sample_dt = sample_dt;

        // load otm jaxb only
        OTM otm = Utils.load_otm(configfile,false,false);

        // check single commodity
        if(otm.scenario().get_commodities().size()!=1)
            throw new Exception("The scenario must be single-commodity.");
        long commodity_id = otm.scenario.get_commodities().iterator().next().getId();

        // Build OD pairs
        List<ODInfo> odinfos = otm.scenario().get_od_info();

        if(odinfos==null || odinfos.isEmpty())
            throw new Exception("No OD info.");

        this.num_steps = (int) (time_horizon / sample_dt);

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
            odpairs.add(new ODPair(configfile,commodity_id,sample_dt,odinfo.origin_node_id,odinfo.destination_node_id,odinfo.subnetworks,total_demand_vph));
        }
    }

}
