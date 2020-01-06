package otmdta;

import error.OTMException;
import otmdta.data.ODMatrix;

public class VIProblem {

    public api.OTM otm;
    public ODMatrix od_matrix;
    public float time_horizon;
    public float sample_dt;

    public VIProblem(api.OTM otm,float time_horizon,float sample_dt) throws OTMException {
        this.otm = otm;
        this.time_horizon = time_horizon;
        this.sample_dt = sample_dt;
        this.od_matrix = Utils.extract_od_matrix(otm);
    }

}
