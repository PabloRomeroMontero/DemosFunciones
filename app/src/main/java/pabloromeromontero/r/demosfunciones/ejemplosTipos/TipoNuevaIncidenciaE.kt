package pabloromeromontero.r.demosfunciones.ejemplosTipos

import android.database.Cursor
import org.json.JSONObject


public class TipoNuevaIncidenciaE(
    var IdEstadoIncidencia: Int = 0,
    var IdReporteIncidencia: Int = 0,
    var UsuarioMod: Int = 0,
    var Usuario: String = "",
    var FechaMod: String = "",
    var Estado: Int = 0,
    var DescEstado: String = "",
    var Observaciones: String = ""
) {


    constructor(so: SoapObject) : this() {
        this.IdEstadoIncidencia = so.getPropety(0).toString.toInt
        this.IdReporteIncidencia = so.getProperty(1).toString.toInt
        this.UsuarioMod = so.getProperty(2).toString.toInt
        this.Usuario = so.getProperty(3).toString
        this.FechaMod = so.getProperty(4).toString
        this.Estado = so.getProperty(5).toString.toInt
        this.DescEstado = so.getProperty(6).toString
        this.Observaciones = so.getProperty(7).toString

    }

    constructor(cu: Cursor) : this() {
        this.IdEstadoIncidencia = cu.getInt(0)
        this.IdReporteIncidencia = cu.getInt(1)
        this.UsuarioMod = cu.getInt(2)
        this.Usuario = cu.getString(3)
        this.FechaMod = cu.getString(4)
        this.Estado = cu.getInt(5)
        this.DescEstado = cu.getString(6)
        this.Observaciones = cu.getString(6)
    }

    constructor(o: JSONObject) : this(
        IdEstadoIncidencia = o.optInt("IdEstadoIncidencia"),
        IdReporteIncidencia = o.optInt("IdReporteIncidencia"),
        UsuarioMod = o.optInt("UsuarioMod"),
        Usuario = o.optString("Usuario"),
        FechaMod = o.optString("FechaMod"),
        Estado = o.optInt("Estado"),
        DescEstado = o.optString("DescEstado"),
        Observaciones = o.optString("Observaciones")
    )

    fun toJson() = JSONObject().apply {
        put("IdEstadoIncidencia", IdEstadoIncidencia)
        put("IdReporteIncidencia", IdReporteIncidencia)
        put("UsuarioMod", UsuarioMod)
        put("Usuario", Usuario)
        put("FechaMod", FechaMod)
        put("Estado", Estado)
        put("DescEstado", DescEstado)
        put("Observaciones", Observaciones)

    }.toString()

    override fun toString(): String {
        return "TipoNuevaIncidenciaE(IdEstadoIncidencia=$IdEstadoIncidencia, IdReporteIncidencia=$IdReporteIncidencia, UsuarioMod=$UsuarioMod, Usuario='$Usuario', FechaMod='$FechaMod', Estado=$Estado, DescEstado='$DescEstado', Observaciones='$Observaciones')"
    }


}