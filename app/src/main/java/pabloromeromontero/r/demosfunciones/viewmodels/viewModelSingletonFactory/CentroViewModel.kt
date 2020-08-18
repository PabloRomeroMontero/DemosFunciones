package com.procter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.fragment.app.Fragment
import com.procter.db.*

import com.procter.db.TipoUsuario

class CentroViewModel: ViewModel() {


    //region [2. Variables]
    var fragmentCargado = Fragment()
    var listaUsuarios = ArrayList<TipoUsuario>()
    var usuarioElegido = TipoUsuario()
    var listaCentros = ArrayList<TipoCentro>()
    var listaCentrosUsuarioAMayores = ArrayList<TipoCentro>()
    var listaCentrosUsuarioAMayoresFiltrado = ArrayList<TipoCentro>()
    var ubicacionSelected = TipoCodDesc()
    var tipoEspacioAniadir = TipoCodDesc()

    var centroActual = TipoCentro()
    var listaTodasRef = ArrayList<TipoReferencia>()
    var listaRefReportadas = ArrayList<TipoReferencia>()
    var listaEspaciosReportados : ArrayList<ArrayList<TipoReferencia>> = ArrayList()
    var espacioAEditar = ArrayList<TipoReferencia>()

    var listaReporteIncidencias = ArrayList<TipoReporteIncidencia>()
    var listaReportePromociones = ArrayList<TipoPromocion>()
    var promoAReportar = TipoPromocion()
    var folletoElegido = TipoFolleto()
    var centroFolletoElegido = TipoCentroFolleto()


    //ListaTipos...
    var listaTipoMarcas = ArrayList<TipoCodDesc>()
    var listaTipoEspacios = ArrayList<TipoCodDesc>()
    var listaTipoEspaciosLineaCajas = ArrayList<TipoCodDesc>()
    var listaTipoEspaciosNOLineaCajas = ArrayList<TipoCodDesc>()
    var listaTipoUbicaciones = ArrayList<TipoCodDesc>()
    var listaTipoMotivoRoturas = ArrayList<TipoCodDesc>()
    var listaTipoMotivoNoReposicion = ArrayList<TipoCodDesc>()
    var listaTipoMotivoSinBalizaje = ArrayList<TipoCodDesc>()
    var listaTiposEstadoIncidencia = ArrayList<TipoCodDesc>()
    var listaTipoIncidencias = ArrayList<TipoCodDesc>()
    var listaTipoAccionIncidencias = ArrayList<TipoCodDesc>()
    var listaTipoEstadoPromo = ArrayList<TipoCodDesc>()
    var listaTipoMotivoNoPromo = ArrayList<TipoCodDesc>()
    var listaTipoExtensiones = ArrayList<TipoCodDesc>()
    var listaMotivoNoEspacioPromo = ArrayList<TipoCodDesc>()
    var preguntasCentroFolletoReporte =ArrayList<TipoPreguntaFolletoReporte>()
    var secciones = ArrayList<ArrayList<TipoPreguntaFolletoReporte>>()
    var listaFotosHechas = ArrayList<Int>()



    //Fotos...
    var categoriaFoto = "1"
    var detalleFoto = "0"
    var photoPAth = ""
    var photoIncidenciaPath = ""


    //endregion


    //region [5. Métodos y funciones]



    //endregiones


}


