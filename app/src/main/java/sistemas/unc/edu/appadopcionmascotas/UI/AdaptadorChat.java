package sistemas.unc.edu.appadopcionmascotas.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Mensaje> lista;
    private int miIdUsuario; // Para saber qué mensajes envié yo

    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;

    public AdaptadorChat(List<Mensaje> lista, int miIdUsuario) {
        this.lista = lista;
        this.miIdUsuario = miIdUsuario;
    }

    //NUEVO PARA AGREGAR
    @Override
    public int getItemViewType(int position) {
        // Si el id_emisor es igual a mi ID, es enviado
        if (lista.get(position).getIdEmisor() == miIdUsuario) {
            return TIPO_ENVIADO;
        } else {
            return TIPO_RECIBIDO;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_ENVIADO) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_enviado, parent, false);
            return new EnviadoViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_recibido, parent, false);
            return new RecibidoViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje m = lista.get(position);

        if (holder instanceof EnviadoViewHolder) {
            ((EnviadoViewHolder) holder).txt.setText(m.getTexto());
        } else if (holder instanceof RecibidoViewHolder) {
            ((RecibidoViewHolder) holder).txt.setText(m.getTexto());
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class EnviadoViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        EnviadoViewHolder(View v) {
            super(v);
            txt = v.findViewById(R.id.tvMensajeEnviado);
        }
    }

    static class RecibidoViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        RecibidoViewHolder(View v) {
            super(v);
            txt = v.findViewById(R.id.tvMensajeRecibido);
        }
    }

    public class ViewHolder {
    }
}
