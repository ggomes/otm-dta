package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;

/** Modified Projection Method **/
public class SolverMPM extends AbstractSolver {

    public SolverMPM(VIProblem problem){
        super(problem);
    }

    @Override
    public void initialize() throws OTMException{
        System.out.println("INITIALIZING SOLVER");
    }

    @Override
    public void solve()  throws OTMException {
        System.out.println("RUNNING MPM");
    }

}
