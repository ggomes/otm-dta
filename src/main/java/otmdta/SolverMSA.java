package otmdta;

import java.util.List;
import java.util.Map;

/** Method of Successive Averages **/
public class SolverMSA extends AbstractSolver {

    @Override
    public void update_assignment(Map<Long, List<Double>> travel_times) {

    }

    @Override
    public boolean stop_criterion() {
        return false;
    }
}
