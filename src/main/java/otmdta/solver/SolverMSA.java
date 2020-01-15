package otmdta.solver;

import error.OTMException;
import otmdta.Utils;
import otmdta.VIProblem;
import otmdta.data.Assignment;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Method of Successive Averages **/
public class SolverMSA extends AbstractSolver {

    public Assignment curr;
    public Assignment prev;

    public SolverMSA(VIProblem problem){
        super(problem);
    }

    @Override
    public void initialize() throws OTMException {
        curr = Utils.get_random_assignment(problem.od_matrix);
        prev = curr.clone();
    }


    @Override
    public void solve() throws OTMException {
        System.out.println("RUNNING MSA");

        // Initialize the solver
        initialize();

        api.OTM otm = problem.otm;
        long commodity_id = otm.scenario().get_commodities().iterator().next().id;

        // loop
        int c=0;
        while(true){

            System.out.println(c++);

            // 1. Run the model and obtain the travel times on all paths
            System.out.println("\t1");
            otm.run(0f,problem.time_horizon);

            // 2. Extract the travel times and arrange them.
            System.out.println("\t2");
            Map<Long, List<Double>> travel_times = new HashMap<>();
            for(AbstractOutput output : otm.output.get_data()){
                PathTravelTimeWriter ttwriter = (PathTravelTimeWriter) output;
                travel_times.put(ttwriter.get_path_id(),ttwriter.get_travel_times_sec());
            }

            // 3. Improvement step
            System.out.println("\t3");


            // 4. Consider stopping
            System.out.println("\t4");
            if(stop_criterion(c))
                break;

            // 5. convert distribution to path demands
            System.out.println("\t5");
            Map<Long,List<Double>> path_demands = problem.od_matrix.assignment2demands(curr);

            // 6. give it to the simulator
            System.out.println("\t6");
            for(Map.Entry<Long,List<Double>> e : path_demands.entrySet())
                otm.scenario().add_pathfull_demand(e.getKey(),commodity_id,0f,problem.sample_dt,e.getValue());

        }


    }


    public boolean stop_criterion(int iteration){
        return iteration>10;
    }

}
