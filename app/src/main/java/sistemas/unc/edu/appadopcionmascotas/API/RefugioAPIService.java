package sistemas.unc.edu.appadopcionmascotas.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sistemas.unc.edu.appadopcionmascotas.Model.RefugioAPI;

public interface RefugioAPIService {
    @GET("AmnerMarin/b2e96efbbd7d164c5e4d814a2471c076/raw/3b0e9c54beda8e3db1e02cf46da53f4f12be4b56/refugios.json")
    Call<List<RefugioAPI>> obtenerRefugiosExternos();

}
