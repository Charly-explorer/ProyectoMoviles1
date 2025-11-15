package com.example.proyectomoviles1;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        onCreate(db);
    }
}



