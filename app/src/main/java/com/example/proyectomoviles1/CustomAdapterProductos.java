package com.example.proyectomoviles1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapterProductos extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Producto> lista;

    public CustomAdapterProductos(Context ctx, ArrayList<Producto> lista) {
        this.context = ctx;
        this.lista = lista;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Producto getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_productos, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView txtCode = convertView.findViewById(R.id.textViewCode);
        TextView txtNombre = convertView.findViewById(R.id.textViewNombre);
        TextView txtDes = convertView.findViewById(R.id.textViewDes);

        Producto item = lista.get(position);

        txtCode.setText(String.valueOf(item.getCode()));
        txtNombre.setText(item.getNombre());
        txtDes.setText(item.getDescripcion());
        byte[] imagen = item.getImagen();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
        imageView.setImageBitmap(bitmap);


        return convertView;
    }
    public void updateList(ArrayList<Producto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }
}

