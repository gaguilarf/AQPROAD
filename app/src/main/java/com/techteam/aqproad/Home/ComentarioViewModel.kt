package com.techteam.aqproad.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {
    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> get() = _comentarios

    private var fullList: List<Comentario> = listOf()

    fun loadComentarios() {
        fullList = repository.getComentarios()
        _comentarios.value = fullList
    }
    fun addComentario(comentario: Comentario) {
        repository.addComentario(comentario)
        loadComentarios()
    }
}