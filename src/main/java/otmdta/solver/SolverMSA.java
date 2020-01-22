package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.List;

/** Method of Successive Averages **/
public class SolverMSA extends AbstractSolver {


    public SolverMSA(VIProblem problem){
        super(problem);
    }

    @Override
    public void initialize_for_od_pair(final ODPair odpair) throws OTMException {

        odpair.curr = new double[odpair.num_paths][odpair.num_steps];

        if(odpair.num_paths==1)
            odpair.curr[0] = odpair.total_demand_vph;
        else
            odpair.curr = odpair.get_random_assignment();

        odpair.set_assignment(odpair.curr);
    }

    @Override
    public double advance_for_od_pair(final ODPair odpair,long advance_max_iterations) throws OTMException {

        // trivial case
        if(odpair.num_paths==1)
            return 0d;

        // loop
        int k=0;
        while(true) {

            System.out.println(k++);

            // record previous
//            odpair.prev = odpair.curr.clone();

            // 1. Run the model and obtain the travel times on all paths
            odpair.run_simulation(problem.time_horizon);

            // 2. Extract the travel times and arrange them.
            double [][] tt = odpair.get_assignment();
            for(AbstractOutput output : odpair.otm.output.get_data()){
                PathTravelTimeWriter o = (PathTravelTimeWriter) output;
                int path_index = odpair.path_id_to_index.get(o.get_path_id());
                List<Double> ltt = o.get_travel_times_sec();
                for(int t=0;t<ltt.size();t++)
                    tt[path_index][t] = ltt.get(t);
            }

            // 3. All-or-nothing assignment
            int [] aon_path_index = odpair.AllOrNothing(tt);

            // 5. Update
            double s = 1.0/((double)k);
            double sbar = 1-s;
            double error = 0d;
            for(int p=0;p<odpair.num_paths;p++) {
                for (int t = 0; t < odpair.num_steps; t++) {
                    if (p == aon_path_index[t]) {
                        error += tt[p][t] * s * Math.abs(odpair.total_demand_vph[t] - odpair.curr[p][t]);
                        odpair.curr[p][t] = sbar * odpair.curr[p][t] + s * odpair.total_demand_vph[t];
                    } else {
                        error += tt[p][t] * s * odpair.curr[p][t];
                        odpair.curr[p][t] = sbar * odpair.curr[p][t];
                    }
                }
            }

            // Stop criterion
            if(error<max_error)
                break;

            if(k>=advance_max_iterations)
                break;

            // apply the assignment
            odpair.set_assignment(odpair.curr);

        }

        return 0;
    }

}
