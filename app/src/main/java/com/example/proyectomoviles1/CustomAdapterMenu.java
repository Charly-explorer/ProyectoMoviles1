package com.example.proyectomoviles1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
public class CustomAdapterMenu extends BaseAdapter{
    private Context context;
    private ArrayList<OpcionMenu> opciones;

    public CustomAdapterMenu(Context context, ArrayList<OpcionMenu> opciones) {
        this.context = context;
        this.opciones = opciones;
    }

    @Override
    public int getCount() {
        return opciones.size();
    }

    @Override
    public Object getItem(int position) {
        return opciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_menu, parent, false);
        }

        ImageView imgIcono = convertView.findViewById(R.id.imgIcono);
        TextView txtTitulo = convertView.findViewById(R.id.txtTitulo);

        OpcionMenu opcion = opciones.get(position);

        imgIcono.setImageResource(opcion.getIconoResId());
        txtTitulo.setText(opcion.getTitulo());

        return convertView;
    }
}
