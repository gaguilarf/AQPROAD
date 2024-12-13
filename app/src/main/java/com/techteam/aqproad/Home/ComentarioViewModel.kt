package com.techteam.aqproad.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {
    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> get() = _comentarios

    private var fullList: List<Comentario> = listOf()

    fun loadComentarios(img: Int) {
        fullList = repository.getComentarios(img)
        _comentarios.value = fullList
    }
    fun addComentario(comentario: Comentario) {
        repository.addComentario(comentario)

        _comentarios.value = fullList
    }
}*/
/*
class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {
    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> get() = _comentarios

    private var fullList: List<Comentario> = listOf()

    fun loadComentarios(img: Int) {
        // Cargar los comentarios específicos para el ID proporcionado

        Log.d("ViewModelComment", "COMENTARIOS REPO COUNT: " + repository.getComentarios(img).size)

        fullList = repository.getComentarios(img)
        _comentarios.value = fullList

        Log.d("ViewModelComment", "COMENTARIOS COUNT: " + fullList.size)
        for (comentario in fullList){
            Log.d("ViewModelComment", "COMENTARIOS: " + comentario.autor)
        }
    }

    fun addComentario(comentario: Comentario) {
        // Agregar un nuevo comentario y actualizar el LiveData
        repository.addComentario(comentario)
        fullList = fullList + comentario
        _comentarios.value = fullList
    }
}*/
class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {
    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> get() = _comentarios

    fun loadComentarios(sitId: Int) {
        repository.getComentarios(sitId) { comentarioList ->
            _comentarios.postValue(comentarioList) // Actualiza el LiveData cuando los datos están listos
        }
    }
    fun addComentario(sitId: Int, comUsuNom: String, comDet: String, comFec: String) {
        repository.saveComment(sitId, comUsuNom, comDet, comFec) { result ->
            Log.d("ComentarioViewModel", result) // Muestra si se guardó correctamente
            // Después de guardar, recargar los comentarios
            loadComentarios(sitId)
        }
    }
}