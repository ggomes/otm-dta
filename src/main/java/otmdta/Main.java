package otmdta;

import api.OTM;
import error.OTMException;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String [] args){

        String configfile = "asdgf0";
        float time_horizon = 3600f;
        float sampling_dt = 1200f;
        int num_steps = (int) (time_horizon/sampling_dt);
        long commodity_id = 0L;

        AbstractSolver solver = null;

        try {

            // Load OTM scenario
            OTM otm = new OTM();
            otm.load(configfile,true,false);

            // Initialize the solver
            solver.initialize(otm,num_steps);

            // loop
            int c=0;
            while(true){

                System.out.println(c++);

                // 1. Run the model and obtain the travel times on all paths
                otm.run(0f,time_horizon);

                // 2. Extract the travel times and arrange them.
                Map<Long,List<Double>> travel_times = new HashMap<>();
                for(AbstractOutput output : otm.output.get_data()){
                    PathTravelTimeWriter ttwriter = (PathTravelTimeWriter) output;
                    travel_times.put(ttwriter.get_path_id(),ttwriter.get_travel_times_sec());
                }

                // 3. Update the solver and retrieve the assignment
                solver.update_assignment(travel_times);
                Map<ODPair,PathDistribution> assignment = solver.get_current_assignment();

                // 4. Consider stopping
                if(solver.stop_criterion())
                    break;

                // 5. give it to the simulator
                for(Map.Entry<ODPair,PathDistribution> e : assignment.entrySet()){
                    ODPair odpair = e.getKey();
                    PathDistribution pathdist = e.getValue();
                    for(int p=0;p<odpair.path_ids.size();p++) {
                        long path_id = odpair.path_ids.get(p);
                        otm.scenario.set_demand_on_path_in_vph(path_id, commodity_id, 0f, sampling_dt, pathdist.props[p]);
                    }
                }

            }


        } catch (OTMException e) {
            e.printStackTrace();
        }

    }
}
