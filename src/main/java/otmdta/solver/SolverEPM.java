package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;

/** Extra Projection Method **/
public class SolverEPM extends AbstractSolver {


    public SolverEPM(VIProblem problem) {
        super(problem);
    }

    @Override
    public void initialize_for_od_pair(ODPair odpair) throws OTMException {

    }

    @Override
    public double advance_for_od_pair(ODPair odpair, long max_iterations) throws OTMException {
        return 0;
    }
}
