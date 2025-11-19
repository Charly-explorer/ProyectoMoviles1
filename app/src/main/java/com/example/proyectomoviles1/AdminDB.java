package com.example.proyectomoviles1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdminDB extends SQLiteOpenHelper {

    public AdminDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ========= TABLAS ========= //

        db.execSQL("CREATE TABLE Categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Productos (" +
                "codigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "idCategoria INTEGER NOT NULL, " +
                "descripcion TEXT, " +
                "FOREIGN KEY(idCategoria) REFERENCES Categorias(id))");

        db.execSQL("CREATE TABLE Inventario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigoProducto INTEGER NOT NULL, " +
                "existencias INTEGER NOT NULL, " +
                "estado TINYINT NOT NULL DEFAULT 1, " +
                "FOREIGN KEY(codigoProducto) REFERENCES Productos(codigo))");

        db.execSQL("CREATE TABLE Usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellido TEXT, " +
                "apellido2 TEXT, " +
                "correo TEXT, " +
                "contrasena TEXT, " +
                "esAdmin TINYINT NOT NULL)");

        db.execSQL("CREATE TABLE Proveedores (" +
                "codigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "correo TEXT, " +
                "telefono TEXT)");

        db.execSQL("CREATE TABLE MovimientosInventario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idInventario INTEGER NOT NULL, " +
                "idUsuario INTEGER NOT NULL, " +
                "codigoProveedor INTEGER, " +
                "fecha DATE NOT NULL, " +
                "movimiento TEXT NOT NULL, " +
                "detalle TEXT, " +
                "FOREIGN KEY(idInventario) REFERENCES Inventario(id), " +
                "FOREIGN KEY(idUsuario) REFERENCES Usuarios(id), " +
                "FOREIGN KEY(codigoProveedor) REFERENCES Proveedores(codigo))");

        // ========= TRIGGERS ========= //

        // 1️⃣ Trigger: Si las existencias bajan a 0 → estado = 0 (no mostrar)
        db.execSQL("CREATE TRIGGER trg_inventario_cero " +
                "AFTER UPDATE ON Inventario " +
                "FOR EACH ROW " +
                "WHEN NEW.existencias <= 0 " +
                "BEGIN " +
                "   UPDATE Inventario SET estado = 0 WHERE id = NEW.id; " +
                "END;");

        // 2️⃣ (Opcional) Si las existencias suben a más de 0 → estado = 1
        db.execSQL("CREATE TRIGGER trg_inventario_disponible " +
                "AFTER UPDATE ON Inventario " +
                "FOR EACH ROW " +
                "WHEN NEW.existencias > 0 " +
                "BEGIN " +
                "   UPDATE Inventario SET estado = 1 WHERE id = NEW.id; " +
                "END;");


        db.execSQL("INSERT INTO Usuarios (nombre, apellido, apellido2, correo, contrasena, esAdmin) " +
                "VALUES ('Admin', '', '', 'admin@admin.com', 'admin123', 1)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        onCreate(db);
    }

    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT P.codigo, P.nombre, P.descripcion, C.nombre FROM Productos P INNER JOIN Categorias C ON C.id = P.idCategoria",null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(new Producto(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public ArrayList<Inventario> obtenerInventario() {
        ArrayList<com.example.proyectomoviles1.Inventario> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT Inv.id, Inv.codigoProducto, P.nombre,Inv.existencias, Inv.estado FROM Inventario Inv INNER JOIN Productos P ON P.codigo = Inv.codigoProducto WHERE Inv.estado = 1",null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(new com.example.proyectomoviles1.Inventario(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        parseTinyitToBool(cursor.getInt(4))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean parseTinyitToBool(int num){
        if (num ==1) return true;
        return  false;
    }

    public Integer loginUsuario(String correo, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT esAdmin FROM Usuarios WHERE correo = ? AND contrasena = ? LIMIT 1",
                new String[]{correo, contrasena}
        );
        Integer esAdmin = null;
        if (cursor.moveToFirst()) {
            esAdmin = cursor.getInt(0); // 1 pa admin, 0 para usuario normal
        }
        cursor.close();
        return esAdmin;  // no exite el user o la password o estan incorrectas
    }

    public void guardarOActualizarInventario(int codigoProducto, int existencias, boolean estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cInv = db.rawQuery(
                "SELECT id FROM Inventario WHERE codigoProducto = ?",
                new String[]{ String.valueOf(codigoProducto) }
        );

        ContentValues valuesInv = new ContentValues();
        valuesInv.put("codigoProducto", codigoProducto);
        valuesInv.put("existencias", existencias);
        valuesInv.put("estado", estado ? 1 : 0);

        if (cInv.moveToFirst()) {
            db.update(
                    "Inventario",
                    valuesInv,
                    "codigoProducto = ?",
                    new String[]{ String.valueOf(codigoProducto) }
            );
        } else {
            db.insert("Inventario", null, valuesInv);
        }

        cInv.close();
    }
    public void guardarOActualizarProducto(int codigoProducto, String nombre, int idCategoria, String descripcion ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cInv = db.rawQuery(
                "SELECT codigo FROM Productos WHERE codigo = ?",
                new String[]{ String.valueOf(codigoProducto) }
        );

        ContentValues valuesInv = new ContentValues();
        valuesInv.put("nombre", nombre);
        valuesInv.put("idCategoria", idCategoria);
        valuesInv.put("descripcion", descripcion);

        if (cInv.moveToFirst()) {
            db.update(
                    "Productos",
                    valuesInv,
                    "codigo = ?",
                    new String[]{ String.valueOf(codigoProducto) }
            );
        } else {
            db.insert("Productos", null, valuesInv);
        }

        cInv.close();
    }

    public void desactivarInventarioPorId(int idInventario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("estado", 0);
        db.update(
                "Inventario",
                values,
                "id = ?",
                new String[]{ String.valueOf(idInventario) }
        );
    }
}



