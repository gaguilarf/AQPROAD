package com.techteam.aqproad.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {
    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> get() = _comentarios

    private var fullList: List<Comentario> = listOf()  // Guardamos la lista completa

    fun loadComentarios() {
        fullList = repository.getComentarios()  // Cargar los datos completos
        _comentarios.value = fullList
    }
    fun addComentario(comentario: Comentario) {
        repository.addComentario(comentario)
        loadComentarios()  // Volver a cargar la lista con el nuevo comentario
    }
}