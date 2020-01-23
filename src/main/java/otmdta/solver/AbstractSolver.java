package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSolver {

    public final VIProblem problem;
    public final long max_iterations = 100;

    public abstract void initialize_for_od_pair(final ODPair odpair) throws OTMException ;
    public abstract double advance_for_od_pair(final ODPair odpair,double max_error,long max_iterations) throws OTMException;
    public abstract void finalize_for_od_pair(ODPair odpair);

    public AbstractSolver(VIProblem problem){
        this.problem = problem;
    }

    public void solve(){

        try {
            // initialize
            for(ODPair odpair : problem.odpairs)
                initialize_for_od_pair(odpair);

            long it = 0;
            int advance_max_iterations = 100;
            double max_error = 10d;
            while(true){

                // advance for od pair, accumulate error
                double it_error = 0d;
                for(ODPair odpair : problem.odpairs)
                    it_error += advance_for_od_pair(odpair, max_error, advance_max_iterations);

                // stop criterion
                if(it_error<max_error)
                    break;

                if(it>max_iterations)
                    break;

                it++;
            }

            // finalize
            for(ODPair odpair : problem.odpairs)
                finalize_for_od_pair(odpair);

        } catch (OTMException e) {
            e.printStackTrace();
        }

    }


}
