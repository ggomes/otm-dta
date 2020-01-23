package otmdta;

import api.info.ODInfo;
import otmdta.data.ODPair;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public Set<ODPair> extract_od_pairs_from_otm_api(OTMManager otm,float sample_dt) throws Exception {
        List<ODInfo> odinfos = otm.otm.scenario().get_od_info();

        if(odinfos==null || odinfos.isEmpty())
            throw new Exception("No OD info.");

        Set<ODPair> odpairs = new HashSet<>();
        for(int index=0;index<odinfos.size();index++){
            ODInfo odinfo = odinfos.get(index);
            double [] total_demand_vph = new double[otm.num_steps];
            int num_given_demand_samples = odinfo.total_demand.num_values();
            float dem_dt = odinfo.total_demand.getDt();
            for(int step=0;step<otm.num_steps;step++) {
                float time = step * sample_dt;
                int k = Math.min( (int)(time / dem_dt) , num_given_demand_samples-1);
                total_demand_vph[step] = odinfo.total_demand.get_value(k) * 3600d;
            }
            odpairs.add(new ODPair(odinfo.origin_node_id,odinfo.destination_node_id,odinfo.subnetworks,total_demand_vph));
        }
        return odpairs;
    }

    public static List<Double> project_onto_simplex(List<Double> betahat, double z){

        if(betahat.isEmpty())
            return new ArrayList<>();

        if(z<=0)
            return zero_array(betahat.size());

        List<Double> mu = new ArrayList(betahat);
        Collections.sort(mu);
        double cs = mu.stream().mapToDouble(x->x).sum();
        double pi = Double.NaN;
        for(int ii=0;ii<mu.size();ii++){
            pi = (cs-z)/(mu.size()-ii);
            if(pi<mu.get(ii))
                break;
            cs -= mu.get(ii);
        }

        double theta = pi;
        return betahat.stream().map(b-> Math.max(0,b-theta)).collect(Collectors.toList());
    }

    public static List<Double> zero_array(int n){
        List<Double> x = new ArrayList<>();
        for(int i=0;i<n;i++)
            x.add(0d);
        return x;
    }

}
