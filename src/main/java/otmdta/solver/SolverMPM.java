package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;
import otmdta.data.ODPair;

/** Modified Projection Method **/
public class SolverMPM extends AbstractSolver {

    public SolverMPM(VIProblem problem) {
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
