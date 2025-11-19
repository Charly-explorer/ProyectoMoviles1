package com.example.proyectomoviles1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminDB extends SQLiteOpenHelper {

    // Incrementamos la versión para forzar la actualización de la BD
    private static final int DATABASE_VERSION = 2;
    private Context context;

    public AdminDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        // Ignoramos el parámetro 'version' y usamos nuestra constante para asegurar que todos usen la misma
        super(context, name, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


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


        db.execSQL("CREATE TRIGGER trg_inventario_cero " +
                "AFTER UPDATE ON Inventario " +
                "FOR EACH ROW " +
                "WHEN NEW.existencias <= 0 " +
                "BEGIN " +
                "   UPDATE Inventario SET estado = 0 WHERE id = NEW.id; " +
                "END;");

        db.execSQL("CREATE TRIGGER trg_inventario_disponible " +
                "AFTER UPDATE OF existencias ON Inventario " +
                "FOR EACH ROW " +
                "WHEN NEW.existencias > 0 " +
                "BEGIN " +
                "   UPDATE Inventario SET estado = 1 WHERE id = NEW.id; " +
                "END;");

        // Insertar usuario Admin por defecto
        db.execSQL("INSERT INTO Usuarios (nombre, apellido, apellido2, correo, contrasena, esAdmin) " +
                "VALUES ('Admin', '', '', 'admin@admin.com', 'admin123', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas antiguas si existen
        db.execSQL("DROP TABLE IF EXISTS MovimientosInventario");
        db.execSQL("DROP TABLE IF EXISTS Proveedores");
        db.execSQL("DROP TABLE IF EXISTS Inventario");
        db.execSQL("DROP TABLE IF EXISTS Productos");
        db.execSQL("DROP TABLE IF EXISTS Categorias");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        
        // Volver a crear todo
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

    public Inventario obtenerProductoInventario(int codigoPro) {
        Inventario pro = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Inv.id, Inv.codigoProducto, P.nombre, Inv.existencias, Inv.estado " +
                        "FROM Inventario Inv " +
                        "INNER JOIN Productos P ON P.codigo = Inv.codigoProducto " +
                        "WHERE Inv.codigoProducto = ?",
                new String[]{ String.valueOf(codigoPro) }
        );

        if (cursor.moveToFirst()) {
            pro = new Inventario(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    parseTinyitToBool(cursor.getInt(4))
            );
        }

        cursor.close();
        return pro;
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
            esAdmin = cursor.getInt(0);
        }
        cursor.close();
        return esAdmin;
    }

    public long insertarUsuario(String nombre, String apellido, String apellido2,
                                String correo, String contrasena, int esAdmin) {

        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("apellido2", apellido2);
        values.put("correo", correo);
        values.put("contrasena", contrasena);
        values.put("esAdmin", esAdmin);
        return db.insert("Usuarios", null, values);
    }


    public void guardarOActualizarInventario(int codigoProducto, int existencias, boolean estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        long idInventario = -1;

        // 1. Verificar si ya existe en inventario
        Cursor cInv = db.rawQuery(
                "SELECT id FROM Inventario WHERE codigoProducto = ?",
                new String[]{ String.valueOf(codigoProducto) }
        );

        ContentValues valuesInv = new ContentValues();
        valuesInv.put("codigoProducto", codigoProducto);
        valuesInv.put("existencias", existencias);
        valuesInv.put("estado", estado ? 1 : 0);

        boolean esNuevo = false;

        if (cInv.moveToFirst()) {
            // Actualizar
            idInventario = cInv.getLong(0);
            db.update(
                    "Inventario",
                    valuesInv,
                    "codigoProducto = ?",
                    new String[]{ String.valueOf(codigoProducto) }
            );
        } else {
            // Insertar
            esNuevo = true;
            idInventario = db.insert("Inventario", null, valuesInv);
        }
        cInv.close();

        // 2. Registrar el Movimiento en MovimientosInventario
        // NOTA: Asumimos usuario ID = 1 (Admin) porque no se pasa el ID del usuario logueado.
        if (idInventario != -1) {
            ContentValues valuesMov = new ContentValues();
            valuesMov.put("idInventario", idInventario);
            valuesMov.put("idUsuario", 1); // Usuario por defecto
            
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            valuesMov.put("fecha", fechaActual);
            
            if (esNuevo) {
                // Usar strings traducidos si es posible
                String mov = "ALTA";
                String det = "Inventario inicial: " + existencias;
                
                if (context != null) {
                    mov = context.getString(R.string.movimiento_alta);
                    det = context.getString(R.string.inventario_inicial) + existencias;
                }
                
                valuesMov.put("movimiento", mov);
                valuesMov.put("detalle", det);
            } else {
                String mov = "ACTUALIZACION";
                String det = "Ajuste de existencias a: " + existencias;

                if (context != null) {
                    mov = context.getString(R.string.movimiento_actualizacion);
                    det = context.getString(R.string.ajuste_existencias) + existencias;
                }

                valuesMov.put("movimiento", mov);
                valuesMov.put("detalle", det);
            }
            
            db.insert("MovimientosInventario", null, valuesMov);
        }
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
    public ArrayList<Movimiento> obtenerMovimientos() {
        ArrayList<Movimiento> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT M.id, P.nombre, M.fecha, M.movimiento, M.detalle " +
                "FROM MovimientosInventario M " +
                "INNER JOIN Inventario I ON M.idInventario = I.id " +
                "INNER JOIN Productos P ON I.codigoProducto = P.codigo " +
                "ORDER BY M.fecha DESC, M.id DESC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombreProducto = cursor.getString(1);
                String fecha = cursor.getString(2);
                String tipo = cursor.getString(3);
                String detalle = cursor.getString(4);

                Movimiento mov = new Movimiento(id, nombreProducto, fecha, tipo, detalle);
                lista.add(mov);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }
}
