package pabloromeromontero.r.demosfunciones.ejemplosTipos

import android.database.Cursor
import org.json.JSONObject


public class TipoNuevaIncidenciaRef(
    var idReporteIncidencia: Int = 0,
    var idReferencia: Int = 0,
    var Marca: String = "",
    var Sabor: String = "",
    var Formato1: String = "",
    var AKA: String = "",
    var cantidad: Int = 0,
    var fecha: String = ""
) {


    constructor(so: SoapObject) : this() {
        this.idReporteIncidencia = so.getPropety(0).toString.toInt
        this.idReferencia = so.getProperty(1).toString.toInt
        this.Marca = so.getProperty(2).toString
        this.Sabor = so.getProperty(3).toString
        this.Formato1 = so.getProperty(4).toString
        this.AKA = so.getProperty(5).toString
        this.cantidad = so.getProperty(6).toString.toInt
        this.fecha = so.getProperty(7).toString

    }

    constructor(cu: Cursor) : this() {
        this.idReporteIncidencia = cu.getInt(0)
        this.idReferencia = cu.getInt(1)
        this.Marca = cu.getString(2)
        this.Sabor = cu.getString(3)
        this.Formato1 = cu.getString(4)
        this.AKA = cu.getString(5)
        this.cantidad = cu.getInt(6)
        this.fecha = cu.getString(7)

    }

    constructor(o: JSONObject) : this(
        idReporteIncidencia = o.optInt("idReporteIncidencia"),
        idReferencia = o.optInt("idReferencia"),
        Marca = o.optString("marca"),
        Sabor = o.optString("sabor"),
        Formato1 = o.optString("formato1"),
        AKA = o.optString("AKA"),
        cantidad = o.optInt("cantidad"),
        fecha = o.optString("fecha")
    )

    fun toJson() = JSONObject().apply {
        put("idReporteIncidencia", idReporteIncidencia)
        put("idReferencia", idReferencia)
        put("Marca", Marca)
        put("Sabor", Sabor)
        put("Formato1", Formato1)
        put("AKA", AKA)
        put("cantidad", cantidad)
        put("fecha", fecha)

    }.toString()

    override fun toString(): String {
        return "TipoNuevaIncidenciaRef(idReporteIncidencia=$idReporteIncidencia, idReferencia=$idReferencia, Marca='$Marca', Sabor='$Sabor', Formato1='$Formato1', AKA='$AKA', cantidad=$cantidad, fecha='$fecha')"
    }


}