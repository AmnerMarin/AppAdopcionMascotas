package sistemas.unc.edu.appadopcionmascotas.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sistemas.unc.edu.appadopcionmascotas.Model.RefugioAPI;

public interface RefugioAPIService {
    @GET("v1/46204901-069b-4ee2-bfe5-768ec37f3ab6")
    Call<List<RefugioAPI>> obtenerRefugiosExternos();
}
