package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSolver {

    public final VIProblem problem;
    public final double max_error = 0.1d;
    public final long max_iterations = 1000;

    public abstract void initialize_for_od_pair(final ODPair odpair) throws OTMException ;
    public abstract double advance_for_od_pair(final ODPair odpair,long max_iterations) throws OTMException;

    public AbstractSolver(VIProblem problem){
        this.problem = problem;
    }

    public void solve(){

        try {
            // initialize
            for(ODPair odpair : problem.odpairs)
                initialize_for_od_pair(odpair);

            List<Double> error = new ArrayList<>();
            long it = 0;
            int advance_max_iterations = 20;
            while(true){

                // advance for od pair, accumulate error
                double it_error = 0d;
                for(ODPair odpair : problem.odpairs)
                    it_error += advance_for_od_pair(odpair, advance_max_iterations);

                error.add(it_error);

                // stop criterion
                if(it_error<max_error)
                    break;

                if(it>max_iterations)
                    break;

                it++;
            }
        } catch (OTMException e) {
            e.printStackTrace();
        }
    };


}
