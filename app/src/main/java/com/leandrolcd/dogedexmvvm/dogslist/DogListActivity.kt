package com.leandrolcd.dogedexmvvm.dogslist


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.leandrolcd.dogedexmvvm.databinding.ActivityDogListBinding
import com.leandrolcd.dogedexmvvm.dogdetail.DogDetailActivity
import com.leandrolcd.dogedexmvvm.dogdetail.DogDetailActivity.Companion.DOG_KEY
private const val GRID_SPAN_COLUMN = 3
class DogListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: DogListViewModel by viewModels();
        super.onCreate(savedInstanceState)
        val binding =
            ActivityDogListBinding.inflate(layoutInflater)  // crea el Binding entre el xml y el activity
        setContentView(binding.root) //establece el binding con el xml

        val pbLoading = binding.pbLoading
        val recycler = binding.rvDogs  // llama a la referencia el Recycler View
        recycler.layoutManager = GridLayoutManager(this, GRID_SPAN_COLUMN)
        val adapter = DogAdapter(); // crea el adapter
        adapter.setOnItemClickListener {
            //pasar el obj dog al detalle
            val intent = Intent(this, DogDetailActivity::class.java) // crea el intent
            intent.putExtra(DOG_KEY, it) // agrega el obj dog con key
            startActivity(intent) //lanza el nuevo evento
        }
        recycler.adapter = adapter; // setea el adapter el recycler view
        viewModel.dogList.observe(this) {
            adapter.submitList(it)
        }

        viewModel.status.observe(this) { status ->
            when (status) {
                is UiStatus.Error -> {
                    pbLoading.visibility = View.GONE
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                }
                is UiStatus.Loading -> pbLoading.visibility = View.VISIBLE
                is UiStatus.Success -> pbLoading.visibility = View.GONE
            }

        }

    }
}