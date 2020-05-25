package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository

class ListaNoticiasViewModel(private val respository: NoticiaRepository) : ViewModel() {

    fun getAllNews(onSuccess: (newsListNew: List<Noticia>) -> Unit, onFail: (errorMessage: String?) -> Unit) {
        respository.getAllNews(onSuccess, onFail)
    }
}