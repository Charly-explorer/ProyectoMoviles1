package com.example.proyectomoviles1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
public class CustomAdapterInventario extends BaseAdapter{
    private Context context;
    private ArrayList<Inventario> lista;

    public CustomAdapterInventario( Context context, ArrayList<Inventario> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.lista.get(position).getIdInv();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.listview_inventario, null);
        }

        TextView txtNombreProducto = convertView.findViewById(R.id.textViewNombreProd);
        TextView txtCodeProducto = convertView.findViewById(R.id.textViewCodeInv);
        TextView txtExistencia = convertView.findViewById(R.id.textViewExistenciaInv);

        Inventario item = this.lista.get(position);

        txtNombreProducto.setText(item.getNombreProducto());
        txtCodeProducto.setText(String.valueOf(item.getCodigoProducto()));
        txtExistencia.setText(String.valueOf(item.getExistencia()));

        return convertView;
    }

    public void updateList(ArrayList<Inventario> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }
}
