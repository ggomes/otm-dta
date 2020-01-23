package otmdta;

import error.OTMException;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<Double> project_onto_simplex(List<Double> betahat, double z){

        if(betahat.isEmpty())
            return new ArrayList<>();

        if(z<=0)
            return zero_array(betahat.size());

        List<Double> mu = new ArrayList(betahat);
        Collections.sort(mu);
        double cs = mu.stream().mapToDouble(x->x).sum();
        double pi = Double.NaN;
        for(int ii=0;ii<mu.size();ii++){
            pi = (cs-z)/(mu.size()-ii);
            if(pi<mu.get(ii))
                break;
            cs -= mu.get(ii);
        }

        double theta = pi;
        return betahat.stream().map(b-> Math.max(0,b-theta)).collect(Collectors.toList());
    }

    public static List<Double> zero_array(int n){
        List<Double> x = new ArrayList<>();
        for(int i=0;i<n;i++)
            x.add(0d);
        return x;
    }

    ////////////////////////////
    // OTM
    ////////////////////////////

    public static api.OTM load_otm(String configfile,boolean validate,boolean jaxb_only) throws OTMException {
        api.OTM otm = new api.OTM();
        otm.load(configfile,validate,jaxb_only);
        return otm;
    }


}
