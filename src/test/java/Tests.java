import api.OTM;
import api.info.SubnetworkInfo;
import error.OTMException;
import org.junit.Test;
import otmdta.VIProblem;
import otmdta.data.ODMatrix;
import otmdta.Utils;
import otmdta.solver.AbstractSolver;
import otmdta.solver.SolverMSA;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.List;
import java.util.Set;

public class Tests {

    private OTM otm;
    private String configfile = "config/seven_links.xml";
    private float time_horizon = 3600f;
    private float sample_dt = 30f;

    @Test
    public void load_and_run_otm(){
        try {
            load_and_request();

            // Run .................................
            otm.run(0,time_horizon);

            // Print output .........................
            for(AbstractOutput output :  otm.output.get_data()){
                if(output instanceof PathTravelTimeWriter){
                    PathTravelTimeWriter ptt = (PathTravelTimeWriter) output;
                    List<Double> travel_times = ptt.get_travel_times_sec();
                    System.out.println(travel_times);
                }
            }
        } catch (OTMException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void extract_od_matrix(){
        try {
            load_and_request();
            ODMatrix od_matrix = Utils.extract_od_matrix(otm);
            System.out.println(od_matrix);
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void build_vi_problem(){
        try {
            load_and_request();
            VIProblem problem = new VIProblem(otm,time_horizon, sample_dt);
            System.out.println(problem);
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void run_MSA(){
        try {
            load_and_request();
            VIProblem problem = new VIProblem(otm,time_horizon, sample_dt);
            AbstractSolver solver = new SolverMSA(problem);

            solver.solve();

        } catch (OTMException e) {
            e.printStackTrace();
        }
    }


    ///////////////////////////////////////////
    // private
    ///////////////////////////////////////////

    private void load_and_request() throws OTMException{

        // Load OTM scenario
        otm = new OTM();
        otm.load(configfile,true,false);

        // Output requests .....................
        Set<SubnetworkInfo> paths = otm.scenario.get_subnetworks();
        for(SubnetworkInfo path : paths)
            otm.output.request_path_travel_time(path.id, sample_dt);

    }

}
