import api.OTM;
import api.info.SubnetworkInfo;
import error.OTMException;
import org.junit.Test;
import otmdta.VIProblem;
import otmdta.Utils;
import otmdta.solver.AbstractSolver;
import otmdta.solver.SolverMSA;
import output.AbstractOutput;
import output.PathTravelTimeWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class Tests {

    private OTM otm;
    private String configfile = "config/seven_links.xml";
    private float time_horizon = 3600f;
    private float sample_dt = 30f;

    @Test
    public void test_project_onto_simples(){

        Random rand = new Random();
        int n = 300;
        double z = 1d;
        List<Double> betahat = new ArrayList<>();
        for(int i=0;i<n;i++)
            betahat.add(rand.nextDouble()*5d);

        List<Double> beta = Utils.project_onto_simplex(betahat,z);

        assertEquals(beta.stream().mapToDouble(x->x).sum(),z,0.01);
        assertTrue(beta.stream().allMatch(x->x>=0d));
    }

//    @Test
//    public void load_and_run_otm(){
//        try {
//            load_and_request();
//
//            // Run .................................
//            otm.run(0,time_horizon);
//
//            // Print output .........................
//            for(AbstractOutput output :  otm.output.get_data()){
//                if(output instanceof PathTravelTimeWriter){
//                    PathTravelTimeWriter ptt = (PathTravelTimeWriter) output;
//                    List<Double> travel_times = ptt.get_travel_times_sec();
//                    System.out.println(travel_times);
//                }
//            }
//        } catch (OTMException e) {
//            e.printStackTrace();
//        }
//
//    }


//    @Test
//    public void build_vi_problem(){
//        try {
//            load_and_request();
//            VIProblem problem = new VIProblem(otm,time_horizon, sample_dt);
//            System.out.println(problem);
//        } catch (OTMException e) {
//            e.printStackTrace();
//        }
//    }

//    @Test
//    public void run_MSA(){
//        try {
//            load_and_request();
//            VIProblem problem = new VIProblem(otm,time_horizon, sample_dt);
//            AbstractSolver solver = new SolverMSA(problem);
//
//            solver.solve();
//
//        } catch (OTMException e) {
//            e.printStackTrace();
//        }
//    }


    ///////////////////////////////////////////
    // private
    ///////////////////////////////////////////



}
