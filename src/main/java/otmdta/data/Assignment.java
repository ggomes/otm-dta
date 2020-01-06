package otmdta.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assignment {

    public Map<ODPair,double [][]> pair2dist;

    public Assignment(){};

    public Assignment(List<ODPair> odpairs, int num_steps){
        pair2dist = new HashMap<>();
        for(ODPair pair : odpairs)
            pair2dist.put(pair,new double[pair.path_ids.size()][num_steps]);
    }

    public Assignment clone(){
        Assignment that = new Assignment();
        that.pair2dist = new HashMap<>();
        for(Map.Entry<ODPair,double [][]> e : this.pair2dist.entrySet())
            that.pair2dist.put(e.getKey(),e.getValue().clone());
        return that;
    }

}
