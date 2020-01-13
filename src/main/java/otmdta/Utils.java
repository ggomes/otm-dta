package otmdta;

import api.info.ODInfo;
import error.OTMException;
import otmdta.data.Assignment;
import otmdta.data.ODMatrix;
import otmdta.data.ODPair;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static ODMatrix extract_od_matrix(api.OTM otm) throws OTMException {

        // check single commodity
        if(otm.scenario().get_commodities().size()!=1)
            throw new OTMException("The scenario must be single-commodity.");

        List<ODInfo> odinfos = otm.scenario().get_od_info();

        if(odinfos==null || odinfos.isEmpty())
            throw new OTMException("Empty ODInfos.");

        float dt = odinfos.get(0).total_demand.getDt();
        int num_steps = odinfos.get(0).total_demand.getValues().size();
        int num_pairs = odinfos.size();

        if(!odinfos.stream().map(x->x.total_demand).allMatch(z->z.getDt()==dt && z.getValues().size()==num_steps))
            throw new OTMException("Non-uniform dt or num steps");

        ODMatrix odmatrix = new ODMatrix(dt,num_steps,num_pairs);
        for(ODInfo odinfo : odinfos){
            ODPair pair = new ODPair(odinfo.origin_node_id,odinfo.destination_node_id,odinfo.subnetworks);
            odmatrix.add_pair(pair,odinfo.total_demand);
        }

        return odmatrix;
    }

    public static Assignment get_random_assignment(ODMatrix odmatrix){

        Random random = new Random(System.currentTimeMillis());

        int num_steps = odmatrix.num_steps;
        Assignment assignment = new Assignment(odmatrix.odpairs,num_steps);

        for(ODPair pair : odmatrix.odpairs){

            int num_paths = pair.path_ids.size();
            double [][] props = new double[num_paths][num_steps];
            double [] temp = new double[num_paths];
            double sum;

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

            assignment.pair2dist.put(pair,props);
        }

        return assignment;
    }

    public static List<Double> prod(double[] A,Double [] B){
        List<Double> x = new ArrayList<>();
        for(int i=0;i<A.length;i++)
            x.add(A[i]*B[i]);
        return x;
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
