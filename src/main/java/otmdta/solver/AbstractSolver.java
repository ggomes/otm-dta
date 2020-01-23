package otmdta.solver;

import otmdta.OTMManager;
import otmdta.VIProblem;
import otmdta.data.ODPair;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.*;

public abstract class AbstractSolver {

    public final VIProblem problem;

    public final OTMManager otm;

    // OD information
    public Set<ODPair> odpairs;
    public boolean has_nontrivial_ods;      // every od pair has a single path

    // abstract methods
    public abstract void close();

    ///////////////////////////////
    // construction and initialization
    ///////////////////////////////

    public AbstractSolver(VIProblem problem) throws Exception {
        this.problem = problem;
        this.odpairs = new HashSet<>();
        this.has_nontrivial_ods = false;
        this.otm = new OTMManager(problem.configfile,false,false,time_horizon,commodity_id,sample_dt);
    }

    public void add_odpair(ODPair odpair){
        odpairs.add(odpair);
        has_nontrivial_ods = has_nontrivial_ods || odpair.num_paths>1;
    }

    public void initialize() throws Exception {
        for(ODPair odpair : odpairs){
            odpair.initialize();

        // TODO SET INITIAL ASSIGNMENT
    }

    ///////////////////////////////
    // advance
    ///////////////////////////////

//    public double advance(double max_error,long max_iterations) {
//
//        if(!has_nontrivial_ods){
//            // TODO TRIVIAL CASE
//            return Double.NaN;
//        }
//
//        // Assume the assignment has been updated with new information from other solvers.
//
//        // loop
//        int k=0;
//        double error;
//        while(true) {
//
//            // 1. Run the model and obtain the travel times on all paths
//            otm.run_simulation(problem.time_horizon);
//
//            // 2. Extract the travel times and arrange them.
//            double [][] tt = odpair.get_assignment();
//            for(AbstractOutput output : odpair.otm.output.get_data()){
//                PathTravelTimeWriter o = (PathTravelTimeWriter) output;
//                int path_index = odpair.path_id_to_index.get(o.get_path_id());
//                List<Double> ltt = o.get_travel_times_sec();
//                for(int t=0;t<odpair.num_steps;t++)
//                    tt[path_index][t] = ltt.get(t);
//            }
//
//            // 3. All-or-nothing assignment
//            int [] aon_path_index = odpair.AllOrNothing(tt);
//
//            // 5. Update
//            k++;
//            double s = 1.0/((double)k);
//            double sbar = 1-s;
//            error = 0d;
//            for(int p=0;p<odpair.num_paths;p++) {
//                for (int t = 0; t < odpair.num_steps; t++) {
//                    if (p == aon_path_index[t]) {
//                        error += tt[p][t] * s * Math.abs(odpair.total_demand_vph[t] - odpair.curr[p][t]);
//                        odpair.curr[p][t] = sbar * odpair.curr[p][t] + s * odpair.total_demand_vph[t];
//                    } else {
//                        error += tt[p][t] * s * odpair.curr[p][t];
//                        odpair.curr[p][t] = sbar * odpair.curr[p][t];
//                    }
//                }
//            }
//
//            // Stop criterion
//            if(error<max_error)
//                break;
//
//            if(k>=advance_max_iterations)
//                break;
//
//            // apply the assignment
//            odpair.set_assignment(odpair.curr);
//
//        }
//
//        odpair.error.add(error);
//
//        return error;
//
//    }


}
