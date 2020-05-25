package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import br.com.alura.technews.R
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.ui.activity.extensions.mostraErro
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import br.com.alura.technews.ui.viewmodel.factory.ListaNoticiasViewModelFactory
import kotlinx.android.synthetic.main.activity_lista_noticias.*

private const val TITULO_APPBAR = "Notícias"
private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Unable to load new news"

class ListaNoticiasActivity : AppCompatActivity() {

    private val adapter by lazy {
        ListaNoticiasAdapter(context = this)
    }

    private val viewModel by lazy {
        val respository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
        val factory = ListaNoticiasViewModelFactory(respository)
        val provider = ViewModelProviders.of(this, factory)
        provider.get(ListaNoticiasViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_noticias)
        title = TITULO_APPBAR
        configuraRecyclerView()
        configuraFabAdicionaNoticia()
    }

    override fun onResume() {
        super.onResume()
        buscaNoticias()
    }

    private fun configuraFabAdicionaNoticia() {
        activity_lista_noticias_fab_salva_noticia.setOnClickListener {
            abreFormularioModoCriacao()
        }
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(this, VERTICAL)
        activity_lista_noticias_recyclerview.addItemDecoration(divisor)
        activity_lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
        adapter.quandoItemClicado = this::abreVisualizadorNoticia
    }

    private fun buscaNoticias() {
        viewModel.getAllNews(
            onSuccess = {
                adapter.atualiza(it)
            },
            onFail = {
                mostraErro(MENSAGEM_FALHA_CARREGAR_NOTICIAS)
            }
        )
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(it: Noticia) {
        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, it.id)
        startActivity(intent)
    }

}
