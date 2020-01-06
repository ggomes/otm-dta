package otmdta.solver;

import error.OTMException;
import otmdta.VIProblem;

public abstract class AbstractSolver {

    public VIProblem problem;
    public abstract void initialize() throws OTMException;
    public abstract void solve() throws OTMException;

    public AbstractSolver(VIProblem problem){
        this.problem = problem;
    }
}
