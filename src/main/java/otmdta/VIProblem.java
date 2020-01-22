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
    public float dt;
    public int num_steps;
    public Set<ODPair> odpairs;

    public VIProblem(String configfile,float time_horizon,float sample_dt) throws Exception {

        this.configfile = configfile;
        this.time_horizon = time_horizon;
        this.sample_dt = sample_dt;

        // Get OD information from OTM ....
        OTM otm = new OTM();
        otm.load(configfile,true,false);

        // check single commodity
        if(otm.scenario().get_commodities().size()!=1)
            throw new Exception("The scenario must be single-commodity.");

        long commodity_id = otm.scenario.get_commodities().iterator().next().getId();

        List<ODInfo> odinfos = otm.scenario().get_od_info();

        if(odinfos==null || odinfos.isEmpty())
            throw new Exception("Empty ODInfos.");

        this.dt = odinfos.get(0).total_demand.getDt();
        this.num_steps = odinfos.get(0).total_demand.getValues().size();

        if(!odinfos.stream().map(x->x.total_demand).allMatch(z->z.getDt()==dt && z.getValues().size()==num_steps))
            throw new Exception("Non-uniform dt or num steps");

        this.odpairs = new HashSet<>();
        for(int index=0;index<odinfos.size();index++){
            ODInfo odinfo = odinfos.get(index);
            double [] total_demand_vph = new double[num_steps];
            for(int i=0;i<num_steps;i++)
                total_demand_vph[i] = odinfo.total_demand.get_value(i);
            odpairs.add(new ODPair(configfile,commodity_id,sample_dt,odinfo.origin_node_id,odinfo.destination_node_id,odinfo.subnetworks,total_demand_vph));
        }
    }

}
