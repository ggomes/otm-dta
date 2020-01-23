package otmdta;

import api.info.ODInfo;
import otmdta.data.ODPair;
import otmdta.solver.AbstractSolver;
import otmdta.solver.SolverMSA;

import java.util.*;

public class RunnerSerial {

    public static void main(String [] args){

        String configfile = "config/seven_links.xml";
        float time_horizon = 3600;
        float sample_dt = 60;
        int num_solvers = 2;        // equal to number of processes in distributed run

        List<AbstractSolver> solvers = new ArrayList<>();

        long iteration_count = 0;
        final long max_iterations = 100;
        final int advance_max_iterations = 100;
        final double max_error = 10d;

        try {

            /** root process ............................ **/

            // create global otm runner
            OTMManager global_otm = new OTMManager(configfile,false,true,time_horizon,sample_dt);

            // Build OD pairs
            Set<ODPair> all_odpairs = Utils.extract_od_pairs_from_otm_api(global_otm,sample_dt)

            // request output
            global_otm.set_odpairs_and_request_output(problem.odpairs,problem.sample_dt);
            /** .......................................... **/

            // create solvers
            for(int i=0;i<num_solvers;i++)
                solvers.add(new SolverMSA(problem));

            /** root process ............................ **/
            // split od pairs amongst solvers
            Iterator<ODPair> it = problem.odpairs.iterator();
            while(it.hasNext())
                for(int i=0;i<num_solvers;i++)
                    if(it.hasNext())
                        solvers.get(i).add_odpair(it.next());
            /** .......................................... **/

            // initialize solvers (initialize od pairs with random assignments)
            for(AbstractSolver solver : solvers)
                solver.initialize();

            /** root process ............................ **/
            // run the initial assignment globally
            global_otm.run_simulation(xxx)
            /** .......................................... **/

            // large loop
            while(true){

                // advance each solver
                for(AbstractSolver solver : solvers)
                    solver.advance(max_error, advance_max_iterations);

                // gather assignments
                Map<Long,double []> global_assignment = new HashMap<>();
                for(AbstractSolver solver : solvers){
                    for(ODPair odpair : solver.odpairs){
                        double [][] asgmt = solver.get_current_assignment_for_odpair(odpair);
                        for(int p=0;p<asgmt.length;p++){
                            long path_id = odpair.path_index_to_id(p);
                            global_assignment.put(path_id,asgmt[p]);
                        }
                    }
                }

                // run once with global assignment
                path_travel_times, total_error = run_simulation(global_assignment);

                // stop criterion
                if(total_error<max_error)
                    break;

                if(++iteration_count>max_iterations)
                    break;

                // Send global assignment to solvers
                for(AbstractSolver solver : solvers)
                    solver.set_global_assignment(global_assignment, path_travel_times);

            }

            // finalize
            for(AbstractSolver solver : solvers)
                solver.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
