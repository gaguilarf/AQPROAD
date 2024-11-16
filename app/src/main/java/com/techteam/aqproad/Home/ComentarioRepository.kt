package com.techteam.aqproad.Home

class ComentarioRepository() {

    private val comentarios = mutableListOf<Comentario>()

    init {
        comentarios.add(Comentario(id = 1, autor = "Ana Tapia", contenido = "Es un museo vivo. Ya que el molino hoy en dia es funcional con su Riachuelo que incrementa la belleza de sus jardines, el sitio se adapta para hacer películas y ... Más"))
        comentarios.add(Comentario(id = 2, autor = "Luis Soriano Torres", contenido = "Lindo lugar en la campiña de Arequipa,se puede llevar algo de comer y hacer un picnic,le molino es histórico y vale la pena conocerlo."))
    }

    fun getComentarios(): List<Comentario> {

        return comentarios
    }

    fun addComentario(comentario: Comentario) {
        comentarios.add(comentario)
    }
}