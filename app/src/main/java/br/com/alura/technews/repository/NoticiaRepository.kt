package br.com.alura.technews.repository

import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {

    fun getAllNews(
        onSuccess: (List<Noticia>) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        getOffline(onSuccess)
        getOnline(onSuccess, onFail)
    }

    fun save(
        news: Noticia,
        onSuccess: (noticiaNova: Noticia) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        saveOnline(news, onSuccess, onFail)
    }

    fun remove(
        news: Noticia,
        onSuccess: () -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        deleteOnline(news, onSuccess, onFail)
    }

    fun edit(
        news: Noticia,
        onSuccess: (noticiaEditada: Noticia) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        editOnline(news, onSuccess, onFail)
    }

    fun getById(
        newsId: Long,
        onSuccess: (noticiaEncontrada: Noticia?) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.buscaPorId(newsId)
        }, quandoFinaliza = onSuccess)
            .execute()
    }

    private fun getOnline(
        onSuccess: (List<Noticia>) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    saveOffline(noticiasNovas, onSuccess)
                }
            }, quandoFalha = onFail
        )
    }

    private fun getOffline(onSuccess: (List<Noticia>) -> Unit) {
        BaseAsyncTask(quandoExecuta = {
            dao.buscaTodos()
        }, quandoFinaliza = onSuccess)
            .execute()
    }


    private fun saveOnline(
        news: Noticia,
        onSuccess: (noticiaNova: Noticia) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        webclient.salva(
            news,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    saveOffline(noticiaSalva, onSuccess)
                }
            }, quandoFalha = onFail
        )
    }

    private fun saveOffline(
        news: List<Noticia>,
        onSuccess: (newNews: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(news)
                dao.buscaTodos()
            }, quandoFinaliza = onSuccess
        ).execute()
    }

    private fun saveOffline(
        news: Noticia,
        onSuccess: (noticiaNova: Noticia) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(news)
            dao.buscaPorId(news.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                onSuccess(it)
            }
        }).execute()

    }

    private fun deleteOnline(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = {
                deleteOffline(noticia, quandoSucesso)
            },
            quandoFalha = quandoFalha
        )
    }


    private fun deleteOffline(
        news: Noticia,
        onSuccess: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.remove(news)
        }, quandoFinaliza = {
            onSuccess()
        }).execute()
    }

    private fun editOnline(
        news: Noticia,
        onSuccess: (noticiaEditada: Noticia) -> Unit,
        onFail: (erro: String?) -> Unit
    ) {
        webclient.edita(
            news.id, news,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let {
                    saveOffline(noticiaEditada, onSuccess)
                }
            }, quandoFalha = onFail
        )
    }

}
