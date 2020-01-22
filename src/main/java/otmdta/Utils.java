package otmdta;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {




    public static List<Double> prod(double[] A,Double [] B){
        List<Double> x = new ArrayList<>();
        for(int i=0;i<A.length;i++)
            x.add(A[i]*B[i]);
        return x;
    }

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

}
