package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.List;

/** Method of Successive Averages **/
public class SolverMSA extends AbstractSolver {

    double [][] curr;
    double [][] prev;

    public SolverMSA(VIProblem problem){
        super(problem);
    }

    @Override
    public void initialize_for_od_pair(ODPair odpair) throws OTMException {
        this.curr = odpair.get_random_assignment();
        odpair.set_assignment(curr);
    }

    @Override
    public double advance_for_od_pair(ODPair odpair,long advance_max_iterations) throws OTMException {

        // loop
        double k=0;
        while(true) {

            System.out.println(k++);

            // record previous
            prev = curr.clone();

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
            double s = 1/k;
            double sbar = 1-s;
            double [][] x_new = odpair.get_assignment();
            double error = 0d;

            for(int p=0;p<odpair.num_paths;p++) {
                for (int t = 0; t < odpair.num_steps; t++) {
                    if (p == aon_path_index[t]) {
                        error += tt[p][t] * s * Math.abs(odpair.total_demand_vph[t] - curr[p][t]);
                        curr[p][t] = sbar * curr[p][t] + s * odpair.total_demand_vph[t];
                    } else {
                        error += tt[p][t] * s * curr[p][t];
                        curr[p][t] = sbar * curr[p][t];
                        error += tt[p][t] * (curr[p][t] - prev[p][t]);
                    }
                }
            }

            // Stop criterion
            if(error<max_error)
                break;

            if(k>=advance_max_iterations)
                break;

            // apply the assignment
            odpair.set_assignment(curr);

        }

        return 0;
    }


    public boolean stop_criterion(int iteration){

        /**
         # Calculating the error
         current_cost_vector = np.asarray(current_path_costs.vector_path_costs())

         if x_assignment_vector is None: x_assignment_vector = np.asarray(assignment.vector_assignment())


         y_assignment_vector = np.asarray(y_assignment.vector_assignment())



         error = round(np.abs(np.dot(current_cost_vector, y_assignment_vector - x_assignment_vector)/
         np.dot(y_assignment_vector,current_cost_vector)),4)


         if prev_error == -1 or prev_error > error:
         prev_error = error
         assignment_vector_to_return = copy(x_assignment_vector)

         if error < stop:
         if display == 1: print "MSA Stop with error: ", error
         return assignment, x_assignment_vector, sim_time, comm_time

         **/

        return iteration>10;
    }

}
