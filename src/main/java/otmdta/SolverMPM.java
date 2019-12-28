package otmdta;

import java.util.List;
import java.util.Map;

/** Modified Projection Method **/
public class SolverMPM extends AbstractSolver {
    @Override
    public void update_assignment(Map<Long, List<Double>> travel_times) {

    }

    @Override
    public boolean stop_criterion() {
        return false;
    }
}
