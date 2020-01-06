package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;

/** Extra Projection Method **/
public class SolverEPM extends AbstractSolver {

    public SolverEPM(VIProblem problem){
        super(problem);
    }

    @Override
    public void initialize() throws OTMException{
        System.out.println("INITIALIZING SOLVER");
    }

    @Override
    public void solve() throws OTMException {
        System.out.println("RUNNING EPM");
    }

}
