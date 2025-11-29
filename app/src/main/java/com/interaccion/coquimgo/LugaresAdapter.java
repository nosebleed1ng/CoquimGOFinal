package com.interaccion.coquimgo;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LugaresAdapter extends RecyclerView.Adapter<LugaresAdapter.LugarViewHolder> {

    private final List<LugarItem> lista;
    private final OnLugarClickListener listener;
    private int lastPosition = -1; // para animación escalonada

    public interface OnLugarClickListener {
        void onClick(LugarItem item);
    }

    public LugaresAdapter(List<LugarItem> lista, OnLugarClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LugarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugar_recycler, parent, false);
        return new LugarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LugarViewHolder holder, int position) {
        LugarItem item = lista.get(position);

        // Título según idioma
        holder.txtNombre.setText(holder.itemView.getContext().getString(item.getTituloResId()));
        // Imagen
        holder.imgLugar.setImageResource(item.getImagenResId());
        // Subtítulo traducible
        holder.txtSubtitulo.setText(
                holder.itemView.getContext().getString(R.string.subtitulo_lugar)
        );

        // Animación de entrada
        runEnterAnimation(holder.itemView, holder.accentBar, position);

        holder.cardLugar.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void runEnterAnimation(View itemView, View accentBar, int position) {
        lastPosition = position;

        itemView.setTranslationX(-200f);
        itemView.setAlpha(0f);
        itemView.setScaleX(0.9f);
        itemView.setScaleY(0.9f);

        accentBar.setScaleX(0f);

        itemView.animate()
                .translationX(0f)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(position * 60L)
                .setDuration(420)
                .setInterpolator(new OvershootInterpolator(1.15f))
                .start();

        accentBar.animate()
                .scaleX(1f)
                .setStartDelay(position * 60L + 180)
                .setDuration(350)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    static class LugarViewHolder extends RecyclerView.ViewHolder {

        CardView cardLugar;
        TextView txtNombre;
        TextView txtSubtitulo;
        ImageView imgLugar;
        View accentBar;

        public LugarViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLugar    = itemView.findViewById(R.id.cardLugar);
            txtNombre    = itemView.findViewById(R.id.txtNombre);
            txtSubtitulo = itemView.findViewById(R.id.txtSubtitulo);
            imgLugar     = itemView.findViewById(R.id.imgLugar);
            accentBar    = itemView.findViewById(R.id.accentBar);

            // Micro animación al tocar
            cardLugar.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(0.96f)
                                .scaleY(0.96f)
                                .setDuration(120)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start();
                        break;
                }
                return false; // deja que se dispare el onClick igual
            });
        }
    }
}

