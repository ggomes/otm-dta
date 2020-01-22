import error.OTMException;
import org.junit.Test;
import otmdta.VIProblem;
import otmdta.Utils;
import otmdta.data.ODPair;
import otmdta.solver.SolverMSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.*;

public class Tests {

    String configfile = "config/seven_links.xml";
    float time_horizon = 3600;
    float sample_dt = 60;

    @Test
    public void load_otm(){
        try {
            api.OTM otm = Utils.load_otm(configfile,true, false);
            assertNotNull(otm);
        } catch (OTMException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void build_vi_problem(){
        try {
            VIProblem problem = new VIProblem(configfile,time_horizon, sample_dt);
            assertNotNull(problem);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void run_otm(){
        try {
            api.OTM otm = Utils.load_otm(configfile,true, false);
            VIProblem problem = new VIProblem(configfile,time_horizon, sample_dt);
            ODPair odpair = problem.odpairs.iterator().next();
            odpair.run_simulation(time_horizon);
            assertNotNull(otm);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void test_project_onto_simplex(){

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

    @Test
    public void run_MSA(){
        try {
            VIProblem problem = new VIProblem(configfile,time_horizon, sample_dt);
            SolverMSA msa = new SolverMSA(problem);
            msa.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
