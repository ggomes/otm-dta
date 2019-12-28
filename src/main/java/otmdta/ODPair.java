package otmdta;

import api.info.SubnetworkInfo;
import error.OTMException;

import java.util.List;
import java.util.stream.Collectors;

public class ODPair {

    public long origin_node_id;
    public long destination_node_id;
    public List<Long> path_ids;

    public ODPair(long origin_node_id, long destination_node_id, List<SubnetworkInfo> subnetworks) throws OTMException {
        this.origin_node_id = origin_node_id;
        this.destination_node_id = destination_node_id;

        if(subnetworks.stream().anyMatch(s->!s.is_path))
            throw new OTMException("Not a path!");

        this.path_ids = subnetworks.stream()
                .map(s->s.id)
                .collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


}
